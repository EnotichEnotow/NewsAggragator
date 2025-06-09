package org.example.sorting;

import org.example.Storage.NewsArticle;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

public class NewsSorter {

    public List<NewsArticle> sort(List<NewsArticle> list, SortCriterion by) {
        Comparator<NewsArticle> cmp;
        switch (by) {
            case SOURCE:
                // сортируем по хосту из URL
                cmp = Comparator.comparing(a -> host(a.getLink()),
                        Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case DATE:
            default:
                // сортируем по дате публикации, null'ы в конец, по убыванию
                cmp = Comparator.comparing(NewsArticle::getReleaseDate,
                        Comparator.nullsLast(Comparator.reverseOrder()));
                break;
        }
        return list.stream()
                .sorted(cmp)
                .toList();
    }

    private String host(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }
}
