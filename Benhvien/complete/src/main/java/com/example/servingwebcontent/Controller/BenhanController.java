package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Room;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.PatientDatabase;
import com.example.servingwebcontent.database.RoomDatabase;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
public class BenhanController {

    private final BenhanDatabase benhanDb = new BenhanDatabase();
    private final PatientDatabase patientDb = new PatientDatabase();
    private final RoomDatabase roomDb = new RoomDatabase();

    // Trang chính: hiển thị danh sách + form thêm mới + tìm kiếm (sử dụng Stream)
    @GetMapping("/benhan")
    public String getBenhan(@RequestParam(value = "search", required = false) String search, Model model) {
        List<BenhAn> benhans;
        if (search != null && !search.isEmpty()) {
            // Sử dụng Stream API để tìm kiếm và sắp xếp
            benhans = benhanDb.searchAndSortBenhan(search);
            model.addAttribute("search", search);
        } else {
            benhans = benhanDb.getBenhanList();
        }
        
        // Tạo mã bệnh án mới cho form thêm
        String nextBenhanId = benhanDb.generateNextBenhanId();
        model.addAttribute("nextBenhanId", nextBenhanId);
        
        // Lấy danh sách bệnh nhân và phòng điều trị để hiển thị trong dropdown
        List<Patient> patients = patientDb.getPatientList();
        List<Room> rooms = roomDb.getAllRooms();
        
        model.addAttribute("benhans", benhans);
        model.addAttribute("patients", patients);
        model.addAttribute("rooms", rooms);
        return "benhan";
    }

    // Thêm mới bệnh án
    @PostMapping("/benhan")
    public String saveBenhan(
            @RequestParam String id,
            @RequestParam String patientId,
            @RequestParam String ngayKham,
            @RequestParam String trieuChung,
            @RequestParam String tienSuBenh,
            @RequestParam String chanDoan,
            @RequestParam String roomId,
            Model model
    ) {
        try {
            // Nếu ID trống hoặc không hợp lệ, tạo ID mới
            if (id == null || id.trim().isEmpty()) {
                id = benhanDb.generateNextBenhanId();
            }
            
            // Kiểm tra ID bệnh án đã tồn tại chưa
            BenhAn existingBenhan = benhanDb.findBenhanById(id);
            if (existingBenhan != null) {
                // ID đã tồn tại, hiển thị lỗi
                List<BenhAn> benhans = benhanDb.getBenhanList();
                String nextBenhanId = benhanDb.generateNextBenhanId();
                model.addAttribute("nextBenhanId", nextBenhanId);
                model.addAttribute("benhans", benhans);
                
                List<Patient> patients = patientDb.getPatientList();
                List<Room> rooms = roomDb.getAllRooms();
                model.addAttribute("patients", patients);
                model.addAttribute("rooms", rooms);
                model.addAttribute("errorMessage", "Mã bệnh án '" + id + "' đã tồn tại! Vui lòng sử dụng mã khác.");
                
                return "benhan";
            }
            
            // Kiểm tra bệnh nhân đã có bệnh án chưa
            List<BenhAn> allBenhans = benhanDb.getBenhanList();
            for (BenhAn ba : allBenhans) {
                if (ba.getPatientId().equals(patientId)) {
                    // Bệnh nhân đã có bệnh án rồi, hiển thị lỗi
                    List<BenhAn> benhans = benhanDb.getBenhanList();
                    String nextBenhanId = benhanDb.generateNextBenhanId();
                    model.addAttribute("nextBenhanId", nextBenhanId);
                    model.addAttribute("benhans", benhans);
                    
                    List<Patient> patients = patientDb.getPatientList();
                    List<Room> rooms = roomDb.getAllRooms();
                    model.addAttribute("patients", patients);
                    model.addAttribute("rooms", rooms);
                    model.addAttribute("errorMessage", "Bệnh nhân '" + patientId + "' đã có bệnh án '" + ba.getId() + "' rồi! Một bệnh nhân chỉ được có một bệnh án.");
                    
                    return "benhan";
                }
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(ngayKham));

            BenhAn newBenhan = new BenhAn(id, patientId, calendar, trieuChung, tienSuBenh, chanDoan, roomId);
            benhanDb.saveOrUpdateBenhan(newBenhan);
            
            List<BenhAn> benhans = benhanDb.getBenhanList();
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            model.addAttribute("successMessage", "Đã thêm bệnh án '" + id + "' thành công!");
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        } catch (Exception e) {
            e.printStackTrace();
            List<BenhAn> benhans = benhanDb.getBenhanList();
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi thêm bệnh án: " + e.getMessage());
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        }
    }

