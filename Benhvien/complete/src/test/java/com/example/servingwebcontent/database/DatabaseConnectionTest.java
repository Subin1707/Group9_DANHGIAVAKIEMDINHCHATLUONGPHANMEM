package com.example.servingwebcontent.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void dockerMysqlConnectionShouldWork() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void databaseNameShouldBeHospital() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            assertEquals("hospital", connection.getCatalog());
        }
    }

    @Test
    void requiredTablesShouldExist() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            assertTrue(tableExists(connection, "patient"));
            assertTrue(tableExists(connection, "room"));
            assertTrue(tableExists(connection, "benhAn"));
            assertTrue(tableExists(connection, "schedule"));
            assertTrue(tableExists(connection, "login"));
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws Exception {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }
}
