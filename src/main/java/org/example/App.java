package org.example;

import org.example.WorkWithRss.ConfigSourceProvider;
import org.example.WorkWithRss.JsoupContentExtractor;
import org.example.WorkWithRss.RssParser;
import org.example.WorkWithRss.RssService;
import org.example.dao.DataSourceProvider;
import org.example.dao.JdbcNewsArticleDao;
import org.example.repository.JdbcNewsArticleRepository;
import org.example.services.NewsAggregatorService;
import org.example.services.SharedCountClientService;
import org.example.sorting.NewsSorter;
import org.example.viewers.ConsoleNewsViewer;
import org.example.console.MainMenu;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        // 1) Создаём таблицы из schema.sql (если нужно)
        initDatabase();

        // 2) Загружаем RSS, сохраняем новые + логируем share-count, удаляем старые (>7 дней)
        fetchToDatabase();

        // 3) Конфигурируем консольный UI
        DataSource ds    = DataSourceProvider.getDataSource();
        var dao          = new JdbcNewsArticleDao(ds);
        var repo         = new JdbcNewsArticleRepository(dao);
        var sorter       = new NewsSorter();               // теперь без PopularityService
        var viewer       = new ConsoleNewsViewer(sorter);  // теперь без stats
        var mainMenu     = new MainMenu(repo, viewer);

        // 4) Запускаем главное меню
        try {
            mainMenu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Создаёт таблицы из schema.sql, если их ещё нет. */
    private static void initDatabase() {
        try (
                Connection c = DataSourceProvider.getDataSource().getConnection();
                Statement  s = c.createStatement();
                InputStream in = App.class.getClassLoader().getResourceAsStream("schema.sql");
                Scanner sc = new Scanner(in).useDelimiter(";")
        ) {
            while (sc.hasNext()) {
                String sql = sc.next().trim();
                if (!sql.isEmpty()) {
                    s.execute(sql);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации БД", e);
        }
    }

    /** Парсит RSS, сохраняет новые статьи (лог share-count), удаляет устаревшие (>7 дней). */
    public static void fetchToDatabase() {
        // 1) Настраиваем RSS-парсер
        var parser = new RssParser(new JsoupContentExtractor());
        var rss    = new RssService(
                /* threadPoolSize */ 4,
                /* parser        */ parser,
                /* sources       */ new ConfigSourceProvider()
        );

        // 2) Создаём DAO для сохранения в БД
        var dao = new JdbcNewsArticleDao(DataSourceProvider.getDataSource());

        // 3) Клиент SharedCount API с вашим API-ключом
        var shareClientService = new SharedCountClientService(
                "548695c01762f255533a3d4d29c7caaf72b06f6c"
        );

        // 4) Агрегатор новостей, теперь учитывающий share-count
        var service = new NewsAggregatorService(rss, dao, shareClientService);

        try {
            service.updateAll();
            System.out.println("RSS updated.");
        } catch (Exception e) {
            System.err.println("RSS update failed:");
            e.printStackTrace();
        } finally {
            rss.shutdown();
        }
    }
}
