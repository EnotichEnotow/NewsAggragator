package org.example.filters;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SourceFilterTest {

    private SourceFilter filter;

    @BeforeEach
    void setUp() {
        // Фильтруем по домену rbc.ru
        filter = new SourceFilter("rbc.ru");
    }

    @Test
    void matches_returnsTrue_whenLinkContainsHost() {
        NewsArticle a = new NewsArticle();
        a.setLink("https://www.rbc.ru/politics/12345");
        assertTrue(filter.matches(a), "Ссылка содержит 'rbc.ru' → должно быть true");
    }

    @Test
    void matches_returnsFalse_whenLinkDoesNotContainHost() {
        NewsArticle a = new NewsArticle();
        a.setLink("https://www.france24.com/en/12345");
        assertFalse(filter.matches(a), "Ссылка не содержит 'rbc.ru' → должно быть false");
    }

    @Test
    void matches_returnsFalse_whenLinkIsNull() {
        NewsArticle a = new NewsArticle();
        a.setLink(null);
        assertFalse(filter.matches(a), "Если ссылка null → должно быть false");
    }
}
