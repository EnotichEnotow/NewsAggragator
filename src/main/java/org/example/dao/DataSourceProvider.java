package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

public class DataSourceProvider {
    private static HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try {
            props.load(DataSourceProvider.class.getClassLoader().getResourceAsStream("db.properties"));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить настройки БД", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
