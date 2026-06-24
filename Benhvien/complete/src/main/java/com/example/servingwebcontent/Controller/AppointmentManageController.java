package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Appointment;
import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.database.AppointmentDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AppointmentManageController {

    private final AppointmentDatabase appointmentDb = new AppointmentDatabase();

    private Role role(HttpSession session) {
        Object r = session.getAttribute(AuthConstants.SESSION_ROLE);
        return Role.fromStringOrDefault(r != null ? r.toString() : null, Role.USER);
    }

    private boolean isStaffOrAdmin(HttpSession session) {
        Role r = role(session);
        return r == Role.ADMIN || r == Role.STAFF;
    }

    @GetMapping("/appointments/manage")
    public String manage(HttpSession session, Model model) {
        if (!isStaffOrAdmin(session)) return "redirect:/dashboard?denied=1";

        ArrayList<Appointment> list = appointmentDb.getAllAppointments();
        if (list.isEmpty() && appointmentDb.getLastError() != null) {
            model.addAttribute("errorMessage", appointmentDb.getLastError());
        }
        List<Map<String, String>> view = new ArrayList<>();
        for (Appointment a : list) {
            Map<String, String> row = new HashMap<>();
            row.put("id", a.getId());
            row.put("patientId", a.getPatientId());
            row.put("roomId", a.getRoomId() != null ? a.getRoomId() : "—");
            row.put("timeStr", a.getAppointmentTime() != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(a.getAppointmentTime()) : "—");
            row.put("note", a.getNote() != null ? a.getNote() : "");
            row.put("status", a.getStatus() != null ? a.getStatus() : "PENDING");
            view.add(row);
        }
        model.addAttribute("appointments", view);
        model.addAttribute("isAdmin", role(session) == Role.ADMIN);
        return "appointments_manage";
    }

    @PostMapping("/appointments/manage/status")
    public String updateStatus(HttpSession session, @RequestParam String id, @RequestParam String status) {
        if (!isStaffOrAdmin(session)) return "redirect:/dashboard?denied=1";
        String st = status != null ? status.trim().toUpperCase() : "PENDING";
        if (!st.equals("PENDING") && !st.equals("CONFIRMED") && !st.equals("CANCELLED")) {
            st = "PENDING";
        }
        appointmentDb.updateStatus(id, st);
        return "redirect:/appointments/manage";
    }

    @PostMapping("/appointments/manage/delete")
    public String delete(HttpSession session, @RequestParam String id) {
        if (role(session) != Role.ADMIN) return "redirect:/dashboard?denied=1";
        appointmentDb.deleteById(id);
        return "redirect:/appointments/manage";
    }
}
