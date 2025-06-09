package org.example.viewers;

import org.example.Storage.NewsArticle;
import org.example.console.Page;
import org.example.sorting.NewsSorter;
import org.example.sorting.SortCriterion;
import org.jsoup.Jsoup;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Scanner;

public class ConsoleNewsViewer {

    private final NewsSorter sorter;

    public ConsoleNewsViewer(NewsSorter sorter) {
        this.sorter = sorter;
    }

    public void show(List<NewsArticle> list) {
        if (list.isEmpty()) {
            System.out.println("\nНет новостей по заданным условиям.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        int page = 0, perPage = 10;
        SortCriterion criterion = SortCriterion.DATE;

        while (true) {
            // Сортируем и формируем страницу
            List<NewsArticle> sorted = sorter.sort(list, criterion);
            Page<NewsArticle> p = makePage(sorted, page, perPage);

            System.out.printf("%n» page %d/%d  (sorted by %s)%n",
                    p.pageNo() + 1, p.pagesTotal(), criterion);

            for (int i = 0; i < p.items().size(); i++) {
                print(i + 1, p.items().get(i));
            }

            System.out.println("\n n-next, p-prev, d-date, s-source, v-views, q-quit");

            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "n"  -> page = Math.min(page + 1, p.pagesTotal() - 1);
                case "p"  -> page = Math.max(page - 1, 0);
                case "d"  -> { criterion = SortCriterion.DATE;       page = 0; }
                case "s"  -> { criterion = SortCriterion.SOURCE;     page = 0; }
                case "v"  -> { criterion = SortCriterion.POPULARITY; page = 0; }
                case "q"  -> { return; }
                default   -> openByNumber(cmd, p.items());
            }
        }
    }

    private void print(int idx, NewsArticle a) {
        System.out.printf("[%2d] %s%n    %s%n    %s%n",
                idx, clean(a.getTitle()), clean(a.getDescription()), a.getLink());
    }

    private String clean(String raw) {
        if (raw == null) return "";
        String txt = Jsoup.parse(raw).text().replaceAll("\\s+", " ").trim();
        return txt.length() > 120 ? txt.substring(0, 117) + "..." : txt;
    }

    private Page<NewsArticle> makePage(List<NewsArticle> src, int idx, int size) {
        int pages = Math.max(1, (int) Math.ceil(src.size() / (double) size));
        int from  = Math.min(idx * size, src.size());
        int to    = Math.min(from + size, src.size());
        return new Page<>(src.subList(from, to), idx, pages);
    }
    // показываем по 10 новостей за раз
    private void openByNumber(String input, List<NewsArticle> items) {
        try {
            int num = Integer.parseInt(input);
            if (num >= 1 && num <= items.size()) {
                NewsArticle a = items.get(num - 1);
                if (Desktop.isDesktopSupported()) { // Проверка поддерживается ли открытие ссылок
                    Desktop.getDesktop().browse(new URI(a.getLink()));
                } else {
                    System.out.println("Нужно ручками: " + a.getLink());
                }
            }
        } catch (Exception ignored) {
        }
    }
}
