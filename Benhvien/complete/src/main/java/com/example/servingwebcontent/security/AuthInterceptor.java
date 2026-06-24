package com.example.servingwebcontent.security;

import com.example.servingwebcontent.Model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private boolean isPublicPath(String path) {
        if (path == null) return true;
        return path.equals("/")
            || path.startsWith("/login")
            || path.startsWith("/register")
            || path.startsWith("/error")
            || path.startsWith("/css")
            || path.startsWith("/js")
            || path.startsWith("/images")
            || path.startsWith("/asset")
            || path.endsWith(".css")
            || path.endsWith(".js")
            || path.endsWith(".png")
            || path.endsWith(".jpg")
            || path.endsWith(".jpeg")
            || path.endsWith(".webp")
            || path.endsWith(".svg")
            || path.endsWith(".ico");
    }

    private boolean requiresStaffOrAdmin(String path) {
        if (path == null) return false;
        return path.startsWith("/patients")
            || path.startsWith("/benhan")
            || path.startsWith("/schedule")
            || path.startsWith("/room")
            || path.startsWith("/admin");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        String username = (String) session.getAttribute(AuthConstants.SESSION_USERNAME);
        String roleRaw = (String) session.getAttribute(AuthConstants.SESSION_ROLE);
        if (username == null || username.isBlank()) {
            response.sendRedirect("/login");
            return false;
        }

        Role role = Role.fromStringOrDefault(roleRaw, Role.USER);
        if (requiresStaffOrAdmin(path) && !(role == Role.ADMIN || role == Role.STAFF)) {
            response.sendRedirect("/dashboard?denied=1");
            return false;
        }

        return true;
    }
}

