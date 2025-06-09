package org.example.WorkWithRss;

import org.example.Storage.NewsArticle;
import org.example.Storage.Source;
import org.example.WorkWithRss.interfaces.IParser;
import org.example.WorkWithRss.interfaces.ISourceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RssServiceTest {

    @AfterEach
    void tearDown() {
        // JUnit сам не завершает пул; чистим, чтобы не мешать другим тестам
        // (можно вызвать service.shutdown() внутри тестов)
    }

    @Test
    void fetchAll_parallelParsingAndDeduplication() throws Exception {
        // Мокаем провайдер двух фидов
        ISourceProvider sp = mock(ISourceProvider.class);
        when(sp.getSources()).thenReturn(List.of(
                new Source("a", "http://feed1", "ru"),
                new Source("b", "http://feed2", "ru")
        ));

        // Мокаем парсер: из первого фида 2 статьи, из второго — одну дубликатную по link
        IParser parser = mock(IParser.class);

        NewsArticle a1 = new NewsArticle(); a1.setLink("L1");
        NewsArticle a2 = new NewsArticle(); a2.setLink("L2");
        NewsArticle dup = new NewsArticle(); dup.setLink("L1");

        when(parser.parseFeed("http://feed1")).thenReturn(List.of(a1, a2));
        when(parser.parseFeed("http://feed2")).thenReturn(List.of(dup));

        // создаём сервис с пулом из 2 потоков
        RssService service = new RssService(2, parser, sp);

        List<NewsArticle> all = service.fetchAll();

        // Должно получить ровно две уникальные ссылки: L1 и L2
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(a -> "L1".equals(a.getLink())));
        assertTrue(all.stream().anyMatch(a -> "L2".equals(a.getLink())));

        service.shutdown();
    }

    @Test
    void fetchAll_handlesParserExceptionsAndContinues() throws Exception {
        ISourceProvider sp = mock(ISourceProvider.class);
        when(sp.getSources()).thenReturn(List.of(
                new Source("a", "http://good", "ru"),
                new Source("b", "http://bad",  "ru")
        ));

        IParser parser = mock(IParser.class);
        NewsArticle ok = new NewsArticle(); ok.setLink("OK");
        when(parser.parseFeed("http://good")).thenReturn(List.of(ok));
        when(parser.parseFeed("http://bad"))
                .thenThrow(new RuntimeException("fail"));

        RssService service = new RssService(2, parser, sp);

        // Должен вернуть только ту статью, что из "http://good"
        List<NewsArticle> all = service.fetchAll();
        assertEquals(1, all.size());
        assertEquals("OK", all.get(0).getLink());

        service.shutdown();
    }

    @Test
    void shutdown_terminatesThreadPool() throws Exception {
        ISourceProvider sp = mock(ISourceProvider.class);
        when(sp.getSources()).thenReturn(List.of());
        IParser parser = mock(IParser.class);

        RssService service = new RssService(1, parser, sp);
        service.shutdown();

        // После shutdown пул не должен принимать новые задачи
        assertTrue(service.pool.isShutdown());
    }
}
