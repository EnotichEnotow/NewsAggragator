package org.example.WorkWithRss;

import org.example.Storage.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Читает RSS-источники из файла ресурсов rss-sources.properties.
 */
public class ConfigSourceProvider implements org.example.WorkWithRss.interfaces.ISourceProvider {
    private static final String CONFIG_FILE = "rss-sources.properties";
    private static final String KEY = "rss.sources";

    @Override
    public List<Source> getSources() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("Конфигурационный файл не найден: " + CONFIG_FILE);
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения конфигурации RSS-источников", e);
        }

        String raw = props.getProperty(KEY, "");
        List<Source> list = new ArrayList<>();
        for (String url : raw.split(",")) {
            url = url.trim();
            if (!url.isEmpty()) {
                list.add(new Source("", url, ""));
            }
        }
        return list;
    }
}
