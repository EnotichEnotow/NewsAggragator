package org.example.viewers;

import org.example.Storage.NewsArticle;
import org.example.Storage.Category;
import org.example.repository.NewsArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryStatsViewerTest {

    private NewsArticleRepository repo;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        repo = mock(NewsArticleRepository.class);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void show_printsCorrectCountsForAllCategories_includingSPORTS() throws Exception {
        // две статьи со спортом на русском
        NewsArticle sport1 = new NewsArticle(); sport1.setCategory("спорт");
        NewsArticle sport2 = new NewsArticle(); sport2.setCategory("спорт");
        // по одной статье других категорий
        NewsArticle pol   = new NewsArticle(); pol.setCategory("политика");
        NewsArticle sci   = new NewsArticle(); sci.setCategory("наука");
        NewsArticle other = new NewsArticle(); other.setCategory("unknown");

        when(repo.findAll()).thenReturn(List.of(sport1, sport2, pol, sci, other));

        new CategoryStatsViewer(repo).show();

        String output = out.toString();

        // Проверяем, что для SPORTS (т.е. "спорт") вывелось 2
        assertTrue(output.contains("SPORTS: 2"),   "Должно быть подсчитано 2 статьи в категории SPORTS");

        // Остальные категории
        assertTrue(output.contains("POLITICS: 1"), "Должно быть подсчитано 1 статья в категории POLITICS");
        assertTrue(output.contains("SCIENCE: 1"),  "Должно быть подсчитано 1 статья в категории SCIENCE");
        assertTrue(output.contains("OTHER: 1"),    "Должно быть подсчитано 1 статья в категории OTHER");
    }
}
