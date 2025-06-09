package org.example.filters;


import org.example.Storage.NewsArticle;
import java.time.ZonedDateTime;

public final class DateRangeFilter implements NewsFilter {
    private final ZonedDateTime from;
    private final ZonedDateTime to;

    public DateRangeFilter(ZonedDateTime from, ZonedDateTime to) {
        this.from = from;
        this.to   = to;
    }
    @Override public boolean matches(NewsArticle a) {
        return a.getReleaseDate() != null &&
                !a.getReleaseDate().isBefore(from) &&
                !a.getReleaseDate().isAfter(to);
    }
}
