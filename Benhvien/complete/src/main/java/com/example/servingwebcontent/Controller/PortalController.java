package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.Schedule;
import com.example.servingwebcontent.Model.Room;
import com.example.servingwebcontent.Model.Appointment;
import com.example.servingwebcontent.database.AppointmentDatabase;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.PatientDatabase;
import com.example.servingwebcontent.database.RoomDatabase;
import com.example.servingwebcontent.database.ScheduleDatabase;
import com.example.servingwebcontent.database.UserDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

@Controller
public class PortalController {

    private final PatientDatabase patientDb = new PatientDatabase();
    private final BenhanDatabase benhanDb = new BenhanDatabase();
    private final ScheduleDatabase scheduleDb = new ScheduleDatabase();
    private final RoomDatabase roomDb = new RoomDatabase();
    private final AppointmentDatabase appointmentDb = new AppointmentDatabase();
    private final UserDatabase userDb = new UserDatabase();

    private String sessionUsername(HttpSession session) {
        Object u = session.getAttribute(AuthConstants.SESSION_USERNAME);
        return u != null ? u.toString() : "";
    }

    private Role sessionRole(HttpSession session) {
        Object r = session.getAttribute(AuthConstants.SESSION_ROLE);
        return Role.fromStringOrDefault(r != null ? r.toString() : null, Role.USER);
    }

    private boolean isCustomer(HttpSession session) {
        return sessionRole(session) == Role.USER;
    }

    private String linkedPatientId(String username) {
        if (username == null || username.isBlank()) return username;
        return userDb.getLinkedPatientId(username);
    }

    private void addPortalHeader(Model model, String username) {
        String patientId = linkedPatientId(username);
        model.addAttribute("notificationCount", buildNotifications(patientId).size());
        model.addAttribute("displayName", username);
    }

    private List<Map<String, String>> buildNotifications(String patientId) {
        List<Map<String, String>> notifications = new ArrayList<>();
        if (patientId == null || patientId.isBlank()) return notifications;

        for (Appointment appointment : appointmentDb.findByPatientId(patientId)) {
            Map<String, String> item = new HashMap<>();
            String status = appointment.getStatus() != null
                    ? appointment.getStatus().toUpperCase() : "PENDING";
            String statusText = switch (status) {
                case "CONFIRMED" -> "đã được xác nhận";
                case "CANCELLED" -> "đã bị hủy";
                default -> "đang chờ xác nhận";
            };
            item.put("type", "appointment");
            item.put("title", "Lịch khám " + statusText);
            item.put("message", "Mã " + appointment.getId() + " • Phòng "
                    + (appointment.getRoomId() != null ? appointment.getRoomId() : "chưa xếp"));
            item.put("dateStr", appointment.getAppointmentTime() != null
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(appointment.getAppointmentTime()) : "");
            notifications.add(item);
        }

        for (Schedule schedule : scheduleDb.findScheduleByPatientId(patientId)) {
            Map<String, String> item = new HashMap<>();
            item.put("type", "medicine");
            item.put("title", "Lịch cấp thuốc");
            item.put("message", schedule.getTenthuoc() + " • " + schedule.getSoluong());
            item.put("dateStr", schedule.getDate() != null
                    ? new SimpleDateFormat("dd/MM/yyyy").format(schedule.getDate().getTime()) : "");
            notifications.add(item);
        }
        return notifications;
    }

    @GetMapping("/portal")
    public String portalRoot(HttpSession session) {
        if (!isCustomer(session)) return "redirect:/dashboard";
        return "redirect:/portal/profile";
    }

    @GetMapping("/portal/profile")
    public String profile(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        addPortalHeader(model, username);

        Patient p = patientDb.findPatientById(patientId);
        if (p != null) {
            model.addAttribute("fullName", p.getName());
            model.addAttribute("phone", p.getPhone());
            model.addAttribute("gender", p.getGender());
            model.addAttribute("address", p.getAddress());
            model.addAttribute("dob", formatDob(p.getDob()));
        } else {
            model.addAttribute("fullName", username);
            model.addAttribute("phone", "—");
            model.addAttribute("gender", "—");
            model.addAttribute("address", "—");
            model.addAttribute("dob", "—");
        }

        model.addAttribute("email", username + "@email.com");
        return "portal_profile";
    }

    @GetMapping("/portal/profile/edit")
    public String profileEdit(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        addPortalHeader(model, username);

        Patient p = patientDb.findPatientById(patientId);
        if (p != null) {
            model.addAttribute("fullName", p.getName());
            model.addAttribute("phone", p.getPhone());
            model.addAttribute("gender", p.getGender());
            model.addAttribute("address", p.getAddress());
            model.addAttribute("dobIso", formatDobIso(p.getDob()));
        } else {
            model.addAttribute("fullName", username);
            model.addAttribute("phone", "");
            model.addAttribute("gender", "Nam");
            model.addAttribute("address", "");
            model.addAttribute("dobIso", "2000-01-01");
        }
        return "portal_profile_edit";
    }

    @PostMapping("/portal/profile")
    public String profileUpdate(
        HttpSession session,
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam String dob,
        @RequestParam String gender,
        @RequestParam(required = false) String address
    ) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            int year = cal.get(Calendar.YEAR);
            int now = Calendar.getInstance().get(Calendar.YEAR);
            int age = Math.max(0, now - year);

