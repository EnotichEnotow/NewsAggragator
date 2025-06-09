package org.example.util;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLoggerTest {

    private static final Path LOG = Paths.get("news_changes.log");

    @BeforeEach
    void cleanLog() throws IOException {
        Files.deleteIfExists(LOG);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(LOG);
    }

    @Test
    void logAddedAndDeleted_appendTwoLinesWithProperFormat() throws IOException {
        // 1) Подготовка тестовой статьи
        NewsArticle a = new NewsArticle();
        a.setTitle("Test \"Title\"");
        a.setLink("http://example.com/article");

        // 2) Вызываем оба метода логгера
        ChangeLogger.logAdded(a);
        ChangeLogger.logDeleted(a);

        // 3) Проверяем, что файл появился и в нём две строки
        assertTrue(Files.exists(LOG), "Файл лога должен быть создан");
        List<String> lines = Files.readAllLines(LOG);
        assertEquals(2, lines.size(), "Должно быть ровно две записи в логе");

        // 4) Первая строка — ADDED
        String first = lines.get(0);
        assertTrue(first.contains("] ADDED |"), "Первая строка должна содержать метку '] ADDED |'");
        assertTrue(first.contains("title=\"Test"), "Первая строка должна содержать title Test");
        assertTrue(first.contains("link=http://example.com/article"), "Первая строка должна содержать ссылку");

        // 5) Вторая строка — DELETED
        String second = lines.get(1);
        assertTrue(second.contains("] DELETED |"), "Вторая строка должна содержать метку '] DELETED |'");
        assertTrue(second.contains("title=\"Test"), "Вторая строка должна содержать title Test");
        assertTrue(second.contains("link=http://example.com/article"), "Вторая строка должна содержать ссылку");
    }
}
