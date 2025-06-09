package org.example.filters;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeFilterTest {

    // Границы для фильтра
    private static final ZonedDateTime START = ZonedDateTime.of(2025, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final ZonedDateTime END   = ZonedDateTime.of(2025, 6, 30, 23, 59, 59, 0, ZoneOffset.UTC);

    private final DateRangeFilter filter = new DateRangeFilter(START, END);

    /** Входящая статья без даты должна отфильтровываться */
    @Test
    void matches_returnsFalse_whenDateIsNull() {
        NewsArticle a = new NewsArticle();
        a.setReleaseDate(null);
        assertFalse(filter.matches(a), "Статья без даты не должна проходить фильтр");
    }

    /** Дата до начала интервала — false */
    @Test
    void matches_returnsFalse_whenBeforeStart() {
        NewsArticle a = new NewsArticle();
        a.setReleaseDate(START.minusDays(1));
        assertFalse(filter.matches(a), "Дата перед началом интервала не должна проходить");
    }

    /** Дата после конца интервала — false */
    @Test
    void matches_returnsFalse_whenAfterEnd() {
        NewsArticle a = new NewsArticle();
        a.setReleaseDate(END.plusSeconds(1));
        assertFalse(filter.matches(a), "Дата после конца интервала не должна проходить");
    }

    /** Дата внутри интервала — true */
    @Test
    void matches_returnsTrue_whenWithinRange() {
        NewsArticle a = new NewsArticle();
        a.setReleaseDate(START.plusDays(15));
        assertTrue(filter.matches(a), "Дата внутри интервала должна проходить");
    }

    /** Граничные даты (равные START и END) — true */
    @Test
    void matches_returnsTrue_whenOnBounds() {
        NewsArticle a1 = new NewsArticle();
        a1.setReleaseDate(START);
        assertTrue(filter.matches(a1), "Дата, равная START, должна проходить");

        NewsArticle a2 = new NewsArticle();
        a2.setReleaseDate(END);
        assertTrue(filter.matches(a2), "Дата, равная END, должна проходить");
    }
}
