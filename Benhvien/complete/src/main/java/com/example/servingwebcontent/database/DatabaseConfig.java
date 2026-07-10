package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;

/** Shared JDBC configuration for local Docker MySQL and Railway MySQL. */
public final class DatabaseConfig {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String URL_SUFFIX =
        "/hospital?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
        + "&connectTimeout=5000&socketTimeout=10000";
    private static final String DEFAULT_USERNAME = "hospital";
    private static final String DEFAULT_PASSWORD = "hospital_secret";

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = setting("SPRING_DATASOURCE_URL", "spring.datasource.url", "");
        String username = setting("SPRING_DATASOURCE_USERNAME", "spring.datasource.username", "");
        String password = setting("SPRING_DATASOURCE_PASSWORD", "spring.datasource.password", "");
        if (!url.isBlank()) {
            return DriverManager.getConnection(
                url,
                username.isBlank() ? DEFAULT_USERNAME : username,
                password.isBlank() ? DEFAULT_PASSWORD : password
            );
        }

        String host = setting("DB_HOST", "db.host", DEFAULT_HOST);
        String port = setting("DB_PORT", "db.port", DEFAULT_PORT);
        String fallbackUrl = "jdbc:mysql://" + host + ":" + port + URL_SUFFIX;
        return DriverManager.getConnection(
            fallbackUrl,
            setting("DB_USERNAME", "db.username", DEFAULT_USERNAME),
            setting("DB_PASSWORD", "db.password", DEFAULT_PASSWORD)
        );
    }

    private static String setting(String environmentName, String propertyName, String defaultValue) {
        String systemValue = System.getProperty(propertyName);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String environmentValue = System.getenv(environmentName);
        return environmentValue != null && !environmentValue.isBlank() ? environmentValue : defaultValue;
    }
}
