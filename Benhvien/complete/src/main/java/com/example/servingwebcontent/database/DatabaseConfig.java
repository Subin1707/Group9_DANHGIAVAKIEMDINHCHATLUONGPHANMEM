package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;

/** Shared JDBC configuration for the local Docker MySQL instance. */
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
        String host = setting("DB_HOST", "db.host", DEFAULT_HOST);
        String port = setting("DB_PORT", "db.port", DEFAULT_PORT);
        rejectUnsupportedHost(host);
        String url = "jdbc:mysql://" + host + ":" + port + URL_SUFFIX;
        return DriverManager.getConnection(
            url,
            setting("DB_USERNAME", "db.username", DEFAULT_USERNAME),
            setting("DB_PASSWORD", "db.password", DEFAULT_PASSWORD)
        );
    }

    private static void rejectUnsupportedHost(String host) {
        String normalizedHost = host == null ? "" : host.trim().toLowerCase();
        if (!normalizedHost.equals("localhost")
            && !normalizedHost.equals("127.0.0.1")
            && !normalizedHost.equals("mysql")
            && !normalizedHost.equals("host.docker.internal")) {
            throw new IllegalStateException("Only local Docker MySQL hosts are supported.");
        }
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
