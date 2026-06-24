package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.servingwebcontent.Model.Room;

public class RoomDatabase {

    // Database connection configuration - sử dụng database hospital giống như patient
    // Debug flag
    private static final boolean DEBUG = true;

    private void debug(String message) {
        if (DEBUG) {
            System.out.println("[Room Database] " + message);
        }
    }

    private Connection getConnection() throws Exception {
        return DatabaseConfig.getConnection();
    }

    // Tự động sinh mã phòng tiếp theo (R001, R002, ...)
    public String generateNextRoomId() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM room ORDER BY id DESC LIMIT 1")) {
            
            if (rs.next()) {
                String lastId = rs.getString("id");
                debug("Last Room ID found: " + lastId);
                
                if (lastId != null && lastId.startsWith("R") && lastId.length() > 1) {
                    try {
                        int number = Integer.parseInt(lastId.substring(1));
                        String nextId = "R" + String.format("%03d", number + 1);
                        debug("Generated next Room ID: " + nextId);
                        return nextId;
                    } catch (NumberFormatException e) {
                        debug("Error parsing last ID, using default");
                    }
                }
            }
            
            debug("No previous Room found, starting with R001");
            return "R001";
            
        } catch (Exception e) {
            debug("Error generating Room ID: " + e.getMessage());
            e.printStackTrace();
            return "R001";
        }
    }

    // Lấy tất cả phòng
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM room ORDER BY id")) {

            while (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                rooms.add(room);
            }
            
            debug("Retrieved " + rooms.size() + " rooms from database");
            
        } catch (Exception e) {
            debug("Error retrieving rooms: " + e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

    // Backward compatibility
    public ArrayList<Room> getRoomList() {
        return new ArrayList<>(getAllRooms());
    }

    // Tìm phòng theo ID
    public Room findRoomById(String id) {
        if (id == null || id.trim().isEmpty()) {
            debug("Invalid room ID provided");
            return null;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM room WHERE id = ?")) {
            
            pstmt.setString(1, id.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Room room = createRoomFromResultSet(rs);
                debug("Found room: " + room.getId());
                return room;
            }
            
            debug("Room not found with ID: " + id);
            return null;
            
        } catch (Exception e) {
            debug("Error finding room by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Thêm phòng mới
    public boolean addRoom(Room room) {
        if (room == null) {
            debug("Cannot add null room");
            return false;
        }

        // Validate required fields
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            debug("Room name is required");
            return false;
        }

        if (room.getDoctorName() == null || room.getDoctorName().trim().isEmpty()) {
            debug("Doctor name is required");
            return false;
        }

        // Auto-generate ID if not provided or empty
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            room.setId(generateNextRoomId());
        }

        // Check for duplicate ID
        if (findRoomById(room.getId()) != null) {
            debug("Room with ID " + room.getId() + " already exists");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO room (id, name, doctorName) VALUES (?, ?, ?)")) {
            
            pstmt.setString(1, room.getId().trim());
            pstmt.setString(2, room.getName().trim());
            pstmt.setString(3, room.getDoctorName().trim());
            
            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                debug("Successfully added room: " + room.getId());
            } else {
                debug("Failed to add room: " + room.getId());
            }
            
            return success;
            
        } catch (Exception e) {
            debug("Error adding room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật phòng
    public boolean updateRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            debug("Invalid room or room ID for update");
            return false;
        }

        // Validate required fields
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            debug("Room name is required for update");
            return false;
        }

        if (room.getDoctorName() == null || room.getDoctorName().trim().isEmpty()) {
            debug("Doctor name is required for update");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE room SET name = ?, doctorName = ? WHERE id = ?")) {
            
            pstmt.setString(1, room.getName().trim());
            pstmt.setString(2, room.getDoctorName().trim());
            pstmt.setString(3, room.getId().trim());
            
            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                debug("Successfully updated room: " + room.getId());
            } else {
                debug("No room found to update with ID: " + room.getId());
            }
            
            return success;
            
        } catch (Exception e) {
            debug("Error updating room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phòng
    public boolean deleteRoom(String id) {
        if (id == null || id.trim().isEmpty()) {
            debug("Invalid room ID for deletion");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM room WHERE id = ?")) {
            
            pstmt.setString(1, id.trim());
            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                debug("Successfully deleted room: " + id);
            } else {
                debug("No room found to delete with ID: " + id);
            }
            
            return success;
            
        } catch (Exception e) {
            debug("Error deleting room: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm phòng theo từ khóa (ID, tên phòng, hoặc tên bác sĩ)
    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRooms();
        }
        
        List<Room> allRooms = getAllRooms();
        String searchTerm = keyword.trim().toLowerCase();
        
        return allRooms.stream()
            .filter(room -> 
                room.getId().toLowerCase().contains(searchTerm) ||
                room.getName().toLowerCase().contains(searchTerm) ||
                room.getDoctorName().toLowerCase().contains(searchTerm)
            )
            .collect(Collectors.toList());
    }

    // Kiểm tra phòng có tồn tại không
    public boolean roomExists(String id) {
        return findRoomById(id) != null;
    }

    // Lấy số lượng phòng
    public int getRoomCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM room")) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (Exception e) {
            debug("Error getting room count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Utility method để tạo Room object từ ResultSet
    private Room createRoomFromResultSet(ResultSet rs) throws Exception {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String doctorName = rs.getString("doctorName");
        
        return new Room(id, name, doctorName);
    }

    // Xóa tất cả phòng (để testing)
    public boolean deleteAllRooms() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rowsAffected = stmt.executeUpdate("DELETE FROM room");
            debug("Deleted " + rowsAffected + " rooms from database");
            return true;
            
        } catch (Exception e) {
            debug("Error deleting all rooms: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lưu hoặc cập nhật phòng (giống như PatientDatabase)
    public boolean saveOrUpdateRoom(Room room) {
        if (room == null) {
            debug("Cannot save null room");
            return false;
        }

        // Validate required fields
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            debug("Room name is required");
            return false;
        }

        if (room.getDoctorName() == null || room.getDoctorName().trim().isEmpty()) {
            debug("Doctor name is required");
            return false;
        }

        // Auto-generate ID if not provided or empty
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            room.setId(generateNextRoomId());
        }

        try (Connection conn = getConnection()) {
            // Kiểm tra tồn tại
            boolean exists = roomExists(room.getId());
            
            if (exists) {
                // Update existing room
                String sql = "UPDATE room SET name = ?, doctorName = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, room.getName().trim());
                    stmt.setString(2, room.getDoctorName().trim());
                    stmt.setString(3, room.getId().trim());
                    
                    int result = stmt.executeUpdate();
                    boolean success = result > 0;
                    
                    if (success) {
                        debug("Successfully updated room: " + room.getId());
                    } else {
                        debug("Failed to update room: " + room.getId());
                    }
                    
                    return success;
                }
            } else {
                // Insert new room
                String sql = "INSERT INTO room (id, name, doctorName) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, room.getId().trim());
                    stmt.setString(2, room.getName().trim());
                    stmt.setString(3, room.getDoctorName().trim());
                    
                    int result = stmt.executeUpdate();
                    boolean success = result > 0;
                    
                    if (success) {
                        debug("Successfully added room: " + room.getId());
                    } else {
                        debug("Failed to add room: " + room.getId());
                    }
                    
                    return success;
                }
            }
            
        } catch (Exception e) {
            debug("Error in saveOrUpdateRoom: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
