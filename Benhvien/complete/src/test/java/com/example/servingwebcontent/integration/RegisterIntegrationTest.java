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
class RegisterIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getRegisterPageShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void registerPasswordMismatchShouldShowError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "register_mismatch")
                        .param("password", "123456")
                        .param("confirmPassword", "654321"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void registerEmptyUsernameShouldShowError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void registerEmptyPasswordShouldShowError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "register_empty_password")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }
}
