package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import com.example.servingwebcontent.Model.Patient;

public class PatientDatabase {

    public ArrayList<Patient> getPatientList() {
        ArrayList<Patient> patients = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM patient");
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                java.sql.Date dobSql = rs.getDate("dob");
                Calendar dob = Calendar.getInstance();
                if (dobSql != null) {
                    dob.setTime(dobSql);
                }
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                Patient patient = new Patient(id, name, dob, age, gender, address, phone);
                patients.add(patient);
            }
        } catch (Exception e) {
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return patients;
    }

    public boolean saveOrUpdatePatient(Patient p) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            // Kiểm tra tồn tại
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM patient WHERE id = '" + p.getId() + "'");
            rs.next();
            boolean exists = rs.getInt(1) > 0;
            rs.close();
            checkStmt.close();

            if (exists) {
                // Update
                String sql = "UPDATE patient SET name='" + p.getName() +
                        "', dob='" + new java.sql.Date(p.getDob().getTimeInMillis()) +
                        "', age=" + p.getAge() +
                        ", gender='" + p.getGender() +
                        "', address='" + p.getAddress() +
                        "', phone='" + p.getPhone() +
                        "' WHERE id='" + p.getId() + "'";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            } else {
                // Insert
                String sql = "INSERT INTO patient (id, name, dob, age, gender, address, phone) VALUES (" +
                        "'" + p.getId() + "'," +
                        "'" + p.getName() + "'," +
                        "'" + new java.sql.Date(p.getDob().getTimeInMillis()) + "'," +
                        p.getAge() + "," +
                        "'" + p.getGender() + "'," +
                        "'" + p.getAddress() + "'," +
                        "'" + p.getPhone() + "')";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
            conn.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error in saveOrUpdatePatient");
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
            return false;
        }
    }
    public boolean deletePatientById(String id) {
    Connection conn = null;
    try {
        conn = DatabaseConfig.getConnection();
        
        // Debug: Kiểm tra xem bệnh nhân có tồn tại không
        String checkSql = "SELECT COUNT(*) FROM patient WHERE id = ?";
        java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, id);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        checkStmt.close();
        
        System.out.println("Debug: Found " + count + " patient(s) with ID: " + id);
        
        if (count == 0) {
            System.out.println("Debug: Patient not found!");
            conn.close();
            return false;
        }
        
        // Trước khi xóa bệnh nhân, phải xóa tất cả bệnh án liên quan
        System.out.println("Debug: Deleting related medical records first...");
        String deleteBenhAnSql = "DELETE FROM benhAn WHERE patientId = ?";
        java.sql.PreparedStatement deleteBenhAnStmt = conn.prepareStatement(deleteBenhAnSql);
        deleteBenhAnStmt.setString(1, id);
        int benhAnDeleted = deleteBenhAnStmt.executeUpdate();
        deleteBenhAnStmt.close();
        System.out.println("Debug: Deleted " + benhAnDeleted + " medical record(s)");
        
        // Bây giờ mới xóa bệnh nhân
        String deleteSql = "DELETE FROM patient WHERE id = ?";
        java.sql.PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setString(1, id);
        int result = deleteStmt.executeUpdate();
        deleteStmt.close();
        conn.close();
        
        System.out.println("Debug: Deleted " + result + " row(s)");
        return result > 0;
    } catch (Exception e) {
        System.out.println("Error in deletePatientById: " + e.getMessage());
        e.printStackTrace();
        try { if (conn != null) conn.close(); } catch (Exception ex) {}
        return false;
    }
}

