package org.example.services;

import org.example.Storage.NewsArticle;
import org.example.export.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportService {

    private final Map<String, Exporter> exps = new HashMap<>();

    public ExportService(List<Exporter> list) {
        for (Exporter e : list) {
            String key =
                    (e instanceof CsvExporter)  ? "csv"  :
                            (e instanceof JsonExporter) ? "json" :
                                    (e instanceof HtmlExporter) ? "html" :
                                            e.getClass().getSimpleName().toLowerCase();
            exps.put(key, e);
        }
    }

    public boolean supports(String fmt) { return exps.containsKey(fmt); }

    /** Экспорт и возврат пути к файлу. */
    public Path export(String fmt, String folder, List<NewsArticle> data) throws Exception {
        Exporter e = exps.get(fmt);
        if (e == null) throw new IllegalArgumentException("unknown fmt: " + fmt);
        return e.export(folder, data);
    }
}
