package com.example.servingwebcontent.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConstraintTest {

    @Test
    void patientNameNotNullConstraintShouldWork() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String sql = """
                    INSERT INTO patient(id, name, dob, age, gender, address, phone)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            assertThrows(SQLException.class, () -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "TEST_NULL_NAME");
                    ps.setString(2, null);
                    ps.setDate(3, java.sql.Date.valueOf("2000-01-01"));
                    ps.setInt(4, 24);
                    ps.setString(5, "Nam");
                    ps.setString(6, "Hà Nội");
                    ps.setString(7, "0900000000");
                    ps.executeUpdate();
                }
            });
        }
    }

    @Test
    void duplicatePrimaryKeyShouldThrowSQLException() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String sql = """
                    INSERT INTO patient(id, name, dob, age, gender, address, phone)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, "TEST_DUPLICATE");
                ps.setString(2, "Người bệnh test");
                ps.setDate(3, java.sql.Date.valueOf("2000-01-01"));
                ps.setInt(4, 24);
                ps.setString(5, "Nam");
                ps.setString(6, "Hà Nội");
                ps.setString(7, "0900000000");
                ps.executeUpdate();
            }

            assertThrows(SQLException.class, () -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "TEST_DUPLICATE");
                    ps.setString(2, "Người bệnh test 2");
                    ps.setDate(3, java.sql.Date.valueOf("2000-01-01"));
                    ps.setInt(4, 24);
                    ps.setString(5, "Nam");
                    ps.setString(6, "Hà Nội");
                    ps.setString(7, "0900000001");
                    ps.executeUpdate();
                }
            });

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM patient WHERE id = ?")) {
                ps.setString(1, "TEST_DUPLICATE");
                ps.executeUpdate();
            }
        }
    }

    @Test
    void benhAnForeignKeyPatientShouldWork() throws Exception {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String sql = """
                    INSERT INTO benhAn(id, patientId, ngayKham, trieuChung, tienSuBenh, chanDoan, roomId)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            assertThrows(SQLException.class, () -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "TEST_BA_FK");
                    ps.setString(2, "PATIENT_NOT_EXISTS");
                    ps.setDate(3, java.sql.Date.valueOf("2026-01-01"));
                    ps.setString(4, "Sốt");
                    ps.setString(5, "Không");
                    ps.setString(6, "Cảm cúm");
                    ps.setString(7, null);
                    ps.executeUpdate();
                }
            });
        }
    }
}
