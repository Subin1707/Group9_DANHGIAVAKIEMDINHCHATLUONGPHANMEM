package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import com.example.servingwebcontent.Model.Schedule;

public class ScheduleDatabase {

    // Debug flag
    private static final boolean DEBUG = true;

    private void debug(String message) {
        if (DEBUG) {
            System.out.println("[Schedule Database] " + message);
        }
    }

    public ArrayList<Schedule> getScheduleList() {
        ArrayList<Schedule> schedules = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM schedule");

            while (rs.next()) {
                String id = rs.getString("id");
                String benhanId = rs.getString("benhanId");
                String patientId = rs.getString("patientId");
                java.sql.Date dateSql = rs.getDate("date");
                Calendar date = Calendar.getInstance();
                if (dateSql != null) {
                    date.setTime(dateSql);
                }
                String tenthuoc = rs.getString("tenthuoc");
                String soluong = rs.getString("soluong");

                Schedule schedule = new Schedule(id, benhanId, patientId, date, tenthuoc, soluong);
                schedules.add(schedule);
            }
            
            debug("Retrieved " + schedules.size() + " schedules from database");
        } catch (Exception e) {
            debug("Error retrieving schedules: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return schedules;
    }

    public boolean saveOrUpdateSchedule(Schedule s) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Kiểm tra tồn tại
            String checkSql = "SELECT COUNT(*) FROM schedule WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, s.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;
            rs.close();
            checkStmt.close();

            if (exists) {
                // Update
                String sql = "UPDATE schedule SET benhanId = ?, patientId = ?, date = ?, tenthuoc = ?, soluong = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, s.getBenhanId());
                stmt.setString(2, s.getPatientId());
                stmt.setDate(3, new java.sql.Date(s.getDate().getTimeInMillis()));
                stmt.setString(4, s.getTenthuoc());
                stmt.setString(5, s.getSoluong());
                stmt.setString(6, s.getId());
                
                int result = stmt.executeUpdate();
                stmt.close();
                debug("Updated schedule: " + s.getId() + " (rows affected: " + result + ")");
            } else {
                // Insert
                String sql = "INSERT INTO schedule (id, benhanId, patientId, date, tenthuoc, soluong) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, s.getId());
                stmt.setString(2, s.getBenhanId());
                stmt.setString(3, s.getPatientId());
                stmt.setDate(4, new java.sql.Date(s.getDate().getTimeInMillis()));
                stmt.setString(5, s.getTenthuoc());
                stmt.setString(6, s.getSoluong());
                
                int result = stmt.executeUpdate();
                stmt.close();
                debug("Inserted schedule: " + s.getId() + " (rows affected: " + result + ")");
            }
            conn.close();
            return true;
        } catch (Exception e) {
            debug("Error in saveOrUpdateSchedule: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return false;
        }
    }

    public boolean deleteScheduleById(String id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Debug: Kiểm tra xem lịch có tồn tại không
            String checkSql = "SELECT COUNT(*) FROM schedule WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            debug("Found " + count + " schedule(s) with ID: " + id);
            
            if (count == 0) {
                debug("No schedule found with ID: " + id);
                conn.close();
                return false;
            }
            
            // Xóa schedule
            String deleteSql = "DELETE FROM schedule WHERE id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, id);
            int result = deleteStmt.executeUpdate();
            deleteStmt.close();
            conn.close();
            
            debug("Deleted " + result + " row(s)");
            return result > 0;
        } catch (Exception e) {
            debug("Error in deleteScheduleById: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return false;
        }
    }

    public Schedule findScheduleById(String id) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            String sql = "SELECT * FROM schedule WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String benhanId = rs.getString("benhanId");
                String patientId = rs.getString("patientId");
                java.sql.Date dateSql = rs.getDate("date");
                Calendar date = Calendar.getInstance();
                if (dateSql != null) {
                    date.setTime(dateSql);
                }
                String tenthuoc = rs.getString("tenthuoc");
                String soluong = rs.getString("soluong");
                
                Schedule schedule = new Schedule(id, benhanId, patientId, date, tenthuoc, soluong);
                rs.close();
                stmt.close();
                conn.close();
                debug("Found schedule: " + id);
                return schedule;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            debug("Error finding schedule by ID: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
        }
        debug("Schedule not found with ID: " + id);
        return null;
    }

    // Tìm kiếm sử dụng Stream API
    public ArrayList<Schedule> searchAndSortSchedules(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getScheduleList().stream()
                .sorted((s1, s2) -> s1.getId().compareToIgnoreCase(s2.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        
        return getScheduleList().stream()
            .filter(schedule -> 
                schedule.getId().toLowerCase().contains(searchTerm) ||
                schedule.getBenhanId().toLowerCase().contains(searchTerm) ||
                schedule.getPatientId().toLowerCase().contains(searchTerm) ||
                schedule.getTenthuoc().toLowerCase().contains(searchTerm) ||
                schedule.getSoluong().toLowerCase().contains(searchTerm)
            )
            .sorted((s1, s2) -> s1.getId().compareToIgnoreCase(s2.getId()))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Tạo mã lịch tự động theo định dạng BT001, BT002, BT003...
    public String generateNextScheduleId() {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            
            // Tìm ID cao nhất có định dạng BT###
            String sql = "SELECT id FROM schedule WHERE id REGEXP '^BT[0-9]+$' ORDER BY CAST(SUBSTRING(id, 3) AS UNSIGNED) DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int nextNumber = 1; // Mặc định bắt đầu từ BT001
            
            if (rs.next()) {
                String lastId = rs.getString("id");
                if (lastId != null && lastId.startsWith("BT")) {
                    try {
                        int lastNumber = Integer.parseInt(lastId.substring(2));
                        nextNumber = lastNumber + 1;
                        debug("Last schedule ID: " + lastId + ", next number: " + nextNumber);
                    } catch (NumberFormatException e) {
                        debug("Error parsing last ID, using default: " + e.getMessage());
                    }
                }
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Định dạng số thành BT001, BT002, etc.
            String nextId = String.format("BT%03d", nextNumber);
            debug("Generated next schedule ID: " + nextId);
            return nextId;
            
        } catch (Exception e) {
            debug("Error generating schedule ID: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return "BT001"; // Fallback
        }
    }

    // Tìm lịch theo mã bệnh án
    public ArrayList<Schedule> findScheduleByBenhanId(String benhanId) {
        return getScheduleList().stream()
            .filter(schedule -> schedule.getBenhanId().equalsIgnoreCase(benhanId))
            .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate())) // Sắp xếp theo ngày mới nhất
            .collect(Collectors.toCollection(ArrayList::new));
    }

    // Tìm lịch theo mã bệnh nhân
    public ArrayList<Schedule> findScheduleByPatientId(String patientId) {
        return getScheduleList().stream()
            .filter(schedule -> schedule.getPatientId().equalsIgnoreCase(patientId))
            .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate())) // Sắp xếp theo ngày mới nhất
            .collect(Collectors.toCollection(ArrayList::new));
    }
}
