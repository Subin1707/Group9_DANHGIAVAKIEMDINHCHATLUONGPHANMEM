package com.example.servingwebcontent.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getLoginPageShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void adminLoginSuccessShouldRedirectToDashboard() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void loginWrongPasswordShouldReturnIndexWithError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void getRegisterPageShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void registerWithDifferentConfirmPasswordShouldReturnError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "123456")
                        .param("confirmPassword", "654321"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }
}
