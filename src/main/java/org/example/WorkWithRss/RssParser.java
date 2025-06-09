package org.example.WorkWithRss;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.example.Storage.NewsArticle;
import org.example.WorkWithRss.interfaces.IContentExtractor;
import org.example.WorkWithRss.interfaces.IParser;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

// Парсер RSS-лент. Использует библиотеку ROME для чтения XML и Jsoup для обработки HTML-контента.

public class RssParser implements IParser {

    private final IContentExtractor extractor;

    public RssParser(IContentExtractor extractor) {
        this.extractor = extractor;  // Сервис для вытягивания полного текста и медиа
    }

    @Override
    public List<NewsArticle> parseFeed(String feedUrl) throws Exception {
        return parse(feedUrl);
    }

    public List<NewsArticle> parse(String feedUrl) throws Exception {
        List<NewsArticle> result = new ArrayList<>();

        SyndFeedInput input = new SyndFeedInput();
        try (XmlReader reader = new XmlReader(new URL(feedUrl))) {
            SyndFeed feed = input.build(reader);  // Чтение RSS-ленты

            for (SyndEntry entry : feed.getEntries()) {
                NewsArticle a = new NewsArticle();

                // Заголовок и ссылка
                a.setTitle(entry.getTitle());
                a.setLink(entry.getLink());

                // Описание (очищаем от HTML)
                if (entry.getDescription() != null) {
                    String raw = entry.getDescription().getValue();
                    a.setDescription(Jsoup.parse(raw).text());
                }

                // Дата публикации
                Date pub = entry.getPublishedDate();
                a.setReleaseDate(pub != null
                        ? pub.toInstant().atZone(ZoneId.systemDefault())
                        : ZonedDateTime.now());  // если нет даты — текущее время

                // Категория: либо из тега <category>, либо из URL
                if (!entry.getCategories().isEmpty()) {
                    a.setCategory(entry.getCategories().get(0).getName());
                } else {
                    try {
                        String[] seg = URI.create(a.getLink()).getPath().split("/");
                        if (seg.length > 1 && !seg[1].isBlank())
                            a.setCategory(seg[1]);
                    } catch (Exception ignored) { }// нерабочая ссылка
                }

                // Картинки из enclosure в RSS
                List<String> images = new ArrayList<>();
                for (SyndEnclosure enc : entry.getEnclosures()) {
                    if (enc.getType() != null && enc.getType().startsWith("image"))
                        images.add(enc.getUrl());
                }

                // Получение полного текста и доп медиа со страницы
                if (extractor != null) {
                    try {
                        a.setContent(extractor.extract(a.getLink())); // основной текст со страницы
                        // Если используем JsoupExtractor, дополнительно подтягиваем картинки и видео
                        if (extractor instanceof JsoupContentExtractor je) {
                            images.addAll(je.extractImages(a.getLink()));
                            a.setVideoUrl(je.extractVideos(a.getLink()));
                        }
                    } catch (Exception ex) {
                        System.err.println("Enrich error " + a.getLink() + ": " + ex);
                    }
                }

                a.setImageUrl(images); // финальный список изображений
                result.add(a);  // добавляем статью в результат
            }
        }
        return result;
    }
}
