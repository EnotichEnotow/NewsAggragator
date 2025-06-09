package org.example.console;

import org.example.Storage.NewsArticle;
import org.example.repository.NewsArticleRepository;
import org.example.viewers.ConsoleNewsViewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MainMenuTest {

    private NewsArticleRepository mockRepo;
    private ConsoleNewsViewer mockViewer;

    @BeforeEach
    void setUp() {
        mockRepo = mock(NewsArticleRepository.class);
        mockViewer = mock(ConsoleNewsViewer.class);
    }

    @Test
    void testOption1_AllNews() throws Exception {
        NewsArticle article = new NewsArticle();
        article.setTitle("Test title");
        article.setContent("Test content");

        when(mockRepo.findAll()).thenReturn(Collections.singletonList(article));

        // Ввод: "1" (все новости), потом "0" (выход)
        String input = "1\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu(mockRepo, mockViewer);
        menu.run();

        verify(mockRepo).findAll();
        verify(mockViewer).show(any());
    }

    @Test
    void testOption2_Search() throws Exception {
        when(mockRepo.findAll(any())).thenReturn(Collections.emptyList());

        String input = "2\nпоиск\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu(mockRepo, mockViewer);
        menu.run();

        verify(mockRepo).findAll(any());
        verify(mockViewer).show(any());
    }

    @Test
    void testOption0_Exit() throws Exception {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu(mockRepo, mockViewer);
        menu.run();

        verifyNoInteractions(mockRepo, mockViewer);
    }
}
