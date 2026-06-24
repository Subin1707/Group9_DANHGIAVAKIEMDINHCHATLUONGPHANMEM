package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.UserProfile;
import com.example.servingwebcontent.database.UserDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminAccountController {

    private final UserDatabase userService = new UserDatabase();

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute(AuthConstants.SESSION_ROLE);
        return role != null && Role.fromStringOrDefault(role.toString(), Role.USER) == Role.ADMIN;
    }

    @GetMapping("/admin/accounts")
    public String accounts(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";

        List<UserProfile> users = userService.listUserProfiles();
        model.addAttribute("users", users);
        return "admin_accounts";
    }

    @GetMapping("/admin/accounts/{username}")
    public String profile(@PathVariable String username, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";

        UserProfile profile = userService.findUserProfile(username);
        if (profile == null) {
            return "redirect:/admin/accounts";
        }
        model.addAttribute("profile", profile);
        return "admin_profile";
    }

    @PostMapping("/admin/accounts/{username}/role")
    public String updateRole(
        @PathVariable String username,
        @RequestParam String role,
        HttpSession session,
        Model model
    ) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";
        if ("admin".equalsIgnoreCase(username)) return "redirect:/admin/accounts/" + username;

        boolean ok = userService.updateUserRole(username, Role.fromStringOrDefault(role, Role.USER));
        if (!ok) {
            model.addAttribute("errorMessage", "Không thể cập nhật role (DB chưa có cột role hoặc lỗi DB).");
        } else {
            model.addAttribute("successMessage", "Đã cập nhật role cho " + username);
        }
        return profile(username, session, model);
    }

    @PostMapping("/admin/accounts/{username}/password")
    public String resetPassword(
        @PathVariable String username,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        HttpSession session,
        Model model
    ) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            return profile(username, session, model);
        }
        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            return profile(username, session, model);
        }

        boolean ok = userService.updatePassword(username, password);
        if (!ok) {
            model.addAttribute("errorMessage", "Không thể reset mật khẩu (lỗi DB).");
        } else {
            model.addAttribute("successMessage", "Đã reset mật khẩu cho " + username);
        }
        return profile(username, session, model);
    }

    @PostMapping("/admin/accounts/{username}/patient")
    public String updatePatientLink(
        @PathVariable String username,
        @RequestParam(required = false) String patientId,
        HttpSession session,
        Model model
    ) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";
        if ("admin".equalsIgnoreCase(username)) return "redirect:/admin/accounts/" + username;

        String value = patientId != null && !patientId.isBlank() ? patientId.trim() : null;
        boolean ok = userService.updateLinkedPatientId(username, value);
        if (!ok) {
            model.addAttribute("errorMessage", "Không thể cập nhật patientId (DB chưa có cột patientId hoặc lỗi DB).");
        } else {
            model.addAttribute("successMessage", "Đã cập nhật patientId cho " + username);
        }
        return profile(username, session, model);
    }

    @PostMapping("/admin/accounts/{username}/delete")
    public String delete(@PathVariable String username, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/dashboard?denied=1";
        userService.deleteUser(username);
        return "redirect:/admin/accounts";
    }
}