    // Xóa bệnh án
    @GetMapping("/benhan/delete/{id}")
    public String deleteBenhan(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        try {
            boolean deleted = benhanDb.deleteBenhanById(id);
            
            List<BenhAn> benhans;
            if (search != null && !search.isEmpty()) {
                benhans = benhanDb.searchAndSortBenhan(search);
                model.addAttribute("search", search);
            } else {
                benhans = benhanDb.getBenhanList();
            }
            
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            
            if (deleted) {
                model.addAttribute("successMessage", "Đã xóa bệnh án '" + id + "' thành công!");
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy bệnh án '" + id + "' để xóa!");
            }
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        } catch (Exception e) {
            e.printStackTrace();
            
            List<BenhAn> benhans;
            if (search != null && !search.isEmpty()) {
                benhans = benhanDb.searchAndSortBenhan(search);
                model.addAttribute("search", search);
            } else {
                benhans = benhanDb.getBenhanList();
            }
            
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi xóa bệnh án '" + id + "': " + e.getMessage());
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        }
    }

    // Hiển thị form sửa
    @GetMapping("/benhan/edit/{id}")
    public String editBenhanForm(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        BenhAn benhan = benhanDb.findBenhanById(id);
        model.addAttribute("editBenhan", benhan);
        List<BenhAn> benhans;
        if (search != null && !search.isEmpty()) {
            benhans = benhanDb.searchAndSortBenhan(search);
            model.addAttribute("search", search);
        } else {
            benhans = benhanDb.getBenhanList();
        }
        
        String nextBenhanId = benhanDb.generateNextBenhanId();
        model.addAttribute("nextBenhanId", nextBenhanId);
        model.addAttribute("benhans", benhans);
        
        // Lấy lại danh sách bệnh nhân và phòng điều trị
        List<Patient> patients = patientDb.getPatientList();
        List<Room> rooms = roomDb.getAllRooms();
        model.addAttribute("patients", patients);
        model.addAttribute("rooms", rooms);
        
        return "benhan";
    }

    // Cập nhật bệnh án
    @PostMapping("/benhan/update")
    public String updateBenhan(
            @RequestParam String id,
            @RequestParam String patientId,
            @RequestParam String ngayKham,
            @RequestParam String trieuChung,
            @RequestParam String tienSuBenh,
            @RequestParam String chanDoan,
            @RequestParam String roomId,
            Model model
    ) {
        try {
            // Lấy bệnh án hiện tại để kiểm tra
            BenhAn currentBenhan = benhanDb.findBenhanById(id);
            
            // Nếu đổi bệnh nhân (patientId khác với bệnh án hiện tại)
            if (currentBenhan != null && !currentBenhan.getPatientId().equals(patientId)) {
                // Kiểm tra bệnh nhân mới đã có bệnh án chưa
                List<BenhAn> allBenhans = benhanDb.getBenhanList();
                for (BenhAn ba : allBenhans) {
                    if (ba.getPatientId().equals(patientId)) {
                        // Bệnh nhân mới đã có bệnh án rồi, hiển thị lỗi
                        List<BenhAn> benhans = benhanDb.getBenhanList();
                        String nextBenhanId = benhanDb.generateNextBenhanId();
                        model.addAttribute("nextBenhanId", nextBenhanId);
                        model.addAttribute("benhans", benhans);
                        model.addAttribute("editBenhan", currentBenhan); // Giữ lại form edit
                        
                        List<Patient> patients = patientDb.getPatientList();
                        List<Room> rooms = roomDb.getAllRooms();
                        model.addAttribute("patients", patients);
                        model.addAttribute("rooms", rooms);
                        model.addAttribute("errorMessage", "Bệnh nhân '" + patientId + "' đã có bệnh án '" + ba.getId() + "' rồi! Một bệnh nhân chỉ được có một bệnh án.");
                        
                        return "benhan";
                    }
                }
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(ngayKham));

            BenhAn updatedBenhan = new BenhAn(id, patientId, calendar, trieuChung, tienSuBenh, chanDoan, roomId);
            benhanDb.saveOrUpdateBenhan(updatedBenhan);
            
            List<BenhAn> benhans = benhanDb.getBenhanList();
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            model.addAttribute("successMessage", "Đã cập nhật bệnh án '" + id + "' thành công!");
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        } catch (Exception e) {
            e.printStackTrace();
            List<BenhAn> benhans = benhanDb.getBenhanList();
            String nextBenhanId = benhanDb.generateNextBenhanId();
            model.addAttribute("nextBenhanId", nextBenhanId);
            model.addAttribute("benhans", benhans);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật bệnh án: " + e.getMessage());
            
            // Lấy lại danh sách bệnh nhân và phòng điều trị
            List<Patient> patients = patientDb.getPatientList();
            List<Room> rooms = roomDb.getAllRooms();
            model.addAttribute("patients", patients);
            model.addAttribute("rooms", rooms);
            
            return "benhan";
        }
    }
}
