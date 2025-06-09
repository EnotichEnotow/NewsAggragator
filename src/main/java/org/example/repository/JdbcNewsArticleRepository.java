package org.example.repository;

import org.example.Storage.NewsArticle;
import org.example.dao.JdbcNewsArticleDao;   // уже реализован
import org.example.filters.NewsFilter;

import java.util.List;
import java.util.stream.Collectors;


//Адаптер к существующему DAO: тянем *все* из БД, затем применяем фильтр
public class JdbcNewsArticleRepository implements NewsArticleRepository {

    private final JdbcNewsArticleDao dao;
    public JdbcNewsArticleRepository(JdbcNewsArticleDao dao) { this.dao = dao; }

    @Override public List<NewsArticle> findAll(NewsFilter filter) throws Exception {
        return dao.findAll()  // тянем из БД
                .stream()
                .filter(filter::matches)   // in-memory фильтрация
                .collect(Collectors.toList());
    }
}
