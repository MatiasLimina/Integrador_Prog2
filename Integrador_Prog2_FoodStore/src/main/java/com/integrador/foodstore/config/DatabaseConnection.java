package com.integrador.foodstore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/food_store_tpi");
        config.setUsername("root");
        config.setPassword("");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);

        ds = new HikariDataSource(config);

    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
