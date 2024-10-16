package ru.mai.trpo.configuration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Компонент для логирования информации о подключении к базе данных при запуске приложения
 */
@Slf4j
@Component
public class DataSourceLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final DataSource dataSource;

    public DataSourceLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Метод логирования информации о подключении к БД
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Пытаемся получить соединение с базой данных
        try (Connection connection = dataSource.getConnection()) {
            // Получаем метаданные базы данных для логирования
            DatabaseMetaData metaData = connection.getMetaData();
            // Логируем URL подключения к базе данных
            log.info("Connected to database: {}", metaData.getURL());
            // Логируем имя пользователя, используемого для подключения к базе
            log.info("Database user: {}", metaData.getUserName());
            // Логируем название продукта базы данных (например, PostgreSQL, MySQL)
            log.info("Database product name: {}", metaData.getDatabaseProductName());
            // Логируем версию продукта базы данных
            log.info("Database product version: {}", metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            // Логируем ошибку, если не удалось получить соединение с базой данных или метаданные
            log.error("Failed to log database connection details", e);
        }
    }
}
