package com.example.servingwebcontent.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.example.servingwebcontent.testutil.CsrfTestSupport.csrfPost;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicPagesShouldIncludeSecurityHeaders() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("frame-ancestors 'none'")))
                .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=()"))
                .andExpect(header().string("Cross-Origin-Embedder-Policy", "credentialless"))
                .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin"))
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"));
    }

    @Test
    void postWithoutCsrfTokenShouldBeForbidden() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "123456"))
                .andExpect(status().isForbidden());
    }

    @Test
    void postWithInvalidCsrfTokenShouldBeForbidden() throws Exception {
        MockHttpSession session = new MockHttpSession();
        CsrfTokenFilter.getOrCreateToken(session);

        mockMvc.perform(post("/login")
                        .session(session)
                        .param(CsrfTokenFilter.PARAMETER_NAME, "invalid-token")
                        .param("username", "admin")
                        .param("password", "123456"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginSqlInjectionShouldNotLogin() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "' OR '1'='1")
                        .param("password", "' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void loginXssPayloadShouldNotLogin() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "<script>alert(1)</script>")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void protectedDashboardWithoutSessionShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void protectedPatientsWithoutSessionShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void userRoleShouldNotAccessAdminPage() throws Exception {
        mockMvc.perform(get("/admin/accounts")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "normal_user")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?denied=1"));
    }

    @Test
    void staffRoleShouldAccessPatientPage() throws Exception {
        mockMvc.perform(get("/patients")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff_user")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"));
    }
}
