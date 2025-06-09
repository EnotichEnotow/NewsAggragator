package org.example.WorkWithRss;

import org.example.WorkWithRss.interfaces.IContentExtractor;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class JsoupContentExtractor implements IContentExtractor {

    private static final Logger log = LoggerFactory.getLogger(JsoupContentExtractor.class);

    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " + "(KHTML, like Gecko) Chrome/125.0 Safari/537.36";

    private static final int TIMEOUT = 30_000;  // Таймаут соединения 30 сек

    @Override
    public String extract(String url) throws IOException {
        Document doc = fetchWithFallback(url);
        if (doc == null) return "";

        // Удаляем всё лишнее скрипты, меню, футеры ...
        doc.select("script, style, nav, header, footer, aside, noscript, form, [role=toolbar]")
                .remove();

        // Собираем текст из заголовков и абзацев
        return doc.select("p, h1, h2, h3")
                .stream()
                .map(Element::text)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public List<String> extractImages(String url) throws IOException {
        Document doc = fetchWithFallback(url);
        if (doc == null) return List.of();

        // Собираем абсолютные ссылки на все изображения
        Set<String> imgs = new LinkedHashSet<>();
        doc.select("img[src]").forEach(el -> {
            String s = el.absUrl("src");
            if (!s.isBlank()) imgs.add(s);
        });
        return new ArrayList<>(imgs);
    }

    public List<String> extractVideos(String url) throws IOException {
        Document doc = fetchWithFallback(url);
        if (doc == null) return List.of();

        // Собираем ссылки на видео (video[src] и iframe c YouTube)
        Set<String> videos = new LinkedHashSet<>();
        doc.select("video source[src], video[src]").forEach(el -> {
            String s = el.hasAttr("src") ? el.absUrl("src") : "";
            if (!s.isBlank()) videos.add(s);
        });
        doc.select("iframe[src]").forEach(el -> {
            String s = el.absUrl("src");
            if (s.contains("youtube") || s.contains("player") || s.endsWith(".mp4"))
                videos.add(s);
        });
        return new ArrayList<>(videos);
    }


    // Загружает страницу = При 403 ошибке пробует AMP-версии для France24 и Meduza. AMP (Accelerated Mobile Pages) — это облегчённые версии веб-страниц, разработанные Google. Они загружаются значительно быстрее, особенно на мобильных устройствах.
    private Document fetchWithFallback(String url) throws IOException {
        try {
            return connect(url).get();

        } catch (HttpStatusException e1) {
            int code = e1.getStatusCode();

            // Обход защиты на France 24
            if (code == 403 && url.contains("france24.com")) {
                String amp1 = url.contains("?") ? url + "&outputType=amp" : url + "?outputType=amp";
                try { return connect(amp1).get(); } catch (HttpStatusException ignored) {}
                return tryAmpPath(url, "France24");
            }

            // Обход защиты на Meduza
            if (code == 403 && url.contains("meduza.io")) {
                String amp1 = url.contains("?") ? url + "&amp" : url + "?amp";
                try { return connect(amp1).get(); } catch (HttpStatusException ignored) {}
                return tryAmpPath(url, "Meduza");
            }

            throw e1;
        }
    }

     // Формирует URL вида /amp/... и пробует его загрузить
    private Document tryAmpPath(String url, String siteName) {
        try {
            URI u = URI.create(url);
            String[] p = u.getPath().split("/", 3);
            if (p.length >= 3) {
                String ampPath = "/" + p[1] + "/amp/" + p[2];
                String ampUrl  = u.getScheme() + "://" + u.getHost() + ampPath
                        + (u.getQuery() == null ? "" : "?" + u.getQuery());
                log.debug("{} retry /amp/ {}", siteName, ampUrl);
                return connect(ampUrl).get();
            }
        } catch (Exception ignored) {}

        log.warn("{} AMP fallback failed: {}", siteName, url);
        return null;
    }

    // Настройки соединения Jsoup с нужными заголовками
    private Connection connect(String url) {
        return Jsoup.connect(url)
                .userAgent(UA)
                .timeout(TIMEOUT)
                .maxBodySize(0)  // без ограничения размера тела ответа
                .followRedirects(true)
                .referrer("https://www.google.com/")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Cache-Control", "no-cache")
                .header("Upgrade-Insecure-Requests", "1")
                .ignoreHttpErrors(true);
    }
}
