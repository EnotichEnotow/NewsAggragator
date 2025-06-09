package org.example.export;

import org.example.Storage.NewsArticle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class HtmlExporter implements Exporter {

    @Override
    public Path export(String dir, List<NewsArticle> data) throws Exception {
        Path path = Path.of(dir, "news.html");

        try (Writer w  = new OutputStreamWriter(
                new FileOutputStream(path.toFile()), StandardCharsets.UTF_8);
             PrintWriter pw = new PrintWriter(w))
        {
            pw.println("<!DOCTYPE html><html><head><meta charset=\"utf-8\">");
            pw.println("""
                        <style>
                        table{border-collapse:collapse;font-family:sans-serif;font-size:14px}
                        th,td{border:1px solid #ccc;padding:4px 6px;vertical-align:top}
                        th{background:#f2f2f2}
                        </style></head><body>
                        """);
            pw.println("<table>");
            pw.println("<tr><th>Title</th><th>Date</th><th>Category</th>" +
                    "<th>Description</th><th>Link</th></tr>");

            for (NewsArticle a : data) {
                pw.printf("""
                          <tr><td>%1$s</td><td>%2$s</td><td>%3$s</td>
                              <td>%4$s</td><td><a href="%5$s">open</a></td></tr>%n""",
                        esc(a.getTitle()),
                        a.getReleaseDate()==null?"":a.getReleaseDate(),
                        toRu(a.getCategory()),
                        esc(a.getDescription()),
                        esc(a.getLink()));
            }
            pw.println("</table></body></html>");
        }
        return path;
    }

    private String esc(String s){
        return s==null?"":s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;");
    }

    private String toRu(String cat) {
        return "OTHER".equals(cat) ? "Другое" : cat;
    }
}
