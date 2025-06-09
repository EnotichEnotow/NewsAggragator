// src/main/java/org/example/dao/NewsArticleDao.java
package org.example.dao;

import org.example.Storage.NewsArticle;

import java.util.List;

public interface NewsArticleDao {
    void save(NewsArticle article);
    void saveAll(List<NewsArticle> articles);
    boolean existsByLink(String link);
    List<NewsArticle> findAll() throws Exception;


    List<String> fetchAllLinks();
    List<NewsArticle> deleteOlderThanDays(int days);
}
