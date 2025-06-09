package org.example.filters;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryFilterTest {

    private NewsArticle article;
    private CategoryFilter filter;

    @BeforeEach
    void setUp() {
        article = new NewsArticle();
        // Допустимые категории: "Sports" и "Politics"
        filter = new CategoryFilter(Set.of("Sports", "Politics"));
    }

    @Test
    void matches_returnsTrue_whenCategoryInSet() {
        article.setCategory("Sports");
        assertTrue(filter.matches(article),
                "Статья с категорией 'Sports' должна пройти фильтр");

        article.setCategory("Politics");
        assertTrue(filter.matches(article),
                "Статья с категорией 'Politics' должна пройти фильтр");
    }

    @Test
    void matches_returnsFalse_whenCategoryNotInSet() {
        article.setCategory("Technology");
        assertFalse(filter.matches(article),
                "Статья с категорией 'Technology' не должна пройти фильтр");

        article.setCategory(null);
        assertFalse(filter.matches(article),
                "Статья с null-категорией не должна пройти фильтр");
    }
}
