package org.example.WorkWithRss.interfaces;

/**
 * Интерфейс для извлечения полного текста статьи по URL.
 */
public interface IContentExtractor {
    /**
     * Извлекает и возвращает основной текст статьи.
     * @param url адрес страницы статьи
     * @return текст статьи
     * @throws Exception при ошибке подключения или парсинга
     */
    String extract(String url) throws Exception;
}
