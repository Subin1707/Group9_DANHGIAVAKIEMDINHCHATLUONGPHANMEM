package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Schedule;
import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.database.ScheduleDatabase;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.PatientDatabase;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
public class ScheduleController {

    private final ScheduleDatabase scheduleDb = new ScheduleDatabase();
    private final BenhanDatabase benhanDb = new BenhanDatabase();
    private final PatientDatabase patientDb = new PatientDatabase();

    // Trang chính: hiển thị danh sách + form thêm mới + tìm kiếm (sử dụng Stream)
    @GetMapping("/schedule")
    public String getSchedules(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Schedule> schedules;
        if (search != null && !search.isEmpty()) {
            // Sử dụng Stream API để tìm kiếm và sắp xếp
            schedules = scheduleDb.searchAndSortSchedules(search);
            model.addAttribute("search", search);
        } else {
            schedules = scheduleDb.getScheduleList();
        }
        
        // Tạo mã lịch mới cho form thêm
        String nextScheduleId = scheduleDb.generateNextScheduleId();
        model.addAttribute("nextScheduleId", nextScheduleId);
        
        // Lấy danh sách bệnh án và bệnh nhân để hiển thị trong dropdown
        List<BenhAn> benhans = benhanDb.getBenhanList();
        List<Patient> patients = patientDb.getPatientList();
        
        model.addAttribute("scheduleList", schedules);
        model.addAttribute("benhans", benhans);
        model.addAttribute("patients", patients);
        return "lichcapthuoc";
    }

