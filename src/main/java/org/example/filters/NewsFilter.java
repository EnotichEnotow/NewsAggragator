package org.example.filters;

import org.example.Storage.NewsArticle;

@FunctionalInterface
public interface NewsFilter {
    boolean matches(NewsArticle article);

    /** and-композиция: f1.and(f2).and(f3)… */
    default NewsFilter and(NewsFilter other) {
        return art -> this.matches(art) && other.matches(art);
    }
}
