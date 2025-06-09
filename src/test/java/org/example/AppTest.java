// src/test/java/org/example/AppSmokeTest.java
package org.example;

import org.example.dao.DataSourceProvider;
import org.example.dao.JdbcNewsArticleDao;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Disabled;

@Disabled("DB-тест отключён — H2 удалена")
class AppTest {
    @Test
    void fakeInputExample() throws Exception {
        String fakeInput = "1\n0\n"; // 1 — выбрать пункт меню, 0 — выйти
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));
        App.main(new String[0]);
        assertTrue(true); // если не упал — уже хорошо
    }
}