public Patient findPatientById(String id) {
    Connection conn = null;
    try {
        conn = DatabaseConfig.getConnection();
        String sql = "SELECT * FROM patient WHERE id = ?";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            java.sql.Date dobSql = rs.getDate("dob");
            Calendar dob = Calendar.getInstance();
            if (dobSql != null) dob.setTime(dobSql);
            Patient patient = new Patient(
                    rs.getString("id"),
                    rs.getString("name"),
                    dob,
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("address"),
                    rs.getString("phone")
            );
            rs.close();
            stmt.close();
            conn.close();
            return patient;
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
public ArrayList<Patient> searchPatientsByName(String keyword) {
    ArrayList<Patient> patients = new ArrayList<>();
    Connection conn = null;
    try {
        conn = DatabaseConfig.getConnection();
        String sql = "SELECT * FROM patient WHERE name LIKE ?";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + keyword + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            java.sql.Date dobSql = rs.getDate("dob");
            Calendar dob = Calendar.getInstance();
            if (dobSql != null) dob.setTime(dobSql);
            int age = rs.getInt("age");
            String gender = rs.getString("gender");
            String address = rs.getString("address");
            String phone = rs.getString("phone");
            Patient patient = new Patient(id, name, dob, age, gender, address, phone);
            patients.add(patient);
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
        try { if (conn != null) conn.close(); } catch (Exception ex) {}
    }
    return patients;
}

// Tìm kiếm sử dụng Stream API (tìm kiếm trong memory)
public ArrayList<Patient> searchPatientsWithStream(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return getPatientList();
    }
    
    String searchTerm = keyword.toLowerCase().trim();
    
    return getPatientList().stream()
        .filter(patient -> 
            patient.getId().toLowerCase().contains(searchTerm) ||
            patient.getName().toLowerCase().contains(searchTerm) ||
            patient.getGender().toLowerCase().contains(searchTerm) ||
            patient.getAddress().toLowerCase().contains(searchTerm) ||
            patient.getPhone().toLowerCase().contains(searchTerm) ||
            String.valueOf(patient.getAge()).contains(searchTerm)
        )
        .collect(Collectors.toCollection(ArrayList::new));
}

// Tìm kiếm và sắp xếp theo tên sử dụng Stream
public ArrayList<Patient> searchAndSortPatients(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return getPatientList().stream()
            .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    String searchTerm = keyword.toLowerCase().trim();
    
    return getPatientList().stream()
        .filter(patient -> 
            patient.getId().toLowerCase().contains(searchTerm) ||
            patient.getName().toLowerCase().contains(searchTerm) ||
            patient.getGender().toLowerCase().contains(searchTerm) ||
            patient.getAddress().toLowerCase().contains(searchTerm) ||
            patient.getPhone().toLowerCase().contains(searchTerm) ||
            String.valueOf(patient.getAge()).contains(searchTerm)
        )
        .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
        .collect(Collectors.toCollection(ArrayList::new));
}

// Tìm bệnh nhân theo độ tuổi sử dụng Stream
public ArrayList<Patient> findPatientsByAgeRange(int minAge, int maxAge) {
    return getPatientList().stream()
        .filter(patient -> patient.getAge() >= minAge && patient.getAge() <= maxAge)
        .sorted((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()))
        .collect(Collectors.toCollection(ArrayList::new));
}

// Thống kê theo giới tính sử dụng Stream
public long countPatientsByGender(String gender) {
    return getPatientList().stream()
        .filter(patient -> patient.getGender().equalsIgnoreCase(gender))
        .count();
}

public ArrayList<Patient> searchPatientsGlobal(String keyword) {
    ArrayList<Patient> patients = new ArrayList<>();
    Connection conn = null;
    try {
        conn = DatabaseConfig.getConnection();
        String sql = "SELECT * FROM patient WHERE " +
                     "id LIKE ? OR " +
                     "name LIKE ? OR " +
                     "gender LIKE ? OR " +
                     "address LIKE ? OR " +
                     "phone LIKE ? OR " +
                     "CAST(age AS CHAR) LIKE ?";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        String searchPattern = "%" + keyword + "%";
        stmt.setString(1, searchPattern); // id
        stmt.setString(2, searchPattern); // name
        stmt.setString(3, searchPattern); // gender
        stmt.setString(4, searchPattern); // address
        stmt.setString(5, searchPattern); // phone
        stmt.setString(6, searchPattern); // age
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            java.sql.Date dobSql = rs.getDate("dob");
            Calendar dob = Calendar.getInstance();
            if (dobSql != null) dob.setTime(dobSql);
            int age = rs.getInt("age");
            String gender = rs.getString("gender");
            String address = rs.getString("address");
            String phone = rs.getString("phone");
            Patient patient = new Patient(id, name, dob, age, gender, address, phone);
            patients.add(patient);
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
        try { if (conn != null) conn.close(); } catch (Exception ex) {}
    }
    return patients;
}

// Tạo mã bệnh nhân tự động theo định dạng P001, P002, P003...
public String generateNextPatientId() {
    Connection conn = null;
    try {
        conn = DatabaseConfig.getConnection();
        
        // Tìm ID cao nhất có định dạng P###
        String sql = "SELECT id FROM patient WHERE id REGEXP '^P[0-9]+$' ORDER BY CAST(SUBSTRING(id, 2) AS UNSIGNED) DESC LIMIT 1";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        int nextNumber = 1; // Mặc định bắt đầu từ P001
        
        if (rs.next()) {
            String lastId = rs.getString("id");
            // Lấy phần số từ ID (bỏ chữ P)
            String numberPart = lastId.substring(1);
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        // Định dạng số thành P001, P002, etc.
        return String.format("P%03d", nextNumber);
        
    } catch (Exception e) {
        System.out.println("Error generating patient ID: " + e.getMessage());
        e.printStackTrace();
        try { if (conn != null) conn.close(); } catch (Exception ex) {}
        return "P001"; // Fallback
    }
}
}
