package org.example.WorkWithRss;

import org.example.Storage.Source;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigSourceProviderTest {

    @Test
    void getSources_readsAllUrlsFromProperties() {
        // В src/test/resources/rss-sources.properties должно быть:
        // rss.sources=https://rssexport.rbc.ru/rbcnews/news/30/full.rss,https://www.france24.com/en/rss,https://meduza.io/rss/all
        ConfigSourceProvider provider = new ConfigSourceProvider();

        List<Source> list = provider.getSources();
        assertEquals(3, list.size(), "Должно быть прочитано ровно 3 источника");

        assertEquals("https://rssexport.rbc.ru/rbcnews/news/30/full.rss",
                list.get(0).getFeedUrl(),
                "Первый URL не совпал");

        assertEquals("https://www.france24.com/en/rss",
                list.get(1).getFeedUrl(),
                "Второй URL не совпал");

        assertEquals("https://meduza.io/rss/all",
                list.get(2).getFeedUrl(),
                "Третий URL не совпал");
    }
}
