package org.example.services;

import org.example.Storage.NewsArticle;
import org.example.filters.KeywordFilter;
import org.example.repository.NewsArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private NewsArticleRepository repo;

    @InjectMocks
    private SearchService service;

    @BeforeEach
    void setUp() {
        // Mockito инициирует repo, service автоматически через @InjectMocks
    }

    @Test
    void search_delegatesToRepositoryAndReturnsResults() throws Exception {
        // Подготовка данных
        NewsArticle a1 = new NewsArticle();
        a1.setTitle("Elon Musk запускает ракету");
        NewsArticle a2 = new NewsArticle();
        a2.setDescription("SpaceX успешно совершила посадку");
        List<NewsArticle> expected = List.of(a1, a2);

        // Когда repo.findAll с любым KeywordFilter вызывается, вернуть expected
        when(repo.findAll(any(KeywordFilter.class))).thenReturn(expected);

        // Вызов
        List<NewsArticle> actual = service.search("elon musk|SpaceX");

        // Проверяем, что вернулось ровно то, что репозиторий дал
        assertSame(expected, actual);

        // Ловим переданный в репозиторий фильтр
        ArgumentCaptor<KeywordFilter> captor = ArgumentCaptor.forClass(KeywordFilter.class);
        verify(repo).findAll(captor.capture());

        KeywordFilter filter = captor.getValue();

        // Проверяем, что фильтр действительно ищет по переданным ключевым словам:
        NewsArticle matchByTitle = new NewsArticle();
        matchByTitle.setTitle("Илон Маск и SpaceX");
        NewsArticle matchByDescription = new NewsArticle();
        matchByDescription.setDescription("Something about SpaceX landing");
        NewsArticle noMatch = new NewsArticle();
        noMatch.setContent("Ничего общего");

        assertTrue(filter.matches(matchByTitle),    "Должен матчить по title");
        assertTrue(filter.matches(matchByDescription), "Должен матчить по description");
        assertFalse(filter.matches(noMatch),        "Не должен матчить отсутствующие слова");
    }

    @Test
    void search_propagatesExceptionFromRepository() throws Exception {
        // Репозиторий кидает исключение
        when(repo.findAll(any())).thenThrow(new IllegalStateException("DB error"));

        // Ожидаем, что service.search тоже бросит
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.search("anything")
        );
        assertEquals("DB error", ex.getMessage());
    }
}
