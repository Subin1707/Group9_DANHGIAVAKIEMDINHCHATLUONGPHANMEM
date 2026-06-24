package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Room;
import com.example.servingwebcontent.database.RoomDatabase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RoomController {

    private final RoomDatabase roomDb = new RoomDatabase();

    // Trang chính: hiển thị danh sách + form thêm mới
    @GetMapping("/room")
    public String getRoomsPage(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Room> rooms;
        if (search != null && !search.isEmpty()) {
            rooms = roomDb.searchRooms(search);
            model.addAttribute("search", search);
        } else {
            rooms = roomDb.getAllRooms();
        }
        
        // Tạo mã phòng mới cho form thêm
        String nextRoomId = roomDb.generateNextRoomId();
        model.addAttribute("nextRoomId", nextRoomId);
        
        model.addAttribute("roomList", rooms);
        return "phongdieutri";
    }

    // Thêm mới phòng
    @PostMapping("/room")
    public String addRoom(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String doctorName,
            Model model
    ) {
        try {
            // Nếu ID trống hoặc không hợp lệ, tạo ID mới
            if (id == null || id.trim().isEmpty()) {
                id = roomDb.generateNextRoomId();
            }
            
            // Kiểm tra ID đã tồn tại chưa
            Room existingRoom = roomDb.findRoomById(id);
            if (existingRoom != null) {
                // ID đã tồn tại, hiển thị lỗi
                List<Room> rooms = roomDb.getAllRooms();
                String nextRoomId = roomDb.generateNextRoomId();
                model.addAttribute("nextRoomId", nextRoomId);
                model.addAttribute("roomList", rooms);
                model.addAttribute("errorMessage", "Mã phòng '" + id + "' đã tồn tại! Vui lòng sử dụng mã khác.");
                
                return "phongdieutri";
            }
            
            Room newRoom = new Room(id, name, doctorName);
            roomDb.saveOrUpdateRoom(newRoom);
            
            List<Room> rooms = roomDb.getAllRooms();
            String nextRoomId = roomDb.generateNextRoomId();
            model.addAttribute("nextRoomId", nextRoomId);
            model.addAttribute("roomList", rooms);
            model.addAttribute("successMessage", "Đã thêm phòng '" + id + "' thành công!");
            
            return "phongdieutri";
        } catch (Exception e) {
            e.printStackTrace();
            List<Room> rooms = roomDb.getAllRooms();
            String nextRoomId = roomDb.generateNextRoomId();
            model.addAttribute("nextRoomId", nextRoomId);
            model.addAttribute("roomList", rooms);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi thêm phòng: " + e.getMessage());
            
            return "phongdieutri";
        }
    }

    // Xóa phòng
    @GetMapping("/room/delete/{id}")
    public String deleteRoom(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        try {
            boolean deleted = roomDb.deleteRoom(id);
            
            List<Room> rooms;
            if (search != null && !search.isEmpty()) {
                rooms = roomDb.searchRooms(search);
                model.addAttribute("search", search);
            } else {
                rooms = roomDb.getAllRooms();
            }
            
            String nextRoomId = roomDb.generateNextRoomId();
            model.addAttribute("nextRoomId", nextRoomId);
            model.addAttribute("roomList", rooms);
            
            if (deleted) {
                model.addAttribute("successMessage", "Đã xóa phòng '" + id + "' thành công!");
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phòng '" + id + "' để xóa!");
            }
            
            return "phongdieutri";
        } catch (Exception e) {
            e.printStackTrace();
            
            List<Room> rooms;
            if (search != null && !search.isEmpty()) {
                rooms = roomDb.searchRooms(search);
                model.addAttribute("search", search);
            } else {
                rooms = roomDb.getAllRooms();
            }
            
            String nextRoomId = roomDb.generateNextRoomId();
            model.addAttribute("nextRoomId", nextRoomId);
            model.addAttribute("roomList", rooms);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi xóa phòng '" + id + "': " + e.getMessage());
            
            return "phongdieutri";
        }
    }

    // Hiển thị form sửa
    @GetMapping("/room/edit/{id}")
    public String editRoomForm(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        Room room = roomDb.findRoomById(id);
        model.addAttribute("editRoom", room);
        
        List<Room> rooms;
        if (search != null && !search.isEmpty()) {
            rooms = roomDb.searchRooms(search);
            model.addAttribute("search", search);
        } else {
            rooms = roomDb.getAllRooms();
        }
        
        String nextRoomId = roomDb.generateNextRoomId();
        model.addAttribute("nextRoomId", nextRoomId);
        model.addAttribute("roomList", rooms);
        return "phongdieutri";
    }

    // Cập nhật phòng
    @PostMapping("/room/update")
    public String updateRoom(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String doctorName,
            Model model
    ) {
        try {
            Room updatedRoom = new Room(id, name, doctorName);
            roomDb.saveOrUpdateRoom(updatedRoom);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật phòng!");
        }
        
        List<Room> rooms = roomDb.getAllRooms();
        String nextRoomId = roomDb.generateNextRoomId();
        model.addAttribute("nextRoomId", nextRoomId);
        model.addAttribute("roomList", rooms);
        return "phongdieutri";
    }

    // Tìm kiếm phòng
    @GetMapping("/room/search")
    public String searchRooms(@RequestParam(required = false) String keyword, Model model) {
        List<Room> rooms;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            rooms = roomDb.searchRooms(keyword);
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("searchResultCount", rooms.size());
        } else {
            rooms = roomDb.getAllRooms();
        }
        
        model.addAttribute("roomList", rooms);
        String nextRoomId = roomDb.generateNextRoomId();
        model.addAttribute("nextRoomId", nextRoomId);
        return "phongdieutri";
    }

    // API endpoints
    @GetMapping("/room/api/{id}")
    @ResponseBody
    public Room getRoomById(@PathVariable String id) {
        return roomDb.findRoomById(id);
    }

    @GetMapping("/room/count")
    @ResponseBody
    public int getRoomCount() {
        return roomDb.getRoomCount();
    }

    @PostMapping("/room/generate-id")
    @ResponseBody
    public String generateNextId() {
        return roomDb.generateNextRoomId();
    }
}
