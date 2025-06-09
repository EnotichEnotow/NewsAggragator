package org.example.filters;

import org.example.Storage.NewsArticle;
import java.util.regex.Pattern;

public final class KeywordFilter implements NewsFilter {
    private final Pattern pattern;

    public KeywordFilter(String keywords) {          // "elon musk|SpaceX|Илон"
        this.pattern = Pattern.compile(keywords, Pattern.CASE_INSENSITIVE);
    }
    @Override public boolean matches(NewsArticle a) {
        return pattern.matcher(a.getTitle() == null ? "" : a.getTitle()).find() ||
                pattern.matcher(a.getDescription() == null ? "" : a.getDescription()).find() ||
                pattern.matcher(a.getContent()  == null ? "" : a.getContent()).find();
    }
}