    // Thêm mới lịch cấp thuốc
    @PostMapping("/schedule")
    public String saveSchedule(
            @RequestParam String id,
            @RequestParam String benhanId,
            @RequestParam String patientId,
            @RequestParam String date,
            @RequestParam String tenthuoc,
            @RequestParam String soluong,
            Model model
    ) {
        try {
            // Nếu ID trống hoặc không hợp lệ, tạo ID mới
            if (id == null || id.trim().isEmpty()) {
                id = scheduleDb.generateNextScheduleId();
            }
            
            // Kiểm tra ID lịch đã tồn tại chưa
            Schedule existingSchedule = scheduleDb.findScheduleById(id);
            if (existingSchedule != null) {
                // ID đã tồn tại, hiển thị lỗi
                List<Schedule> schedules = scheduleDb.getScheduleList();
                String nextScheduleId = scheduleDb.generateNextScheduleId();
                model.addAttribute("nextScheduleId", nextScheduleId);
                model.addAttribute("scheduleList", schedules);
                
                List<BenhAn> benhans = benhanDb.getBenhanList();
                List<Patient> patients = patientDb.getPatientList();
                model.addAttribute("benhans", benhans);
                model.addAttribute("patients", patients);
                model.addAttribute("errorMessage", "Mã lịch cấp thuốc '" + id + "' đã tồn tại! Vui lòng sử dụng mã khác.");
                
                return "lichcapthuoc";
            }
            
            // Kiểm tra bệnh nhân đã có lịch cấp thuốc trong ngày này chưa
            Calendar inputCalendar = Calendar.getInstance();
            inputCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
            
            List<Schedule> allSchedules = scheduleDb.getScheduleList();
            for (Schedule sch : allSchedules) {
                if (sch.getPatientId().equals(patientId)) {
                    Calendar schCalendar = sch.getDate();
                    // So sánh ngày, tháng, năm
                    if (inputCalendar.get(Calendar.YEAR) == schCalendar.get(Calendar.YEAR) &&
                        inputCalendar.get(Calendar.MONTH) == schCalendar.get(Calendar.MONTH) &&
                        inputCalendar.get(Calendar.DAY_OF_MONTH) == schCalendar.get(Calendar.DAY_OF_MONTH)) {
                        
                        // Bệnh nhân đã có lịch cấp thuốc trong ngày này rồi
                        List<Schedule> schedules = scheduleDb.getScheduleList();
                        String nextScheduleId = scheduleDb.generateNextScheduleId();
                        model.addAttribute("nextScheduleId", nextScheduleId);
                        model.addAttribute("scheduleList", schedules);
                        
                        List<BenhAn> benhans = benhanDb.getBenhanList();
                        List<Patient> patients = patientDb.getPatientList();
                        model.addAttribute("benhans", benhans);
                        model.addAttribute("patients", patients);
                        model.addAttribute("errorMessage", "Bệnh nhân '" + patientId + "' đã có lịch cấp thuốc '" + sch.getId() + "' trong ngày " + date + " rồi! Một bệnh nhân chỉ được cấp thuốc một lần mỗi ngày.");
                        
                        return "lichcapthuoc";
                    }
                }
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));

            Schedule newSchedule = new Schedule(id, benhanId, patientId, calendar, tenthuoc, soluong);
            scheduleDb.saveOrUpdateSchedule(newSchedule);
            
            List<Schedule> schedules = scheduleDb.getScheduleList();
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            model.addAttribute("successMessage", "Đã thêm lịch cấp thuốc '" + id + "' thành công!");
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        } catch (Exception e) {
            e.printStackTrace();
            List<Schedule> schedules = scheduleDb.getScheduleList();
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi thêm lịch cấp thuốc: " + e.getMessage());
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        }
    }

    // Xóa lịch cấp thuốc
    @GetMapping("/schedule/delete/{id}")
    public String deleteSchedule(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        try {
            boolean deleted = scheduleDb.deleteScheduleById(id);
            
            List<Schedule> schedules;
            if (search != null && !search.isEmpty()) {
                schedules = scheduleDb.searchAndSortSchedules(search);
                model.addAttribute("search", search);
            } else {
                schedules = scheduleDb.getScheduleList();
            }
            
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            
            if (deleted) {
                model.addAttribute("successMessage", "Đã xóa lịch cấp thuốc '" + id + "' thành công!");
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy lịch cấp thuốc '" + id + "' để xóa!");
            }
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        } catch (Exception e) {
            e.printStackTrace();
            
            List<Schedule> schedules;
            if (search != null && !search.isEmpty()) {
                schedules = scheduleDb.searchAndSortSchedules(search);
                model.addAttribute("search", search);
            } else {
                schedules = scheduleDb.getScheduleList();
            }
            
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi xóa lịch cấp thuốc '" + id + "': " + e.getMessage());
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        }
    }

    // Hiển thị form sửa
    @GetMapping("/schedule/edit/{id}")
    public String editScheduleForm(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        Schedule schedule = scheduleDb.findScheduleById(id);
        model.addAttribute("editSchedule", schedule);
        List<Schedule> schedules;
        if (search != null && !search.isEmpty()) {
            schedules = scheduleDb.searchAndSortSchedules(search);
            model.addAttribute("search", search);
        } else {
            schedules = scheduleDb.getScheduleList();
        } 
        
        String nextScheduleId = scheduleDb.generateNextScheduleId();
        model.addAttribute("nextScheduleId", nextScheduleId);
        model.addAttribute("scheduleList", schedules);
        
        // Lấy lại danh sách bệnh án và bệnh nhân
        List<BenhAn> benhans = benhanDb.getBenhanList();
        List<Patient> patients = patientDb.getPatientList();
        model.addAttribute("benhans", benhans);
        model.addAttribute("patients", patients);
        
        return "lichcapthuoc";
    }

    // Cập nhật lịch cấp thuốc
    @PostMapping("/schedule/update")
    public String updateSchedule(
            @RequestParam String id,
            @RequestParam String benhanId,
            @RequestParam String patientId,
            @RequestParam String date,
            @RequestParam String tenthuoc,
            @RequestParam String soluong,
            Model model
    ) {
        try {
            // Lấy lịch hiện tại để kiểm tra
            Schedule currentSchedule = scheduleDb.findScheduleById(id);
            
            if (currentSchedule != null) {
                // Kiểm tra nếu đổi bệnh nhân hoặc ngày
                Calendar inputCalendar = Calendar.getInstance();
                inputCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                
                boolean patientChanged = !currentSchedule.getPatientId().equals(patientId);
                boolean dateChanged = !isSameDate(currentSchedule.getDate(), inputCalendar);
                
                // Nếu có thay đổi bệnh nhân hoặc ngày, cần kiểm tra trùng lặp
                if (patientChanged || dateChanged) {
                    List<Schedule> allSchedules = scheduleDb.getScheduleList();
                    for (Schedule sch : allSchedules) {
                        // Bỏ qua chính lịch đang được update
                        if (sch.getId().equals(id)) continue;
                        
                        if (sch.getPatientId().equals(patientId)) {
                            Calendar schCalendar = sch.getDate();
                            // So sánh ngày, tháng, năm
                            if (inputCalendar.get(Calendar.YEAR) == schCalendar.get(Calendar.YEAR) &&
                                inputCalendar.get(Calendar.MONTH) == schCalendar.get(Calendar.MONTH) &&
                                inputCalendar.get(Calendar.DAY_OF_MONTH) == schCalendar.get(Calendar.DAY_OF_MONTH)) {
                                
                                // Bệnh nhân đã có lịch cấp thuốc trong ngày này rồi
                                List<Schedule> schedules = scheduleDb.getScheduleList();
                                String nextScheduleId = scheduleDb.generateNextScheduleId();
                                model.addAttribute("nextScheduleId", nextScheduleId);
                                model.addAttribute("scheduleList", schedules);
                                model.addAttribute("editSchedule", currentSchedule); // Giữ lại form edit
                                
                                List<BenhAn> benhans = benhanDb.getBenhanList();
                                List<Patient> patients = patientDb.getPatientList();
                                model.addAttribute("benhans", benhans);
                                model.addAttribute("patients", patients);
                                model.addAttribute("errorMessage", "Bệnh nhân '" + patientId + "' đã có lịch cấp thuốc '" + sch.getId() + "' trong ngày " + date + " rồi! Một bệnh nhân chỉ được cấp thuốc một lần mỗi ngày.");
                                
                                return "lichcapthuoc";
                            }
                        }
                    }
                }
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));

            Schedule updatedSchedule = new Schedule(id, benhanId, patientId, calendar, tenthuoc, soluong);
            scheduleDb.saveOrUpdateSchedule(updatedSchedule);
            
            List<Schedule> schedules = scheduleDb.getScheduleList();
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            model.addAttribute("successMessage", "Đã cập nhật lịch cấp thuốc '" + id + "' thành công!");
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        } catch (Exception e) {
            e.printStackTrace();
            List<Schedule> schedules = scheduleDb.getScheduleList();
            String nextScheduleId = scheduleDb.generateNextScheduleId();
            model.addAttribute("nextScheduleId", nextScheduleId);
            model.addAttribute("scheduleList", schedules);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật lịch cấp thuốc: " + e.getMessage());
            
            // Lấy lại danh sách bệnh án và bệnh nhân
            List<BenhAn> benhans = benhanDb.getBenhanList();
            List<Patient> patients = patientDb.getPatientList();
            model.addAttribute("benhans", benhans);
            model.addAttribute("patients", patients);
            
            return "lichcapthuoc";
        }
    }
    
    // Helper method để so sánh ngày
    private boolean isSameDate(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
