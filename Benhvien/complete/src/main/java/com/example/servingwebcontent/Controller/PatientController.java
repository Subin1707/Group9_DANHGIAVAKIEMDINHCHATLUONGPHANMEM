package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.database.PatientDatabase;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
public class PatientController {

    private final PatientDatabase patientDb = new PatientDatabase();

    // Trang chính: hiển thị danh sách + form thêm mới + tìm kiếm (sử dụng Stream)
    @GetMapping("/patients")
    public String getPatients(@RequestParam(value = "search", required = false) String search, Model model) {
        // Debug: Kiểm tra cấu trúc bảng (chỉ chạy 1 lần)
        // patientDb.debugPatientTable();
        
        List<Patient> patients;
        if (search != null && !search.isEmpty()) {
            // Sử dụng Stream API để tìm kiếm và sắp xếp
            patients = patientDb.searchAndSortPatients(search);
            model.addAttribute("search", search);
        } else {
            patients = patientDb.getPatientList();
        }
        
        // Tạo mã bệnh nhân mới cho form thêm
        String nextPatientId = patientDb.generateNextPatientId();
        model.addAttribute("nextPatientId", nextPatientId);
        
        model.addAttribute("patients", patients);
        return "patients";
    }

    // Thêm mới bệnh nhân
    @PostMapping("/patients")
    public String savePatient(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String dob,
            @RequestParam String gender,
            @RequestParam String address,
            @RequestParam String phone,
            Model model
    ) {
        try {
            // Nếu ID trống hoặc không hợp lệ, tạo ID mới
            if (id == null || id.trim().isEmpty()) {
                id = patientDb.generateNextPatientId();
            }
            
            // Kiểm tra ID đã tồn tại chưa
            Patient existingPatient = patientDb.findPatientById(id);
            if (existingPatient != null) {
                // ID đã tồn tại, hiển thị lỗi
                List<Patient> patients = patientDb.getPatientList();
                String nextPatientId = patientDb.generateNextPatientId();
                model.addAttribute("nextPatientId", nextPatientId);
                model.addAttribute("patients", patients);
                model.addAttribute("errorMessage", "Mã bệnh nhân '" + id + "' đã tồn tại! Vui lòng sử dụng mã khác.");
                
                return "patients";
            }
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            int year = calendar.get(Calendar.YEAR);
            int now = Calendar.getInstance().get(Calendar.YEAR);
            int age = now - year;

            Patient newPatient = new Patient(id, name, calendar, age, gender, address, phone);
            patientDb.saveOrUpdatePatient(newPatient);
            
            List<Patient> patients = patientDb.getPatientList();
            String nextPatientId = patientDb.generateNextPatientId();
            model.addAttribute("nextPatientId", nextPatientId);
            model.addAttribute("patients", patients);
            model.addAttribute("successMessage", "Đã thêm bệnh nhân '" + id + "' thành công!");
            
            return "patients";
        } catch (Exception e) {
            e.printStackTrace();
            List<Patient> patients = patientDb.getPatientList();
            String nextPatientId = patientDb.generateNextPatientId();
            model.addAttribute("nextPatientId", nextPatientId);
            model.addAttribute("patients", patients);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi thêm bệnh nhân: " + e.getMessage());
            return "patients";
        }
    }

    // Xóa bệnh nhân (KHÔNG redirect)
    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        try {
            boolean deleted = patientDb.deletePatientById(id);
            
            List<Patient> patients;
            if (search != null && !search.isEmpty()) {
                patients = patientDb.searchAndSortPatients(search);
                model.addAttribute("search", search);
            } else {
                patients = patientDb.getPatientList();
            }
            
            String nextPatientId = patientDb.generateNextPatientId();
            model.addAttribute("nextPatientId", nextPatientId);
            model.addAttribute("patients", patients);
            
            if (deleted) {
                model.addAttribute("successMessage", "Đã xóa bệnh nhân " + id + " và các bệnh án liên quan thành công!");
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy bệnh nhân " + id + " để xóa!");
            }
            
            return "patients";
        } catch (Exception e) {
            e.printStackTrace();
            
            List<Patient> patients;
            if (search != null && !search.isEmpty()) {
                patients = patientDb.searchAndSortPatients(search);
                model.addAttribute("search", search);
            } else {
                patients = patientDb.getPatientList();
            }
            
            String nextPatientId = patientDb.generateNextPatientId();
            model.addAttribute("nextPatientId", nextPatientId);
            model.addAttribute("patients", patients);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi xóa bệnh nhân " + id + ": " + e.getMessage());
            return "patients";
        }
    }

    // Hiển thị form sửa
    @GetMapping("/patients/edit/{id}")
    public String editPatientForm(@PathVariable String id, @RequestParam(value = "search", required = false) String search, Model model) {
        Patient patient = patientDb.findPatientById(id);
        model.addAttribute("editPatient", patient);
        List<Patient> patients;
        if (search != null && !search.isEmpty()) {
            patients = patientDb.searchAndSortPatients(search);
            model.addAttribute("search", search);
        } else {
            patients = patientDb.getPatientList();
        }
        
        String nextPatientId = patientDb.generateNextPatientId();
        model.addAttribute("nextPatientId", nextPatientId);
        model.addAttribute("patients", patients);
        return "patients";
    }

    // Cập nhật bệnh nhân (KHÔNG redirect)
    @PostMapping("/patients/update")
    public String updatePatient(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String dob,
            @RequestParam String gender,
            @RequestParam String address,
            @RequestParam String phone,Model model
    ) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            int year = calendar.get(Calendar.YEAR);
            int now = Calendar.getInstance().get(Calendar.YEAR);
            int age = now - year;

            Patient updatedPatient = new Patient(id, name, calendar, age, gender, address, phone);
            patientDb.saveOrUpdatePatient(updatedPatient);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật bệnh nhân!");
        }
        List<Patient> patients = patientDb.getPatientList();
        String nextPatientId = patientDb.generateNextPatientId();
        model.addAttribute("nextPatientId", nextPatientId);
        model.addAttribute("patients", patients);
        return "patients";
    }
}