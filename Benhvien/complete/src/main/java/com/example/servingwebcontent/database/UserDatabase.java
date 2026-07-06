package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.User;
import com.example.servingwebcontent.Model.UserProfile;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDatabase {

    private volatile boolean schemaChecked = false;
    private volatile boolean roleColumnAvailable = false;
    private volatile boolean patientIdColumnAvailable = false;

    private Connection getConnection() throws Exception {
        Connection conn = DatabaseConfig.getConnection();
        ensureLoginSchema(conn);
        return conn;
    }

    private synchronized void ensureLoginSchema(Connection conn) {
        if (schemaChecked) return;

        try {
            DatabaseMetaData meta = conn.getMetaData();
            if (hasColumn(meta, "login", "role") || hasColumn(meta, "LOGIN", "role")) {
                roleColumnAvailable = true;
            } else {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE login ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER'");
                    roleColumnAvailable = true;
                } catch (Exception ignored) {
                    roleColumnAvailable = false;
                }
            }

            if (hasColumn(meta, "login", "patientId") || hasColumn(meta, "LOGIN", "patientId")) {
                patientIdColumnAvailable = true;
            } else {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE login ADD COLUMN patientId VARCHAR(50) NULL");
                    patientIdColumnAvailable = true;
                } catch (Exception ignored) {
                    patientIdColumnAvailable = false;
                }
            }
        } catch (Exception e) {
            roleColumnAvailable = false;
            patientIdColumnAvailable = false;
        } finally {
            schemaChecked = true;
        }
    }

    private boolean hasColumn(DatabaseMetaData meta, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) AS count FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                rs.close();
                return count > 0;
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi kiểm tra user tồn tại: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String password) {
        return registerUser(username, password, Role.USER);
    }

    public boolean registerUser(String username, String password, Role role) {
        if (userExists(username)) {
            return false;
        }

        Role safeRole = role != null ? role : Role.USER;
        String roleValue = safeRole.name().toUpperCase(Locale.ROOT);

        String queryWithRoleAndPatientId = "INSERT INTO login (username, password, role, patientId, created_at) VALUES (?, ?, ?, ?, NOW())";
        String queryWithRole = "INSERT INTO login (username, password, role, created_at) VALUES (?, ?, ?, NOW())";
        String queryWithoutRole = "INSERT INTO login (username, password, created_at) VALUES (?, ?, NOW())";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 roleColumnAvailable
                     ? (patientIdColumnAvailable ? queryWithRoleAndPatientId : queryWithRole)
                     : queryWithoutRole
             )) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (roleColumnAvailable && patientIdColumnAvailable) {
                pstmt.setString(3, roleValue);
                String defaultPatientId = username != null && username.toUpperCase(Locale.ROOT).matches("^P[0-9]+$") ? username : null;
                pstmt.setString(4, defaultPatientId);
            } else if (roleColumnAvailable) {
                pstmt.setString(3, roleValue);
            }

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi SQL khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        return authenticate(username, password) != null;
    }

    public User authenticate(String username, String password) {
        String queryWithRole = "SELECT username, role FROM login WHERE username = ? AND password = ?";
        String queryWithoutRole = "SELECT username FROM login WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(roleColumnAvailable ? queryWithRole : queryWithoutRole)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String foundUsername = rs.getString("username");
                Role role = Role.USER;
                if (roleColumnAvailable) {
                    role = Role.fromStringOrDefault(rs.getString("role"), Role.USER);
                }
                rs.close();
                return new User(foundUsername, null, role);
            }

            rs.close();
            return null;
        } catch (Exception e) {
            System.out.println("Lỗi khi xác thực user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getLinkedPatientId(String username) {
        if (username == null || username.isBlank()) return null;
        String sql = "SELECT patientId FROM login WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (Connection conn = getConnection()) {
            // getConnection() performs the one-time schema inspection. Checking
            // this flag before opening a connection made the first portal request
            // fall back to the username instead of the linked patient ID.
            if (!patientIdColumnAvailable) return username;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String patientId = rs.getString("patientId");
                        return (patientId == null || patientId.isBlank()) ? username : patientId;
                    }
                }
            }
        } catch (Exception e) {
            return username;
        }
        return username;
    }

    public boolean updateLinkedPatientId(String username, String patientId) {
        if (username == null || username.isBlank()) return false;
        String sql = "UPDATE login SET patientId = ? WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection()) {
            if (!patientIdColumnAvailable) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, patientId);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM login")) {

            if (rs.next()) {
                System.out.println("Kết nối DB OK — Có " + rs.getInt("count") + " user");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi kết nối DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void printAllUsers() {
        String queryWithRole = "SELECT username, password, role, created_at FROM login ORDER BY created_at DESC";
        String queryWithoutRole = "SELECT username, password, created_at FROM login ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(roleColumnAvailable ? queryWithRole : queryWithoutRole)) {

            System.out.println("\nDANH SÁCH USER:");
            System.out.println("======================================");
            while (rs.next()) {
                String roleText = roleColumnAvailable ? (" | role=" + rs.getString("role")) : "";
                System.out.println(rs.getString("username") +
                    " | " + rs.getString("password") +
                    roleText +
                    " | " + rs.getString("created_at"));
            }
            System.out.println("======================================\n");
        } catch (Exception e) {
            System.out.println("Lỗi khi hiển thị users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<UserProfile> listUserProfiles() {
        List<UserProfile> users = new ArrayList<>();
        String queryWithRole = "SELECT username, role, created_at FROM login ORDER BY created_at DESC";
        String queryWithRoleAndPatientId = "SELECT username, role, patientId, created_at FROM login ORDER BY created_at DESC";
        String queryWithoutRole = "SELECT username, created_at FROM login ORDER BY created_at DESC";
        String queryWithoutRoleButPatientId = "SELECT username, patientId, created_at FROM login ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 roleColumnAvailable
                     ? (patientIdColumnAvailable ? queryWithRoleAndPatientId : queryWithRole)
                     : (patientIdColumnAvailable ? queryWithoutRoleButPatientId : queryWithoutRole)
             )) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String createdAt = rs.getString("created_at");
                Role role = Role.USER;
                if (roleColumnAvailable) {
                    role = Role.fromStringOrDefault(rs.getString("role"), Role.USER);
                }
                String patientId = patientIdColumnAvailable ? rs.getString("patientId") : null;
                users.add(new UserProfile(username, role, patientId, createdAt));
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách user: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public UserProfile findUserProfile(String username) {
        if (username == null || username.isBlank()) return null;

        String queryWithRole = "SELECT username, role, created_at FROM login WHERE LOWER(username) = LOWER(?) LIMIT 1";
        String queryWithRoleAndPatientId = "SELECT username, role, patientId, created_at FROM login WHERE LOWER(username) = LOWER(?) LIMIT 1";
        String queryWithoutRole = "SELECT username, created_at FROM login WHERE LOWER(username) = LOWER(?) LIMIT 1";
        String queryWithoutRoleButPatientId = "SELECT username, patientId, created_at FROM login WHERE LOWER(username) = LOWER(?) LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 roleColumnAvailable
                     ? (patientIdColumnAvailable ? queryWithRoleAndPatientId : queryWithRole)
                     : (patientIdColumnAvailable ? queryWithoutRoleButPatientId : queryWithoutRole)
             )) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String foundUsername = rs.getString("username");
                String createdAt = rs.getString("created_at");
                Role role = Role.USER;
                if (roleColumnAvailable) {
                    role = Role.fromStringOrDefault(rs.getString("role"), Role.USER);
                }
                String patientId = patientIdColumnAvailable ? rs.getString("patientId") : null;
                rs.close();
                return new UserProfile(foundUsername, role, patientId, createdAt);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserRole(String username, Role role) {
        if (username == null || username.isBlank()) return false;
        if (!roleColumnAvailable) return false;

        Role safeRole = role != null ? role : Role.USER;
        String roleValue = safeRole.name().toUpperCase(Locale.ROOT);

        String sql = "UPDATE login SET role = ? WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roleValue);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi khi cập nhật role: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        if (username == null || username.isBlank()) return false;
        if (newPassword == null || newPassword.length() < 6) return false;

        String sql = "UPDATE login SET password = ? WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi khi reset mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        if (username == null || username.isBlank()) return false;
        if ("admin".equalsIgnoreCase(username)) return false;

        String sql = "DELETE FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi khi xóa user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
