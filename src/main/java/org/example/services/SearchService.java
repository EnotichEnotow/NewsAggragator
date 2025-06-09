package org.example.services;



import org.example.Storage.NewsArticle;
import org.example.filters.KeywordFilter;
import org.example.repository.NewsArticleRepository;

import java.util.List;

public class SearchService {
    private final NewsArticleRepository repo;
    public SearchService(NewsArticleRepository repo) { this.repo = repo; }

    /** Поиск по ключевым словам в title/description/content. */
    public List<NewsArticle> search(String keywords) throws Exception {
        return repo.findAll(new KeywordFilter(keywords));
    }
}
