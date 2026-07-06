package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.example.servingwebcontent.Model.BenhAn;

public class BenhanDatabase {

    public ArrayList<BenhAn> getBenhanList() {
        ArrayList<BenhAn> benhans = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM benhAn");

            while (rs.next()) {
                String id = rs.getString("id");
                String patientId = rs.getString("patientId");
                java.sql.Date ngayKhamSql = rs.getDate("ngayKham");
                Calendar ngayKham = Calendar.getInstance();
                if (ngayKhamSql != null) {
                    ngayKham.setTime(ngayKhamSql);
                }
                String trieuChung = rs.getString("trieuChung");
                String tienSuBenh = rs.getString("tienSuBenh");
                String chanDoan = rs.getString("chanDoan");
                String roomId = rs.getString("roomId");

                BenhAn benhan = new BenhAn(id, patientId, ngayKham, trieuChung, tienSuBenh, chanDoan, roomId);
                benhans.add(benhan);
            }
        } catch (Exception e) {
            // Avoid flooding Maven Surefire's output channel with a potentially
            // enormous JDBC exception chain. The concise message is enough for
            // controllers to report an empty result while tests remain responsive.
            System.err.println("Cannot load medical records: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return benhans;
    }

    public boolean saveOrUpdateBenhan(BenhAn b) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            // Kiểm tra tồn tại
            String checkSql = "SELECT COUNT(*) FROM benhAn WHERE id = ?";
            java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, b.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;
            rs.close();
            checkStmt.close();

            if (exists) {
                // Update
                String sql = "UPDATE benhAn SET patientId=?, ngayKham=?, trieuChung=?, tienSuBenh=?, chanDoan=?, roomId=? WHERE id=?";
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, b.getPatientId());
                stmt.setDate(2, new java.sql.Date(b.getNgayKham().getTimeInMillis()));
                stmt.setString(3, b.getTrieuChung());
                stmt.setString(4, b.getTienSuBenh());
                stmt.setString(5, b.getChanDoan());
                stmt.setString(6, b.getRoomId());
                stmt.setString(7, b.getId());
                stmt.executeUpdate();
                stmt.close();
            } else {
                // Insert
                String sql = "INSERT INTO benhAn (id, patientId, ngayKham, trieuChung, tienSuBenh, chanDoan, roomId) VALUES (?, ?, ?, ?, ?, ?, ?)";
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, b.getId());
                stmt.setString(2, b.getPatientId());
                stmt.setDate(3, new java.sql.Date(b.getNgayKham().getTimeInMillis()));
                stmt.setString(4, b.getTrieuChung());
                stmt.setString(5, b.getTienSuBenh());
                stmt.setString(6, b.getChanDoan());
                stmt.setString(7, b.getRoomId());
                stmt.executeUpdate();
                stmt.close();
            }
            conn.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error in saveOrUpdateBenhan");
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return false;
        }
    }

    public boolean deleteBenhanById(String id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Debug: Kiểm tra xem bệnh án có tồn tại không
            String checkSql = "SELECT COUNT(*) FROM benhAn WHERE id = ?";
            java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            System.out.println("Debug: Found " + count + " benhan(s) with ID: " + id);
            
            if (count == 0) {
                System.out.println("Debug: Benhan not found!");
                conn.close();
                return false;
            }
            
            // Sử dụng PreparedStatement để tránh SQL injection
            String deleteSql = "DELETE FROM benhAn WHERE id = ?";
            java.sql.PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, id);
            int result = deleteStmt.executeUpdate();
            deleteStmt.close();
            conn.close();
            
            System.out.println("Debug: Deleted " + result + " row(s)");
            return result > 0;
        } catch (Exception e) {
            System.out.println("Error in deleteBenhanById: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return false;
        }
    }

    public BenhAn findBenhanById(String id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM benhAn WHERE id = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date ngayKhamSql = rs.getDate("ngayKham");
                Calendar ngayKham = Calendar.getInstance();
                if (ngayKhamSql != null) ngayKham.setTime(ngayKhamSql);
                BenhAn benhan = new BenhAn(
                        rs.getString("id"),
                        rs.getString("patientId"),
                        ngayKham,
                        rs.getString("trieuChung"),
                        rs.getString("tienSuBenh"),
                        rs.getString("chanDoan"),
                        rs.getString("roomId")
                );
                rs.close();
                stmt.close();
                conn.close();
                return benhan;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
        }
        return null;
    }

    // Tìm kiếm sử dụng Stream API (tìm kiếm trong memory)
    public ArrayList<BenhAn> searchBenhanWithStream(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getBenhanList();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        
        return getBenhanList().stream()
            .filter(benhan -> 
                benhan.getId().toLowerCase().contains(searchTerm) ||
                benhan.getPatientId().toLowerCase().contains(searchTerm) ||
                benhan.getTrieuChung().toLowerCase().contains(searchTerm) ||
                benhan.getTienSuBenh().toLowerCase().contains(searchTerm) ||
                benhan.getChanDoan().toLowerCase().contains(searchTerm) ||
                benhan.getRoomId().toLowerCase().contains(searchTerm)
            )
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Tìm kiếm và sắp xếp theo ID sử dụng Stream
    public ArrayList<BenhAn> searchAndSortBenhan(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getBenhanList().stream()
                .sorted((b1, b2) -> b1.getId().compareToIgnoreCase(b2.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        
        return getBenhanList().stream()
            .filter(benhan -> 
                benhan.getId().toLowerCase().contains(searchTerm) ||
                benhan.getPatientId().toLowerCase().contains(searchTerm) ||
                benhan.getTrieuChung().toLowerCase().contains(searchTerm) ||
                benhan.getTienSuBenh().toLowerCase().contains(searchTerm) ||
                benhan.getChanDoan().toLowerCase().contains(searchTerm) ||
                benhan.getRoomId().toLowerCase().contains(searchTerm)
            )
            .sorted((b1, b2) -> b1.getId().compareToIgnoreCase(b2.getId()))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Tạo mã bệnh án tự động theo định dạng BA001, BA002, BA003...
    public String generateNextBenhanId() {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Tìm ID cao nhất có định dạng BA###
            String sql = "SELECT id FROM benhAn WHERE id REGEXP '^BA[0-9]+$' ORDER BY CAST(SUBSTRING(id, 3) AS UNSIGNED) DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int nextNumber = 1; // Mặc định bắt đầu từ BA001
            
            if (rs.next()) {
                String lastId = rs.getString("id");
                // Lấy phần số từ ID (bỏ chữ BA)
                String numberPart = lastId.substring(2);
                nextNumber = Integer.parseInt(numberPart) + 1;
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Định dạng số thành BA001, BA002, etc.
            return String.format("BA%03d", nextNumber);
            
        } catch (Exception e) {
            System.out.println("Error generating benhan ID: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return "BA001"; // Fallback
        }
    }

    // Tìm bệnh án theo mã bệnh nhân
    public ArrayList<BenhAn> findBenhanByPatientId(String patientId) {
        return getBenhanList().stream()
            .filter(benhan -> benhan.getPatientId().equalsIgnoreCase(patientId))
            .sorted((b1, b2) -> b2.getNgayKham().compareTo(b1.getNgayKham())) // Sắp xếp theo ngày mới nhất
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Thống kê số lượng bệnh án theo phòng
    public long countBenhanByRoomId(String roomId) {
        return getBenhanList().stream()
            .filter(benhan -> benhan.getRoomId().equalsIgnoreCase(roomId))
            .count();
    }
}
