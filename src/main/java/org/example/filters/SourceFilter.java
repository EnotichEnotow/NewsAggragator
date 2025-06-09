package org.example.filters;

import org.example.Storage.NewsArticle;

public final class SourceFilter implements NewsFilter {
    private final String host;  // rbc.ru / france24.com / ...

    public SourceFilter(String host) { this.host = host; }

    @Override public boolean matches(NewsArticle a) {
        return a.getLink() != null && a.getLink().contains(host);
    }
}
