// src/main/java/org/example/dao/JdbcNewsArticleDao.java
package org.example.dao;

import org.example.Storage.NewsArticle;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class JdbcNewsArticleDao implements NewsArticleDao {

    private static final String INSERT_SQL = """
        INSERT INTO news_article
          (title, link, description, release_date, category,
           image_urls, video_urls, tags, content)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (link) DO NOTHING
        """;

    private static final String EXISTS_SQL =
            "SELECT 1 FROM news_article WHERE link = ? LIMIT 1";

    private static final String SELECT_ALL_SQL = """
        SELECT title, link, description, release_date, category,
               image_urls, video_urls, tags, content
          FROM news_article
          ORDER BY release_date DESC
        """;

    private final DataSource ds;

    public JdbcNewsArticleDao(DataSource ds) {
        // DataSource для соединения с БД
        this.ds = ds;
    }

    @Override
    public void save(NewsArticle a) {
        // Пропускаем, если запись уже есть
        if (existsByLink(a.getLink())) return;
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(INSERT_SQL)) {
            // Заполняем параметры
            st.setString(1, a.getTitle());
            st.setString(2, a.getLink());
            st.setString(3, a.getDescription());
            st.setTimestamp(4,
                    a.getReleaseDate() == null
                            ? null
                            : Timestamp.from(a.getReleaseDate().toInstant()));
            st.setString(5, a.getCategory());
            st.setString(6, join(a.getImageUrl()));
            st.setString(7, join(a.getVideoUrl()));
            st.setString(8, join(a.getTags()));
            st.setString(9, a.getContent());
            // Выполняем вставку
            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<NewsArticle> list) {
        // Сохраняем все статьи
        list.forEach(this::save);
    }

    @Override
    public boolean existsByLink(String link) {
        // Проверяем наличие по ссылке
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(EXISTS_SQL)) {
            st.setString(1, link);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NewsArticle> findAll() throws Exception {
        // Возвращаем все статьи, отсортированные по дате
        List<NewsArticle> out = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                out.add(mapRow(rs));
            }
        }
        return out;
    }


    // Возвращает список всех ссылок в бд
    public List<String> fetchAllLinks() {
        List<String> links = new ArrayList<>();
        String sql = "SELECT link FROM news_article";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql); // Подготавливаем SQL-запрос к выполнению
             ResultSet rs = st.executeQuery()) {  // Выполняем запрос и получаем набор результатов

            while (rs.next()) {
                links.add(rs.getString("link"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return links;
    }

     // Удаляем из БД все новости старше 30 дней + возвращает список удалённых статей для записи в логи
    public List<NewsArticle> deleteOlderThanDays(int days) {
        List<NewsArticle> removed = new ArrayList<>();
        String sql = """
            DELETE FROM news_article
             WHERE release_date < now() - (? * INTERVAL '1 day')
            RETURNING title, link, description, release_date,
                      category, image_urls, video_urls, tags, content
            """;
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) { // Подготавливаем команду DELETE с возвратом удалённых строк
            st.setInt(1, days);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    removed.add(mapRow(rs)); // Преобразуем каждую строку в объект
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return removed;
    }

    private NewsArticle mapRow(ResultSet rs) throws SQLException {
        // Преобразуем ResultSet в объект NewsArticle
        NewsArticle a = new NewsArticle();
        a.setTitle(rs.getString("title"));
        a.setLink(rs.getString("link"));
        a.setDescription(rs.getString("description"));

        Timestamp ts = rs.getTimestamp("release_date");
        if (ts != null) {
            a.setReleaseDate(ts.toInstant().atZone(ZoneId.systemDefault()));
        }

        a.setCategory(rs.getString("category"));
        a.setImageUrl(List.of(rs.getString("image_urls").split(",")));
        a.setVideoUrl(List.of(rs.getString("video_urls").split(",")));
        a.setTags(List.of(rs.getString("tags").split(",")));
        a.setContent(rs.getString("content"));
        return a;
    }

    private String join(List<String> list) {
        return list == null ? "" : String.join(",", list);  // Объединяем список строк через запятую для хранения в одной колонке
    }
}
