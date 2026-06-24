package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.database.UserDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminUserController {

    private final UserDatabase userService = new UserDatabase();

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute(AuthConstants.SESSION_ROLE);
        return role != null && Role.fromStringOrDefault(role.toString(), Role.USER) == Role.ADMIN;
    }

    @GetMapping("/admin/staff")
    public String staffForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard?denied=1";
        }
        return "admin_staff";
    }

    @PostMapping("/admin/staff")
    public String createStaff(
        HttpSession session,
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard?denied=1";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            return "admin_staff";
        }

        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
            return "admin_staff";
        }

        if ("admin".equalsIgnoreCase(username)) {
            model.addAttribute("errorMessage", "Không thể tạo user với username 'admin'");
            return "admin_staff";
        }

        boolean success = userService.registerUser(username, password, Role.STAFF);
        if (success) {
            model.addAttribute("successMessage", "Đã tạo tài khoản nhân viên thành công: " + username);
        } else {
            model.addAttribute("errorMessage", "Không thể tạo tài khoản (trùng username hoặc lỗi DB).");
        }

        return "admin_staff";
    }
}

