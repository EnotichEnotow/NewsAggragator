package org.example.export;

import org.example.Storage.NewsArticle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvExporter implements Exporter {

    @Override
    public java.nio.file.Path export(String dir, List<NewsArticle> data) throws Exception {
        java.nio.file.Path path = java.nio.file.Path.of(dir, "news.csv");

        try (Writer w  = new OutputStreamWriter(
                new FileOutputStream(path.toFile()), StandardCharsets.UTF_8);
             PrintWriter pw = new PrintWriter(w))
        {
            pw.println("Title,Date,Category,Description,Link");

            for (NewsArticle a : data) {
                pw.printf("\"%s\",%s,%s,\"%s\",%s%n",
                        esc(a.getTitle()),
                        a.getReleaseDate(),
                        toRu(a.getCategory()),
                        esc(a.getDescription()),
                        a.getLink());
            }
        }
        return path;
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\"\"");
    }

    private String toRu(String cat) {
        return "OTHER".equals(cat) ? "Другое" : cat;
    }
}
