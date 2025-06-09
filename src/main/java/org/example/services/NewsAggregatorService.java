package org.example.services;

import org.example.Storage.NewsArticle;
import org.example.WorkWithRss.interfaces.IRssService;
import org.example.dao.NewsArticleDao;
import org.example.util.ChangeLogger;

import java.util.List;

public class NewsAggregatorService {
    private final IRssService rssService;
    private final NewsArticleDao dao;
    private final SharedCountClientService shareClient;
    private final int retentionDays = 7;

    public NewsAggregatorService(
            IRssService rssService,
            NewsArticleDao dao,
            SharedCountClientService shareClient
    ) {
        // Инициализируем сервисы агрегатора
        this.rssService = rssService;
        this.dao = dao;
        this.shareClient = shareClient;
    }

    public void updateAll() throws Exception {
        // Загружаем новые статьи из RSS
        List<NewsArticle> articles = rssService.fetchAll();

        for (NewsArticle a : articles) {
            if (!dao.existsByLink(a.getLink())) {
                try {
                    // Получаем количество шарингов
                    long sc = shareClient.fetchShareCount(a.getLink());
                    a.setShareCount(sc);
                } catch (Exception e) {
                    a.setShareCount(0);
                }
                dao.save(a);
                ChangeLogger.logAdded(a);
            }
        }

        // Удаляем устаревшие статьи и записываем в логи
        List<NewsArticle> removed = dao.deleteOlderThanDays(retentionDays);
        for (NewsArticle a : removed) {
            ChangeLogger.logDeleted(a);
        }
    }

    public List<NewsArticle> getAll() throws Exception {
        return dao.findAll(); // Получаем все статьи из БД
    }
}