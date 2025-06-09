package org.example.export;

import org.example.Storage.NewsArticle;

import java.nio.file.Path;
import java.util.List;


public interface Exporter {
    Path export(String dir, List<NewsArticle> data) throws Exception;
}
