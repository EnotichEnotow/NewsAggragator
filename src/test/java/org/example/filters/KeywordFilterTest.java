package org.example.filters;

import org.example.Storage.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordFilterTest {

    private KeywordFilter filter;

    @BeforeEach
    void setUp() {
        filter = new KeywordFilter("elon|musk|SpaceX");
    }

    // Если текст есть в заголовке, должен вернуть true
    @Test
    void matches_returnsTrue_whenKeywordInTitle() {
        NewsArticle a = new NewsArticle();
        a.setTitle("Breaking: Elon launches new rocket");
        a.setDescription("nothing");
        a.setContent("nothing");
        assertTrue(filter.matches(a), "Должно найтись слово 'Elon' в title");
    }

    // Если текст есть в описании, должен вернуть true
    @Test
    void matches_returnsTrue_whenKeywordInDescription() {
        NewsArticle a = new NewsArticle();
        a.setTitle("no");
        a.setDescription("This article talks about SpaceX mission");
        a.setContent("");
        assertTrue(filter.matches(a), "Должно найтися слово 'SpaceX' в description");
    }

    // Если текст есть в контенте, должен вернуть true
    @Test
    void matches_returnsTrue_whenKeywordInContent() {
        NewsArticle a = new NewsArticle();
        a.setTitle("");
        a.setDescription("");
        a.setContent("Biography of MUSK including ventures");
        assertTrue(filter.matches(a), "Должно найтися слово 'MUSK' в content (без учёта регистра)");
    }

    // Если нет ни в одном поле — false
    @Test
    void matches_returnsFalse_whenNoKeywordPresent() {
        NewsArticle a = new NewsArticle();
        a.setTitle("Hello world");
        a.setDescription("Nothing special here");
        a.setContent("Just some text");
        assertFalse(filter.matches(a), "Ни одного ключевого слова нет — должно быть false");
    }


    @Test
    void matches_handlesNullFields() {
        NewsArticle a = new NewsArticle();
        a.setTitle(null);
        a.setDescription(null);
        a.setContent(null);
        assertFalse(filter.matches(a), "Все поля null — должно быть false, без NPE");
    }

    // Проверяем, что фильтр действительно нечувствителен к регистру
    @Test
    void matches_caseInsensitive() {
        NewsArticle a = new NewsArticle();
        a.setTitle("spacex");
        a.setDescription(null);
        a.setContent(null);
        assertTrue(filter.matches(a), "Найти 'SpaceX' в 'spacex' должно работать");
    }

    // Проверяем, что несколько альтернатив разделённых \ тоже работают
    @Test
    void matches_multipleAlternatives() {
        // создаём фильтр по словам foo или bar
        KeywordFilter alt = new KeywordFilter("foo|bar");
        NewsArticle a1 = new NewsArticle();
        a1.setTitle("Something about foo");
        assertTrue(alt.matches(a1));

        NewsArticle a2 = new NewsArticle();
        a2.setTitle("Here is BAR in uppercase");
        assertTrue(alt.matches(a2));

        NewsArticle a3 = new NewsArticle();
        a3.setTitle("No match here");
        assertFalse(alt.matches(a3));
    }
}
