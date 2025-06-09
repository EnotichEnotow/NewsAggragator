package org.example.repository;

import org.example.Storage.NewsArticle;
import org.example.dao.JdbcNewsArticleDao;
import org.example.filters.NewsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcNewsArticleRepositoryTest {

    @Mock
    private JdbcNewsArticleDao dao;

    @InjectMocks
    private JdbcNewsArticleRepository repository;

    private NewsArticle a1;
    private NewsArticle a2;

    @BeforeEach
    void setUp() {
        a1 = new NewsArticle();
        a1.setTitle("First");
        a1.setCategory("CAT1");

        a2 = new NewsArticle();
        a2.setTitle("Second");
        a2.setCategory("CAT2");
    }

    @Test
    void findAll_returnsOnlyMatchingArticles() throws Exception {
        // dao returns both articles
        when(dao.findAll()).thenReturn(asList(a1, a2));
        // filter matching only CAT1
        NewsFilter filter = article -> "CAT1".equals(article.getCategory());

        List<NewsArticle> result = repository.findAll(filter);

        assertEquals(1, result.size(), "Should return only one matching article");
        assertSame(a1, result.get(0), "Returned article should be the one matching the filter");

        verify(dao, times(1)).findAll();
    }

    @Test
    void findAll_returnsEmptyList_whenNoMatch() throws Exception {
        when(dao.findAll()).thenReturn(asList(a1, a2));
        // filter matching none
        NewsFilter filter = article -> false;

        List<NewsArticle> result = repository.findAll(filter);

        assertTrue(result.isEmpty(), "Should return empty list when no articles match");
        verify(dao, times(1)).findAll();
    }

    @Test
    void findAll_propagatesExceptionFromDao() throws Exception {
        when(dao.findAll()).thenThrow(new Exception("DB error"));
        NewsFilter filter = article -> true;

        Exception ex = assertThrows(Exception.class, () -> repository.findAll(filter));
        assertEquals("DB error", ex.getMessage());
        verify(dao, times(1)).findAll();
    }
}
