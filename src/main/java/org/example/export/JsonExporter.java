package org.example.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.Storage.NewsArticle;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;


public class JsonExporter implements Exporter {

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    @Override
    public Path export(String dir, List<NewsArticle> data) throws Exception {
        Path path = Path.of(dir, "news.json");

        JsonArray arr = new JsonArray();
        for (NewsArticle a : data) {
            JsonObject o = new JsonObject();
            o.addProperty("title",       a.getTitle());
            o.addProperty("date",        a.getReleaseDate() == null ? null
                    : a.getReleaseDate().toString());
            o.addProperty("category",    toRu(a.getCategory()));      // ← Русификация
            o.addProperty("description", a.getDescription());
            o.addProperty("link",        a.getLink());
            arr.add(o);
        }
        try (FileWriter fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
            gson.toJson(arr, fw);
        }
        return path;
    }

    private String toRu(String cat) {
        return "OTHER".equals(cat) ? "Другое" : cat;
    }
}
