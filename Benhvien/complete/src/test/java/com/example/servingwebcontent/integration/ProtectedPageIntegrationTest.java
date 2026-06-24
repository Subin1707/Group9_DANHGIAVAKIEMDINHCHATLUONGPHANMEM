package com.example.servingwebcontent.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProtectedPageIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void dashboardWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void patientsWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void benhAnWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/benhan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void roomsWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/room"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void schedulesWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/schedule"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminPageWithoutLoginShouldRedirectLogin() throws Exception {
        mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
