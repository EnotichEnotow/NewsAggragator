package org.example.WorkWithRss;

import org.example.Storage.NewsArticle;
import org.example.Storage.Source;
import org.example.WorkWithRss.interfaces.IParser;
import org.example.WorkWithRss.interfaces.ISourceProvider;
import org.example.WorkWithRss.interfaces.IRssService;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// загружает и парсит RSS-ленты параллельно
public class RssService implements IRssService {

    private final IParser parser; // Парсер RSS-лент
    private final ISourceProvider sources; // Источник списка RSS
    final ExecutorService pool;  // Потоковый пул для параллельной загрузки

    public RssService(int threadPoolSize, IParser parser, ISourceProvider sources) {
        this.parser = parser;
        this.sources = sources;
        this.pool  = Executors.newFixedThreadPool(threadPoolSize);  // создаём пул потоков
    }

    @Override
    public List<NewsArticle> fetchAll() throws InterruptedException {
        // Для каждой ленты создаётся Callable с безопасным парсингом
        List<Callable<List<NewsArticle>>> tasks = sources.getSources().stream()
                .map(Source::getFeedUrl)
                .map(url -> (Callable<List<NewsArticle>>) () -> safeParse(url))
                .collect(Collectors.toList());

        List<NewsArticle> collected = new ArrayList<>();
        for (Future<List<NewsArticle>> f : pool.invokeAll(tasks)) {
            try {
                // Получаем результат задачи (список статей) и добавляем к общему списку
                collected.addAll(f.get());
            } catch (ExecutionException e) {
                System.err.println("Task failed: " + e.getCause());
            }
        }

        // Удаляем дубликаты по ссылке
        Map<String, NewsArticle> uniq = new LinkedHashMap<>(); // link - как уникальный ключ
        for (NewsArticle a : collected) {
            uniq.putIfAbsent(a.getLink(), a);
        }
        return new ArrayList<>(uniq.values());
    }

    // Безопасный парсинг RSS-ленты
    private List<NewsArticle> safeParse(String url) {
        try {
            return parser.parseFeed(url);
        } catch (Exception ex) {
            System.err.println("Parse error " + url + ": " + ex);
            return List.of(); // возвращает пустой список при ошибке
        }
    }

    @Override
    public void shutdown() {
        pool.shutdown();  // закрываем пул потоков
    }
}
