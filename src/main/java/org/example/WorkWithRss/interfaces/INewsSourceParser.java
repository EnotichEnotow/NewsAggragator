package org.example.WorkWithRss.interfaces;

import org.example.Storage.NewsArticle;
import java.util.List;

/** Общее API для любого источника новостей. */
public interface INewsSourceParser {

    /** Вернуть свежие статьи (заголовок, дата, контент…). */
    List<NewsArticle> fetchLatest() throws Exception;

    /** Идентификатор источника (URL или название). */
    String getSourceId();
}