            Patient updated = new Patient(patientId, name, cal, age, gender, address != null ? address : "", phone);
            patientDb.saveOrUpdatePatient(updated);
        } catch (Exception ignored) {
        }
        return "redirect:/portal/profile";
    }

    @GetMapping("/portal/password")
    public String passwordPage(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";
        String username = sessionUsername(session);
        addPortalHeader(model, username);
        return "portal_password";
    }

    @PostMapping("/portal/password")
    public String changePassword(
        HttpSession session,
        Model model,
        @RequestParam String currentPassword,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword
    ) {
        if (!isCustomer(session)) return "redirect:/dashboard";
        String username = sessionUsername(session);
        addPortalHeader(model, username);

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            return "portal_password";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            return "portal_password";
        }
        if (!userDb.authenticateUser(username, currentPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
            return "portal_password";
        }

        boolean ok = userDb.updatePassword(username, newPassword);
        if (!ok) {
            model.addAttribute("errorMessage", "Không thể đổi mật khẩu (lỗi DB).");
            return "portal_password";
        }
        model.addAttribute("successMessage", "Đổi mật khẩu thành công.");
        return "portal_password";
    }

    @GetMapping("/portal/benhan")
    public String benhan(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        addPortalHeader(model, username);

        ArrayList<BenhAn> list = benhanDb.findBenhanByPatientId(patientId);
        List<Map<String, String>> view = new ArrayList<>();
        for (BenhAn b : list) {
            Map<String, String> row = new HashMap<>();
            row.put("id", b.getId());
            row.put("chanDoan", b.getChanDoan());
            row.put("roomId", b.getRoomId());
            row.put("ngayKhamStr", formatDob(b.getNgayKham()));
            view.add(row);
        }
        model.addAttribute("benhans", view);
        return "portal_benhan";
    }

    @GetMapping("/portal/schedule")
    public String schedule(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        addPortalHeader(model, username);

        ArrayList<Schedule> list = scheduleDb.findScheduleByPatientId(patientId);
        List<Map<String, String>> view = new ArrayList<>();
        for (Schedule s : list) {
            Map<String, String> row = new HashMap<>();
            row.put("id", s.getId());
            row.put("tenthuoc", s.getTenthuoc());
            row.put("soluong", s.getSoluong());
            row.put("dateStr", formatDob(s.getDate()));
            view.add(row);
        }
        model.addAttribute("schedules", view);
        return "portal_schedule";
    }

    @GetMapping("/portal/notifications")
    public String notifications(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        addPortalHeader(model, username);
        model.addAttribute("notifications", buildNotifications(linkedPatientId(username)));
        return "portal_notifications";
    }

    @GetMapping("/portal/appointments/new")
    public String appointmentsNew(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        addPortalHeader(model, username);
        model.addAttribute("rooms", roomDb.getAllRooms());
        model.addAttribute("nextAppointmentId", appointmentDb.generateNextAppointmentId());
        return "portal_appointments_new";
    }

    @PostMapping("/portal/appointments")
    public String createAppointment(
        HttpSession session,
        Model model,
        @RequestParam String roomId,
        @RequestParam String appointmentTime,
        @RequestParam(required = false) String note
    ) {
        if (!isCustomer(session)) return "redirect:/dashboard";
        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);

        try {
            Timestamp ts = Timestamp.valueOf(appointmentTime.replace("T", " ") + ":00");
            String id = appointmentDb.generateNextAppointmentId();
            Appointment a = new Appointment(id, patientId, roomId != null && roomId.isBlank() ? null : roomId, ts, note != null ? note : "", "PENDING");
            boolean ok = appointmentDb.createAppointment(a);
            if (!ok) {
                addPortalHeader(model, username);
                model.addAttribute("rooms", roomDb.getAllRooms());
                model.addAttribute("errorMessage", "Không tạo được lịch khám. Hãy kiểm tra DB có bảng appointment và khóa ngoại patient/room.");
                return "portal_appointments_new";
            }
        } catch (Exception e) {
            addPortalHeader(model, username);
            model.addAttribute("rooms", roomDb.getAllRooms());
            model.addAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng chọn thời gian khám.");
            return "portal_appointments_new";
        }
        return "redirect:/portal/appointments";
    }

    @GetMapping("/portal/appointments")
    public String appointments(HttpSession session, Model model) {
        if (!isCustomer(session)) return "redirect:/dashboard";

        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        addPortalHeader(model, username);
        ArrayList<Appointment> list = appointmentDb.findByPatientId(patientId);
        List<Map<String, String>> view = new ArrayList<>();
        for (Appointment a : list) {
            Map<String, String> row = new HashMap<>();
            row.put("id", a.getId());
            row.put("roomId", a.getRoomId() != null ? a.getRoomId() : "—");
            row.put("timeStr", formatDateTime(a.getAppointmentTime()));
            row.put("status", a.getStatus() != null ? a.getStatus() : "PENDING");
            row.put("note", a.getNote() != null ? a.getNote() : "");
            view.add(row);
        }
        model.addAttribute("appointments", view);
        return "portal_appointments";
    }

    @PostMapping("/portal/appointments/cancel")
    public String cancelAppointment(HttpSession session, @RequestParam String id) {
        if (!isCustomer(session)) return "redirect:/dashboard";
        String username = sessionUsername(session);
        String patientId = linkedPatientId(username);
        Appointment a = appointmentDb.findById(id);
        if (a != null && patientId != null && patientId.equalsIgnoreCase(a.getPatientId())) {
            String st = a.getStatus() != null ? a.getStatus().toUpperCase() : "PENDING";
            if ("PENDING".equals(st)) {
                appointmentDb.updateStatus(id, "CANCELLED");
            }
        }
        return "redirect:/portal/appointments";
    }

    private String formatDob(Calendar cal) {
        if (cal == null) return "—";
        return new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
    }

    private String formatDobIso(Calendar cal) {
        if (cal == null) return "2000-01-01";
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    private String formatDateTime(Timestamp ts) {
        if (ts == null) return "—";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts);
    }
}
