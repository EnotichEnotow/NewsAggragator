package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SharedCountClientServiceTest {

    private static final String API_KEY = "test-key";

    @Mock
    private HttpClient mockHttp;

    @Mock
    private HttpResponse<String> mockResponse;

    private SharedCountClientService client;

    @BeforeEach
    void setUp() throws Exception {
        client = new SharedCountClientService(API_KEY);

        // Подменяем приватное поле http на наш мок
        Field httpField = SharedCountClientService.class.getDeclaredField("http");
        httpField.setAccessible(true);
        httpField.set(client, mockHttp);

        // Подменяем ObjectMapper, чтобы не было побочных эффектов (необязательно)
        Field mapperField = SharedCountClientService.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(client, new ObjectMapper());
    }

    @Test
    void fetchShareCount_sumsAllNetworks() throws Exception {
        // 1) Подготовим JSON с разными счётчиками
        String body = """
            {
              "Facebook":   {"share_count": 10},
              "Pinterest":  {"share_count": 20},
              "Reddit":     {"share_count": 30},
              "LinkedIn":   {"share_count": 40},
              "OtherNetwork": {"share_count": 999}
            }
            """;
        when(mockResponse.body()).thenReturn(body);
        when(mockHttp.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // 2) Вызов
        long total = client.fetchShareCount("https://example.com/page?param=1");

        // 3) Проверка суммы только по 4 заявленным сетям: 10+20+30+40 = 100
        assertEquals(100, total);

        // 4) Убедимся, что в запросе правильно закодирован ключ и URL
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttp).send(captor.capture(), any());
        URI uri = captor.getValue().uri();
        String encodedUrl = URLEncoder.encode("https://example.com/page?param=1", StandardCharsets.UTF_8);
        assertTrue(uri.toString().contains("apikey=" + API_KEY));
        assertTrue(uri.toString().contains("url=" + encodedUrl));
    }

    @Test
    void fetchShareCount_skipsNonNumericAndMissing() throws Exception {
        // share_count отсутствует или не число
        String body = """
            {
              "Facebook":   {"share_count": null},
              "Pinterest":  {},
              "Reddit":     {"share_count": "NaN"},
              "LinkedIn":   {"share_count": 5}
            }
            """;
        when(mockResponse.body()).thenReturn(body);
        when(mockHttp.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Должно посчитать только LinkedIn = 5
        assertEquals(5, client.fetchShareCount("http://x"));
    }

    @Test
    void fetchShareCount_propagatesIOException() throws Exception {
        when(mockHttp.send(any(), any())).thenThrow(new IOException("network error"));

        IOException ex = assertThrows(IOException.class,
                () -> client.fetchShareCount("http://fail"));
        assertEquals("network error", ex.getMessage());
    }

    @Test
    void fetchShareCount_propagatesInterruptedException() throws Exception {
        when(mockHttp.send(any(), any())).thenThrow(new InterruptedException("interrupted"));

        InterruptedException ex = assertThrows(InterruptedException.class,
                () -> client.fetchShareCount("http://fail"));
        assertEquals("interrupted", ex.getMessage());
    }
}
