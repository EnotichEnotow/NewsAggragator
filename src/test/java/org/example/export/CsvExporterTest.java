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

class CsvExporterTest {

    @TempDir
    Path tempDir;  // временная директория для файлов

    @Test
    void export_createsCsvWithHeaderAndOneRow() throws Exception {
        CsvExporter exporter = new CsvExporter();
        NewsArticle article = new NewsArticle();
        article.setTitle("My Title");
        article.setReleaseDate(ZonedDateTime.parse("2025-06-09T12:34:56Z"));
        article.setCategory("OTHER");
        article.setDescription("Some description");
        article.setLink("http://example.com");

        // экспортируем CSV в tempDir
        Path csv = exporter.export(tempDir.toString(), List.of(article));
        assertTrue(Files.exists(csv));

        // читаем строки CSV
        List<String> lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());  // заголовок + одна строка

        assertEquals("Title,Date,Category,Description,Link", lines.get(0));  // проверяем заголовок

        String row = lines.get(1);
        assertTrue(row.startsWith("\"My Title\","), "Title должен быть в кавычках");
        assertTrue(row.contains(",Другое,"), "Категория OTHER должна стать «Другое»");
        assertTrue(row.endsWith("http://example.com"), "Link без кавычек");
    }

    @Test
    void export_escapesInnerQuotesAndHandlesNulls() throws Exception {
        CsvExporter exporter = new CsvExporter();
        NewsArticle a = new NewsArticle();
        a.setTitle("He said \"Hello\"");  // кавычки внутри текста
        a.setReleaseDate(ZonedDateTime.parse("2025-06-09T12:00:00Z"));
        a.setCategory("News");
        a.setDescription(null);  // null-описание
        a.setLink("link");

        Path csv = exporter.export(tempDir.toString(), List.of(a));
        List<String> lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());

        String row = lines.get(1);
        String[] cols = row.split(",", -1);

        // кавычки внутри поля должны быть экранированы
        assertEquals("\"He said \"\"Hello\"\"\"", cols[0]);
        // null → пустое значение в кавычках
        assertEquals("\"\"", cols[3], "Описание null должно выводиться как пустое поле в кавычках");
    }
}
