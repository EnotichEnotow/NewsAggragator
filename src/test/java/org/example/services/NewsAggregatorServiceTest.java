package org.example.services;

import org.example.Storage.NewsArticle;
import org.example.WorkWithRss.interfaces.IRssService;
import org.example.dao.NewsArticleDao;
import org.example.util.ChangeLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsAggregatorServiceTest {

    @Mock IRssService rssService;
    @Mock NewsArticleDao dao;
    @Mock SharedCountClientService shareClient;

    @InjectMocks
    NewsAggregatorService service;

    NewsArticle newArticle;
    NewsArticle existingArticle;
    NewsArticle oldArticle;

    @BeforeEach
    void setUp() {
        // Новый: dao.existsByLink -> false
        newArticle = new NewsArticle();
        newArticle.setLink("http://example.com/new");
        newArticle.setReleaseDate(ZonedDateTime.now());

        // Уже есть: dao.existsByLink -> true
        existingArticle = new NewsArticle();
        existingArticle.setLink("http://example.com/existing");
        existingArticle.setReleaseDate(ZonedDateTime.now());

        // Старый для удаления
        oldArticle = new NewsArticle();
        oldArticle.setLink("http://example.com/old");
    }

    @Test
    void updateAll_addsNewAndDeletesOld_andLogsChanges() throws Exception {
        // 1. rssService возвращает и новый, и существующий
        when(rssService.fetchAll()).thenReturn(List.of(newArticle, existingArticle));
        // existsByLink: новый → false, существующий → true
        when(dao.existsByLink("http://example.com/new")).thenReturn(false);
        when(dao.existsByLink("http://example.com/existing")).thenReturn(true);
        // shareClient возвращает 42 для нового
        when(shareClient.fetchShareCount("http://example.com/new")).thenReturn(42L);
        // dao.deleteOlderThanDays должен вернуть список со старой статьёй
        when(dao.deleteOlderThanDays(7)).thenReturn(List.of(oldArticle));

        // Мокаем статические вызовы ChangeLogger
        try (MockedStatic<ChangeLogger> log = mockStatic(ChangeLogger.class)) {
            // Вызов
            service.updateAll();

            // 2. Для нового: fetchShareCount → setShareCount и save
            verify(shareClient).fetchShareCount("http://example.com/new");
            assertEquals(42L, newArticle.getShareCount());
            verify(dao).save(newArticle);
            // А для существующего save не вызывается
            verify(dao, never()).save(existingArticle);
            // ChangeLogger.logAdded вызывался для newArticle
            log.verify(() -> ChangeLogger.logAdded(newArticle));

            // 3. Старые удалены и залогированы
            verify(dao).deleteOlderThanDays(7);
            log.verify(() -> ChangeLogger.logDeleted(oldArticle));
        }
    }

    @Test
    void updateAll_onShareClientError_setsZeroAndStillSaves() throws Exception {
        when(rssService.fetchAll()).thenReturn(List.of(newArticle));
        when(dao.existsByLink(newArticle.getLink())).thenReturn(false);
        // эмулируем ошибку при fetchShareCount
        when(shareClient.fetchShareCount(newArticle.getLink()))
                .thenThrow(new RuntimeException("fail"));

        try (MockedStatic<ChangeLogger> log = mockStatic(ChangeLogger.class)) {
            service.updateAll();

            // В случае ошибки shareCount должно стать 0
            assertEquals(0L, newArticle.getShareCount());
            // И save всё равно вызывается
            verify(dao).save(newArticle);
            log.verify(() -> ChangeLogger.logAdded(newArticle));
        }
    }

    @Test
    void getAll_delegatesToDao() throws Exception {
        var articles = List.of(newArticle, existingArticle);
        when(dao.findAll()).thenReturn(articles);

        var result = service.getAll();
        assertSame(articles, result);
        verify(dao).findAll();
    }
}
