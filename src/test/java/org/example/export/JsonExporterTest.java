package org.example.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void export_createsPrettyJsonWithOneElement_andRussianCategory() throws Exception {
        JsonExporter exporter = new JsonExporter();
        NewsArticle a = new NewsArticle();
        a.setTitle("Test Title");
        a.setReleaseDate(ZonedDateTime.parse("2025-06-09T10:15:30Z"));
        a.setCategory("OTHER");     // должно превратиться в "Другое"
        a.setDescription("Some desc");
        a.setLink("http://example.org");

        Path out = exporter.export(tempDir.toString(), List.of(a));
        assertTrue(Files.exists(out), "Файл news.json должен быть создан");

        String jsonText = Files.readString(out, StandardCharsets.UTF_8);
        // Проверяем prettified: есть переносы строк и отступы
        assertTrue(jsonText.contains("[\n"), "Должен быть перенос строки после '['");
        assertTrue(jsonText.contains("  {"), "Должен быть отступ перед '{'");

        // Парсим и проверяем содержимое
        JsonArray arr = JsonParser.parseString(jsonText).getAsJsonArray();
        assertEquals(1, arr.size(), "В массиве должен быть один элемент");

        JsonObject obj = arr.get(0).getAsJsonObject();
        assertEquals("Test Title", obj.get("title").getAsString());
        assertEquals("2025-06-09T10:15:30Z", obj.get("date").getAsString());
        assertEquals("Другое", obj.get("category").getAsString());
        assertEquals("Some desc", obj.get("description").getAsString());
        assertEquals("http://example.org", obj.get("link").getAsString());
    }

    @Test
    void export_nullFieldsBecomeJsonNullLiteral() throws Exception {
        JsonExporter exporter = new JsonExporter();
        NewsArticle a = new NewsArticle();
        a.setTitle(null);
        a.setReleaseDate(null);
        a.setCategory("News");
        a.setDescription(null);
        a.setLink(null);

        Path out = exporter.export(tempDir.toString(), List.of(a));
        String jsonText = Files.readString(out, StandardCharsets.UTF_8);

        // Проверяем, что title, date, description и link выводятся как литерал null
        assertTrue(
                jsonText.contains("\"title\": null") || jsonText.contains("\"title\":null"),
                "title должен быть null"
        );
        assertTrue(
                jsonText.contains("\"date\": null") || jsonText.contains("\"date\":null"),
                "date должен быть null"
        );
        assertTrue(
                jsonText.contains("\"description\": null") || jsonText.contains("\"description\":null"),
                "description должен быть null"
        );
        assertTrue(
                jsonText.contains("\"link\": null") || jsonText.contains("\"link\":null"),
                "link должен быть null"
        );

        // Категория не-null, должна быть просто "News"
        assertTrue(
                jsonText.contains("\"category\": \"News\""),
                "category должен быть News"
        );
    }
}
