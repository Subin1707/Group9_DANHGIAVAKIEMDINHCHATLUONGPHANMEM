package com.example.servingwebcontent.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void dockerMysqlConnectionShouldWork() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }
}
