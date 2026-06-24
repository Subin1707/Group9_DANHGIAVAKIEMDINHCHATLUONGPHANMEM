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

        // Admin mặc định
        if ("admin".equalsIgnoreCase(normalizedUsername) && "123456".equals(normalizedPassword)) {
            session.setAttribute(AuthConstants.SESSION_USERNAME, "admin");
            session.setAttribute(AuthConstants.SESSION_ROLE, Role.ADMIN.name());
            return "redirect:/dashboard";
        }

        // User/Staff trong DB
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

        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
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
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            model.addAttribute("port", serverPort);
            return "register";
        }

        if ("admin".equalsIgnoreCase(username)) {
            model.addAttribute("error", "Tên đăng nhập 'admin' đã được sử dụng");
            model.addAttribute("port", serverPort);
            return "register";
        }

        boolean success = userService.registerUser(username, password, Role.USER);
        if (success) {
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            model.addAttribute("port", serverPort);
            return "index";
        }

        model.addAttribute("error", "Tên đăng nhập đã tồn tại hoặc lỗi kết nối.");
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
            model.addAttribute("error", "Bạn không có quyền truy cập chức năng này.");
        }

        return "dashboard";
    }
}
