package org.example.Storage;

import java.time.ZonedDateTime;
import java.util.List;

public class NewsArticle {
    private String title;
    private String link;
    private String description;
    private ZonedDateTime releaseDate;   // вместо String
    private String category;
    private List<String> imageUrl;
    private List<String> videoUrl;
    private List<String> tags;
    private String content;              // полный текст статьи

    // Геттеры/сеттеры
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ZonedDateTime getReleaseDate() { return releaseDate; }
    public void setReleaseDate(ZonedDateTime releaseDate) { this.releaseDate = releaseDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getImageUrl() { return imageUrl; }
    public void setImageUrl(List<String> imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getVideoUrl() { return videoUrl; }
    public void setVideoUrl(List<String> videoUrl) { this.videoUrl = videoUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", releaseDate=" + releaseDate +
                ", category='" + category + '\'' +
                ", tags=" + tags +
                '}';
    }


    private long shareCount;

    public long getShareCount() { return shareCount; }
    public void setShareCount(long shareCount) { this.shareCount = shareCount; }
}
