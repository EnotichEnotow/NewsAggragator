package org.example.WorkWithRss.interfaces;

import java.util.List;
import org.example.Storage.NewsArticle;

/**
 * Унифицированный контракт для любых парсеров новостей.
 */
public interface IParser {
    /**
     * Спарсить одну RSS-ленту и вернуть список статей.
     *
     * @param feedUrl абсолютный URL ленты .rss / .xml
     */
    List<NewsArticle> parseFeed(String feedUrl) throws Exception;
}
