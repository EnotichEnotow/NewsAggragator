package org.example.export;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void export_createsHtmlWithHeaderAndOneRow() throws Exception {
        HtmlExporter exporter = new HtmlExporter();
        NewsArticle article = new NewsArticle();
        article.setTitle("My Title");
        article.setReleaseDate(ZonedDateTime.parse("2025-06-09T13:45:00Z"));
        article.setCategory("OTHER");
        article.setDescription("Desc text");
        article.setLink("http://example.com");

        Path html = exporter.export(tempDir.toString(), List.of(article));
        assertTrue(Files.exists(html), "Файл должен быть создан");

        String content = Files.readString(html, StandardCharsets.UTF_8);
        // Общие элементы
        assertTrue(content.contains("<!DOCTYPE html>"), "Должен быть DOCTYPE");
        assertTrue(content.contains("<meta charset=\"utf-8\">"), "Должен быть указана кодировка UTF-8");
        assertTrue(content.contains("<table>"), "Должна быть таблица");
        // Заголовки столбцов
        assertTrue(content.contains("<th>Title</th>"), "Должен быть заголовок Title");
        assertTrue(content.contains("<th>Date</th>"), "Должен быть заголовок Date");
        assertTrue(content.contains("<th>Category</th>"), "Должен быть заголовок Category");
        assertTrue(content.contains("<th>Description</th>"), "Должен быть заголовок Description");
        assertTrue(content.contains("<th>Link</th>"), "Должен быть заголовок Link");

        // Проверяем первую (и единственную) строку данных
        // Дата
        assertTrue(content.contains("<td>2025-06-09T13:45Z</td>") ||
                content.contains("2025-06-09T13:45:00Z"), "Дата должна подставиться");
        // Русификация категории OTHER → «Другое»
        assertTrue(content.contains("<td>Другое</td>"), "Категория OTHER должна стать «Другое»");
        // Описание
        assertTrue(content.contains("<td>Desc text</td>"));
        // Ссылка
        assertTrue(content.contains("<a href=\"http://example.com\">open</a>"));
    }

    @Test
    void export_escapesSpecialCharactersAndHandlesNulls() throws Exception {
        HtmlExporter exporter = new HtmlExporter();
        NewsArticle article = new NewsArticle();
        // Спецсимволы для теста экранирования
        article.setTitle("Fish & <Chips>");
        article.setReleaseDate(null);          // null → пустая ячейка
        article.setCategory("News");
        article.setDescription(null);          // null → пустая ячейка
        article.setLink("http://link?x=1&y=2");

        Path html = exporter.export(tempDir.toString(), List.of(article));
        String content = Files.readString(html, StandardCharsets.UTF_8);

        // Экранирование: & → &amp;, < → &lt;, > → &gt;
        assertTrue(content.contains("Fish &amp; &lt;Chips&gt;"), "Заголовок должен быть экранирован");
        // null-дата и null-описание → пустые ячейки <td></td>
        assertTrue(content.contains("<td></td>"), "Null-поля должны отобразиться пустыми");
        // Ссылка тоже экранируется внутри href
        assertTrue(content.contains("http://link?x=1&amp;y=2"), "Ссылка должна экранировать &");
    }
}
