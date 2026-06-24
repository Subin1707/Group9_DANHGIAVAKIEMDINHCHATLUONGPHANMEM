package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Appointment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class AppointmentDatabase {

    private volatile boolean schemaChecked = false;
    private volatile String lastError = null;

    private Connection getConnection() throws Exception {
        Connection conn = DatabaseConfig.getConnection();
        ensureAppointmentSchema(conn);
        return conn;
    }

    private synchronized void ensureAppointmentSchema(Connection conn) {
        if (schemaChecked) return;
        try {
            if (!tableExists(conn, "appointment")) {
                try (Statement stmt = conn.createStatement()) {
                    try {
                        stmt.executeUpdate(
                            "CREATE TABLE appointment (" +
                                "id VARCHAR(50) PRIMARY KEY," +
                                "patientId VARCHAR(50) NOT NULL," +
                                "roomId VARCHAR(20) NULL," +
                                "appointmentTime DATETIME NOT NULL," +
                                "note TEXT," +
                                "status ENUM('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING'," +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                                "CONSTRAINT fk_appointment_patient FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE," +
                                "CONSTRAINT fk_appointment_room FOREIGN KEY (roomId) REFERENCES room(id) ON DELETE SET NULL" +
                            ")"
                        );
                    } catch (Exception fkError) {
                        // Fallback: create without FKs (still functional if patient/room tables are missing)
                        stmt.executeUpdate(
                            "CREATE TABLE appointment (" +
                                "id VARCHAR(50) PRIMARY KEY," +
                                "patientId VARCHAR(50) NOT NULL," +
                                "roomId VARCHAR(20) NULL," +
                                "appointmentTime DATETIME NOT NULL," +
                                "note TEXT," +
                                "status VARCHAR(20) NOT NULL DEFAULT 'PENDING'," +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                            ")"
                        );
                    }
                    try { stmt.executeUpdate("CREATE INDEX idx_appointment_patient ON appointment(patientId)"); } catch (Exception ignored) {}
                    try { stmt.executeUpdate("CREATE INDEX idx_appointment_time ON appointment(appointmentTime)"); } catch (Exception ignored) {}
                    try { stmt.executeUpdate("CREATE INDEX idx_appointment_status ON appointment(status)"); } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {
            lastError = "Không tạo/kiểm tra được bảng appointment: " + ignored.getMessage();
        } finally {
            schemaChecked = true;
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) return true;
        }
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM appointment ORDER BY appointmentTime DESC")) {

            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (Exception e) {
            lastError = "Không đọc được appointment: " + e.getMessage();
        }
        return list;
    }

    public ArrayList<Appointment> findByPatientId(String patientId) {
        ArrayList<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE patientId = ? ORDER BY appointmentTime DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(fromResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Appointment findById(String id) {
        String sql = "SELECT * FROM appointment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return fromResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createAppointment(Appointment a) {
        String sql = "INSERT INTO appointment (id, patientId, roomId, appointmentTime, note, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getId());
            ps.setString(2, a.getPatientId());
            ps.setString(3, a.getRoomId());
            ps.setTimestamp(4, a.getAppointmentTime());
            ps.setString(5, a.getNote());
            ps.setString(6, a.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error createAppointment: " + e.getMessage());
            lastError = "Không tạo được appointment: " + e.getMessage();
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }

    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE appointment SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(String id) {
        String sql = "DELETE FROM appointment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateNextAppointmentId() {
        String sql = "SELECT id FROM appointment WHERE id REGEXP '^AP[0-9]+$' ORDER BY CAST(SUBSTRING(id, 3) AS UNSIGNED) DESC LIMIT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int nextNumber = 1;
            if (rs.next()) {
                String lastId = rs.getString("id");
                if (lastId != null && lastId.startsWith("AP")) {
                    nextNumber = Integer.parseInt(lastId.substring(2)) + 1;
                }
            }
            return String.format("AP%03d", nextNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return "AP001";
        }
    }

    private Appointment fromResultSet(ResultSet rs) throws Exception {
        String id = rs.getString("id");
        String patientId = rs.getString("patientId");
        String roomId = rs.getString("roomId");
        Timestamp appointmentTime = rs.getTimestamp("appointmentTime");
        String note = rs.getString("note");
        String status = rs.getString("status");
        return new Appointment(id, patientId, roomId, appointmentTime, note, status);
    }
}
