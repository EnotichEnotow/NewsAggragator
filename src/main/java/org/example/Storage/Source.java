package org.example.Storage;

public class Source {
    private final String name;
    private final String feedUrl;
    private final String language;

    public Source(String name, String feedUrl, String language) {
        this.name = name;
        this.feedUrl = feedUrl;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getLanguage() {
        return language;
    }
}
