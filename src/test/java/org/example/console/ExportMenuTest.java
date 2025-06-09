package org.example.console;

import org.example.Storage.NewsArticle;
import org.example.services.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class ExportMenuTest {

    private ExportService svc;
    private ExportMenu menu;

    @BeforeEach
    void setUp() {
        svc = mock(ExportService.class);
        menu = new ExportMenu(svc);
    }

    @Test
    void testRun_withValidFormatAndDefaultDir() throws Exception {
        List<NewsArticle> dummyData = Collections.emptyList();

        when(svc.supports("csv")).thenReturn(true);
        when(svc.export(eq("csv"), eq("./export"), eq(dummyData)))
                .thenReturn(Path.of("./export/news.csv"));

        // Подменяем System.in
        String input = "csv\n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        menu.run(dummyData);

        verify(svc).supports("csv");
        verify(svc).export("csv", "./export", dummyData);
    }

    @Test
    void testRun_withUnsupportedFormat() throws Exception {
        List<NewsArticle> dummyData = Collections.emptyList();

        when(svc.supports("xml")).thenReturn(false);

        // Подменяем System.in
        String input = "xml\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        menu.run(dummyData);

        verify(svc).supports("xml");
        verify(svc, never()).export(any(), any(), any());
    }
}
