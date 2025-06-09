package org.example.viewers;

import org.example.Storage.Category;
import org.example.Storage.NewsArticle;
import org.example.repository.NewsArticleRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryStatsViewer {
    private final NewsArticleRepository repo;

    public CategoryStatsViewer(NewsArticleRepository repo) {
        this.repo = repo;
    }

    /**
     * Выводит в консоль статистику: для каждой категории — количество статей.
     */
    public void show() throws Exception {
        List<NewsArticle> all = repo.findAll();
        Map<Category, Long> counts = all.stream()
                .collect(Collectors.groupingBy(
                        a -> Category.fromString(a.getCategory()),
                        Collectors.counting()
                ));

        System.out.println("\nСтатистика по категориям:");
        for (Category c : Category.values()) {
            long cnt = counts.getOrDefault(c, 0L);
            System.out.printf("  %s: %d%n", c, cnt);
        }
    }
}
