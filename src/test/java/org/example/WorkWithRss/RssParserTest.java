package org.example.WorkWithRss;

import org.example.Storage.NewsArticle;
import org.example.WorkWithRss.interfaces.IContentExtractor;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для RssParser.
 */
class RssParserTest {

    @TempDir
    Path tmp;

    /**
     * Генерим простой RSS-файл на диске и проверяем, что
     * RssParser корректно читает title, link, description, pubDate и category.
     */
    @Test
    void parse_handlesBasicFieldsAndCategory() throws Exception {
        String rss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <title>Test feed</title>
                <item>
                  <title>Заголовок 1</title>
                  <link>http://example.com/a</link>
                  <description><![CDATA[<p>Описание &amp; тест</p>]]></description>
                  <pubDate>Tue, 10 Jun 2025 12:00:00 GMT</pubDate>
                  <category>спорт</category>
                  <enclosure url="http://img.com/1.jpg" type="image/jpeg"/>
                </item>
                <item>
                  <title>Заголовок 2</title>
                  <link>http://example.com/news/b</link>
                  <description>Просто текст</description>
                  <!-- нет category -->
                </item>
              </channel>
            </rss>
            """;

        Path file = tmp.resolve("feed.xml");
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write(rss);
        }

        // создём парсер без внешнего экстрактора (null), чтобы он не пытался обогащать
        RssParser parser = new RssParser(null);
        List<NewsArticle> list = parser.parse(file.toUri().toString());

        assertEquals(2, list.size());

        NewsArticle a1 = list.get(0);
        assertEquals("Заголовок 1", a1.getTitle());
        assertEquals("http://example.com/a", a1.getLink());
        assertEquals("Описание & тест", a1.getDescription());
        // pubDate преобразовано в ZonedDateTime в вашей системе
        assertEquals(ZonedDateTime.ofInstant(a1.getReleaseDate().toInstant(), ZoneId.systemDefault()).getDayOfMonth(), 10);
        assertEquals("спорт", a1.getCategory());
        assertEquals(List.of("http://img.com/1.jpg"), a1.getImageUrl());

        NewsArticle a2 = list.get(1);
        assertEquals("Заголовок 2", a2.getTitle());
        assertEquals("Просто текст", a2.getDescription());
        // раз категории нет, берётся сегмент пути: "/news/b" → "news"
        assertEquals("news", a2.getCategory());
    }

    /**
     * Если передан extractor, то оно вызывается для content, а также
     * для изображений и видео (при instanceof JsoupContentExtractor),
     * и результаты добавляются в Article.
     */
    @Test
    void parse_withContentExtractor_enrichesContentImagesAndVideos() throws Exception {
        String rss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0"><channel><item>
              <title>T</title><link>http://ex.com/x</link>
              <description>Desc</description>
            </item></channel></rss>
            """;
        Path file = tmp.resolve("feed2.xml");
        Files.writeString(file, rss);

        @SuppressWarnings("unchecked")
        IContentExtractor mockExt = mock(IContentExtractor.class);
        when(mockExt.extract("http://ex.com/x")).thenReturn("FULL TEXT");
        // Упрощаем: не instanceof JsoupContentExtractor, значит images/video не добавляются
        RssParser parser = new RssParser(mockExt);

        List<NewsArticle> list = parser.parse(file.toUri().toString());
        assertEquals(1, list.size());
        NewsArticle a = list.get(0);
        assertEquals("FULL TEXT", a.getContent());
        // extractor instanceof JsoupContentExtractor? нет → imageUrl остаётся пустым
        assertTrue(a.getImageUrl().isEmpty());
        assertNull(a.getVideoUrl());
        verify(mockExt).extract("http://ex.com/x");
    }

    /**
     * При падении extractor.extract() — мы логим ошибку, но всё равно возвращаем статью.
     */
    @Test
    void parse_onExtractorException_stillReturnsArticle() throws Exception {
        String rss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0"><channel><item>
              <title>X</title><link>http://z.com/z</link>
            </item></channel></rss>
            """;
        Path file = tmp.resolve("feed3.xml");
        Files.writeString(file, rss);

        IContentExtractor badExt = mock(IContentExtractor.class);
        when(badExt.extract(anyString())).thenThrow(new IOException("fail"));

        RssParser parser = new RssParser(badExt);
        List<NewsArticle> list = parser.parse(file.toUri().toString());

        // тест проверяет: ошибки не выбрасываются, возвращается список из одного элемента
        assertEquals(1, list.size());
        NewsArticle a = list.get(0);
        assertEquals("X", a.getTitle());
        // при ошибке — content остаётся null или пустым
        assertTrue(a.getContent() == null || a.getContent().isEmpty());
    }
}
