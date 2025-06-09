package org.example.console;

import org.example.Storage.NewsArticle;
import org.example.services.ExportService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ExportMenu {

    private final ExportService svc;

    public ExportMenu(ExportService svc) {  // получаем сервис для экспорта
        this.svc = svc;
    }

    public void run(List<NewsArticle> data) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Формат (csv/json/html): ");
        String fmt = sc.nextLine().trim().toLowerCase();
        // проверяем, поддерживается ли формат
        if (!svc.supports(fmt)) {
            System.out.println("Не знаю такой формат");
            return;
        }

        System.out.print("Папка (Enter = ./export): ");
        String dir = sc.nextLine().trim();
        if (dir.isBlank()) dir = "./export";  // значение по умолчанию

        // выполняем экспорт и выводим путь к файлу
        Path file = svc.export(fmt, dir, data);
        System.out.println("✓ Готово: " + file.toAbsolutePath());
    }
}