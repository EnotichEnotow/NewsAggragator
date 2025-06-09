package org.example.console;

import org.example.export.CsvExporter;
import org.example.export.HtmlExporter;
import org.example.export.JsonExporter;
import org.example.repository.NewsArticleRepository;
import org.example.services.ExportService;
import org.example.viewers.CategoryStatsViewer;
import org.example.viewers.ConsoleNewsViewer;

import java.util.List;
import java.util.Scanner;

import static org.example.App.fetchToDatabase;

public class MainMenu {

    private final NewsArticleRepository repo;
    private final ConsoleNewsViewer viewer;

    public MainMenu(NewsArticleRepository repo,
                    ConsoleNewsViewer viewer) {
        this.repo = repo;
        this.viewer = viewer;
    }

    public void run() throws Exception {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n1 – all news  2 – search  3 – export  4 – update 5 – statistics 0 – exit");
            String cmd = in.nextLine().trim();
            switch (cmd) {
                case "1" -> viewer.show(repo.findAll());  // показать все статьи
                case "2" -> {
                    System.out.print("Ключевые слова: ");
                    String kw = in.nextLine().trim();
                    // поиск по ключевым словам в заголовке и тексте
                    viewer.show(repo.findAll(a ->
                            a.getTitle().toLowerCase().contains(kw.toLowerCase()) ||
                                    a.getContent().toLowerCase().contains(kw.toLowerCase())
                    ));
                }
                case "3" -> {
                    // создаём меню экспорта с поддерживаемыми форматами
                    ExportMenu em = new ExportMenu(
                            new ExportService(
                                    List.of(new CsvExporter(),
                                            new JsonExporter(),
                                            new HtmlExporter())
                            )
                    );
                    em.run(repo.findAll());
                }
                case "4" -> {
                    System.out.println("Запускаем обновление RSS...");
                    fetchToDatabase();  // обновляем данные из RSS
                }
                case "5" -> new CategoryStatsViewer(repo).show();  // показываем статистику
                case "0" -> { return; }  // выход из приложения
                default  -> System.out.println("Неверная команда");
            }
        }
    }
}
