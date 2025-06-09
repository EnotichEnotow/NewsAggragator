package org.example.repository;

import org.example.Storage.NewsArticle;
import org.example.filters.NewsFilter;

import java.util.List;

public interface NewsArticleRepository {
    // Все статьи, удовлетворяющие фильтру.
    List<NewsArticle> findAll(NewsFilter filter) throws Exception;

    // Все статьи
    default List<NewsArticle> findAll() throws Exception {
        return findAll(a -> true);
    }
}
