package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.User;
import com.example.servingwebcontent.database.UserDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Value("${server.port}")
    private String serverPort;

    private final UserDatabase userService = new UserDatabase();

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("port", serverPort);
        return "index";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam String username,
        @RequestParam String password,
        Model model,
        HttpSession session
    ) {
        String normalizedUsername = username != null ? username.trim() : "";
        String normalizedPassword = password != null ? password.trim() : "";

        if (normalizedUsername.isEmpty() || normalizedPassword.isEmpty()) {
            model.addAttribute("error", "Vui long nhap tai khoan va mat khau");
            model.addAttribute("port", serverPort);
            return "index";
        }

        if ("admin".equalsIgnoreCase(normalizedUsername) && "123456".equals(normalizedPassword)) {
            session.setAttribute(AuthConstants.SESSION_USERNAME, "admin");
            session.setAttribute(AuthConstants.SESSION_ROLE, Role.ADMIN.name());
            return "redirect:/dashboard";
        }

        User user = userService.authenticate(normalizedUsername, normalizedPassword);
        if (user != null) {
            session.setAttribute(AuthConstants.SESSION_USERNAME, user.getUsername());
            session.setAttribute(AuthConstants.SESSION_ROLE, (user.getRole() != null ? user.getRole() : Role.USER).name());
            Role role = user.getRole() != null ? user.getRole() : Role.USER;
            if (role == Role.USER) {
                return "redirect:/portal/profile";
            }
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Sai tai khoan hoac mat khau");
        model.addAttribute("port", serverPort);
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("port", serverPort);
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        Model model
    ) {
        String normalizedUsername = username != null ? username.trim() : "";
        String normalizedPassword = password != null ? password.trim() : "";
        String normalizedConfirmPassword = confirmPassword != null ? confirmPassword.trim() : "";

        if (normalizedUsername.isEmpty()) {
            model.addAttribute("error", "Ten dang nhap khong duoc de trong");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if (normalizedPassword.isEmpty()) {
            model.addAttribute("error", "Mat khau khong duoc de trong");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if (!normalizedPassword.equals(normalizedConfirmPassword)) {
            model.addAttribute("error", "Mat khau xac nhan khong khop");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if (normalizedPassword.length() < 6) {
            model.addAttribute("error", "Mat khau phai co it nhat 6 ky tu");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if ("admin".equalsIgnoreCase(normalizedUsername)) {
            model.addAttribute("error", "Ten dang nhap admin da duoc su dung");
            model.addAttribute("port", serverPort);
            return "register";
        }

        boolean success = userService.registerUser(normalizedUsername, normalizedPassword, Role.USER);
        if (success) {
            model.addAttribute("success", "Dang ky thanh cong! Vui long dang nhap.");
            model.addAttribute("port", serverPort);
            return "index";
        }

        model.addAttribute("error", "Ten dang nhap da ton tai hoac loi ket noi.");
        model.addAttribute("port", serverPort);
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(
        HttpSession session,
        Model model,
        @RequestParam(value = "denied", required = false) String denied
    ) {
        Object username = session.getAttribute(AuthConstants.SESSION_USERNAME);
        Object role = session.getAttribute(AuthConstants.SESSION_ROLE);

        model.addAttribute("username", username != null ? username.toString() : "");
        model.addAttribute("role", role != null ? role.toString() : Role.USER.name());
        if (denied != null) {
            model.addAttribute("error", "Ban khong co quyen truy cap chuc nang nay.");
        }

        return "dashboard";
    }
}
