package com.example.servingwebcontent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CsrfTokenFilter extends OncePerRequestFilter {
    public static final String SESSION_ATTRIBUTE = CsrfTokenFilter.class.getName() + ".TOKEN";
    public static final String REQUEST_ATTRIBUTE = "csrfToken";
    public static final String PARAMETER_NAME = "_csrf";
    public static final String HEADER_NAME = "X-CSRF-TOKEN";

    private static final Set<String> PROTECTED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String expectedToken = getOrCreateToken(session);
        request.setAttribute(REQUEST_ATTRIBUTE, expectedToken);

        if (PROTECTED_METHODS.contains(request.getMethod()) && !hasValidToken(request, expectedToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    public static String getOrCreateToken(HttpSession session) {
        Object existing = session.getAttribute(SESSION_ATTRIBUTE);
        if (existing instanceof String token && !token.isBlank()) {
            return token;
        }
        String token = UUID.randomUUID().toString();
        session.setAttribute(SESSION_ATTRIBUTE, token);
        return token;
    }

    private boolean hasValidToken(HttpServletRequest request, String expectedToken) {
        String actualToken = request.getHeader(HEADER_NAME);
        if (actualToken == null || actualToken.isBlank()) {
            actualToken = request.getParameter(PARAMETER_NAME);
        }
        if (actualToken == null) {
            return false;
        }
        return MessageDigest.isEqual(
            expectedToken.getBytes(StandardCharsets.UTF_8),
            actualToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
