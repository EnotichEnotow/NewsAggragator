package org.example.viewers;

import org.example.Storage.NewsArticle;
import org.example.sorting.NewsSorter;
import org.example.sorting.SortCriterion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsoleNewsViewerTest {

    private ByteArrayOutputStream out;
    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalIn  = System.in;
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void show_emptyList_printsNoNews() {
        NewsSorter sorter = mock(NewsSorter.class);
        ConsoleNewsViewer viewer = new ConsoleNewsViewer(sorter);

        viewer.show(List.of());  // пустой список

        String output = out.toString();
        assertTrue(output.contains("Нет новостей по заданным условиям."),
                "Должно выводиться сообщение об отсутствии новостей");
    }

    @Test
    void show_singleArticle_andQuit_printsOneItemThenExit() {
        // Один объект
        NewsArticle a = new NewsArticle();
        a.setTitle("Заголовок");
        a.setDescription("Описание");
        a.setLink("http://example.com");
        a.setReleaseDate(ZonedDateTime.parse("2025-06-09T12:00:00Z"));

        // Настраиваем сортировщик: возвращает тот же список
        NewsSorter sorter = mock(NewsSorter.class);
        when(sorter.sort(anyList(), eq(SortCriterion.DATE)))
                .thenAnswer(inv -> inv.getArgument(0));

        ConsoleNewsViewer viewer = new ConsoleNewsViewer(sorter);

        // Симулируем ввод: сразу quit
        provideInput("q\n");

        viewer.show(List.of(a));

        String output = out.toString();
        // Должны увидеть заголовок страницы
        assertTrue(output.contains("» page 1/1  (sorted by DATE)"),
                "Должен быть заголовок страницы");
        // Должен быть напечатан наш элемент [ 1] Заголовок
        assertTrue(output.contains("[ 1] Заголовок"),
                "Должен быть выведен заголовок статьи");
        // И после выхода команда q завершает работу без exception
    }

    // Восстанавливаем System.in/out, если нужно в дальнейшем
    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }
}
