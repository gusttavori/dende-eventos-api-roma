package br.com.softhouse.dende.repositories.util;

import br.com.dende.softhouse.annotations.Component;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ConnectionPool {

    private final HikariDataSource dataSource;

    public ConnectionPool(ConfigProperties config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getDbUrl());
        hikariConfig.setUsername(config.getDbUsername());
        hikariConfig.setPassword(config.getDbPassword());
        hikariConfig.setDriverClassName(config.getDbDriverClassName());
        hikariConfig.setMaximumPoolSize(config.getHikariMaxPoolSize());
        hikariConfig.setMinimumIdle(config.getHikariMinimumIdle());
        hikariConfig.setConnectionTimeout(config.getHikariConnectionTimeout());

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}