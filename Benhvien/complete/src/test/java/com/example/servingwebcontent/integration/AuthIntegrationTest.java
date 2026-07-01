package com.example.servingwebcontent.integration;

import com.example.servingwebcontent.database.DatabaseConfig;
import com.example.servingwebcontent.security.AuthConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.example.servingwebcontent.testutil.CsrfTestSupport.csrfPost;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    private static final String STAFF_USERNAME = "it_staff_user";
    private static final String USER_USERNAME = "it_patient_user";

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        deleteLogin(STAFF_USERNAME);
        deleteLogin(USER_USERNAME);
        insertLogin(STAFF_USERNAME, "123456", "STAFF");
        insertLogin(USER_USERNAME, "123456", "USER");
    }

    @AfterEach
    void tearDown() throws Exception {
        deleteLogin(STAFF_USERNAME);
        deleteLogin(USER_USERNAME);
    }

    @Test
    void getLoginPageShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void loginAdminSuccessShouldRedirectDashboard() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "admin")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void loginStaffSuccessShouldRedirectDashboard() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", STAFF_USERNAME)
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void loginUserSuccessShouldRedirectPortalProfile() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", USER_USERNAME)
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portal/profile"));
    }

    @Test
    void loginWrongPasswordShouldShowError() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "admin")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void loginEmptyUsernameShouldShowError() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void loginEmptyPasswordShouldShowError() throws Exception {
        mockMvc.perform(csrfPost("/login")
                        .param("username", "admin")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void logoutShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/logout")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private void insertLogin(String username, String password, String role) throws Exception {
        String sql = "INSERT INTO login(username, password, role, patientId) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, null);
            ps.executeUpdate();
        }
    }

    private void deleteLogin(String username) throws Exception {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM login WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }
}
