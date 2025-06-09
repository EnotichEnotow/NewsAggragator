package org.example.util;

import org.example.Storage.NewsArticle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChangeLogger {
    private static final String LOG_FILE = "news_changes.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /** Логирует добавление статьи */
    public static void logAdded(NewsArticle a) {
        //System.out.println("added");
        log("ADDED", a);
    }

    /** Логирует удаление статьи */
    public static void logDeleted(NewsArticle a) {
        log("DELETED", a);
    }

    /** Общий метод записи в лог */
    private static synchronized void log(String action, NewsArticle a) {
        String timestamp = ZonedDateTime.now().format(FMT);
        String line = String.format("[%s] %s | title=\"%s\" | link=%s%n",
                timestamp, action, a.getTitle(), a.getLink());
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.print(line);
        } catch (IOException e) {
            // если не удалось записать в файл — печатаем в stderr
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }
}
