package org.example.filters;

import org.example.Storage.NewsArticle;
import java.util.Set;

/** Фильтрация по множеству категорий. */
public final class CategoryFilter implements NewsFilter {
    private final Set<String> categories;

    public CategoryFilter(Set<String> categories) {
        this.categories = categories;
    }

    @Override
    public boolean matches(NewsArticle a) {
        String cat = a.getCategory();
        // Если категория null или не входит в заданный набор — возвращаем false
        return cat != null && categories.contains(cat);
    }
}
