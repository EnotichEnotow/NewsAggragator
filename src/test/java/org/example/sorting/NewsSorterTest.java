package org.example.sorting;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewsSorterTest {

    private final NewsSorter sorter = new NewsSorter();

    @Test
    void sortByDate_descendingWithNullsLast() {
        NewsArticle a = new NewsArticle();
        a.setReleaseDate(ZonedDateTime.parse("2025-01-01T10:00:00Z"));
        NewsArticle b = new NewsArticle();
        b.setReleaseDate(ZonedDateTime.parse("2025-06-01T10:00:00Z"));
        NewsArticle c = new NewsArticle();
        c.setReleaseDate(null);

        List<NewsArticle> input = List.of(a, c, b);
        List<NewsArticle> sorted = sorter.sort(input, SortCriterion.DATE);

        // Должно получиться: b (2025-06), потом a (2025-01), потом c (null)
        assertEquals(List.of(b, a, c), sorted);
    }

    @Test
    void sortBySource_lexicographicallyEmptyForEmptyAndNullHost() {
        NewsArticle validA = new NewsArticle();
        validA.setLink("https://b.com/news");       // host = "b.com"

        NewsArticle validB = new NewsArticle();
        validB.setLink("https://a.com/home");       // host = "a.com"

        NewsArticle emptyHost = new NewsArticle();
        emptyHost.setLink(null);                    // host("") через exception → сортируется сначала

        NewsArticle nullHost = new NewsArticle();
        nullHost.setLink("invalid://url");          // URI.getHost() = null → идёт в конец

        List<NewsArticle> input = List.of(validA, validB, emptyHost, nullHost);
        List<NewsArticle> sorted = sorter.sort(input, SortCriterion.SOURCE);

        // Ожидаем порядок:
        //   1) emptyHost (host = "")
        //   2) validB   (host = "a.com")
        //   3) validA   (host = "b.com")
        //   4) nullHost (host = null)
        assertEquals(List.of(emptyHost, validB, validA, nullHost), sorted);
    }
}
