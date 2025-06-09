package org.example.WorkWithRss.interfaces;

import java.util.List;
import org.example.Storage.NewsArticle;

public interface IRssService {
    List<NewsArticle> fetchAll() throws InterruptedException;
    void shutdown();
}
