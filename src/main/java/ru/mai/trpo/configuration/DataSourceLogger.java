package ru.mai.trpo.configuration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataSourceLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final DataSource dataSource;

    public DataSourceLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("Connected to database: {}", metaData.getURL());
            log.info("Database user: {}", metaData.getUserName());
            log.info("Database product name: {}", metaData.getDatabaseProductName());
            log.info("Database product version: {}", metaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("Failed to log database connection details", e);
        }
    }
}
