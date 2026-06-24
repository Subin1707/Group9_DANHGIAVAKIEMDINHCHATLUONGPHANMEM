package com.example.servingwebcontent.integration;

import com.example.servingwebcontent.security.AuthConstants;
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
class ControllerCoverageIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void patientsPageWithStaffSessionShouldReturnPatientsView() throws Exception {
        mockMvc.perform(get("/patients")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    void patientsSearchShouldReturnPatientsView() throws Exception {
        mockMvc.perform(get("/patients").param("search", "P001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("search"));
    }

    @Test
    void patientEditShouldReturnPatientsView() throws Exception {
        mockMvc.perform(get("/patients/edit/P001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("editPatient"));
    }

    @Test
    void roomsPageWithStaffSessionShouldReturnRoomView() throws Exception {
        mockMvc.perform(get("/room")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("phongdieutri"))
                .andExpect(model().attributeExists("roomList"));
    }

    @Test
    void roomSearchShouldReturnRoomView() throws Exception {
        mockMvc.perform(get("/room/search").param("keyword", "R001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("phongdieutri"));
    }

    @Test
    void roomApiShouldReturnJson() throws Exception {
        mockMvc.perform(get("/room/api/R001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("R001"));
    }

    @Test
    void roomCountShouldReturnNumber() throws Exception {
        mockMvc.perform(get("/room/count")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk());
    }

    @Test
    void benhanPageWithStaffSessionShouldReturnBenhanView() throws Exception {
        mockMvc.perform(get("/benhan")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("benhans"));
    }

    @Test
    void benhanSearchShouldReturnBenhanView() throws Exception {
        mockMvc.perform(get("/benhan").param("search", "BA001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("search"));
    }

    @Test
    void schedulePageWithStaffSessionShouldReturnScheduleView() throws Exception {
        mockMvc.perform(get("/schedule")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("scheduleList"));
    }

    @Test
    void scheduleSearchShouldReturnScheduleView() throws Exception {
        mockMvc.perform(get("/schedule").param("search", "BT001")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("search"));
    }

    @Test
    void adminStaffPageShouldReturnAdminStaffView() throws Exception {
        mockMvc.perform(get("/admin/staff")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_staff"));
    }

    @Test
    void adminStaffPasswordMismatchShouldShowError() throws Exception {
        mockMvc.perform(post("/admin/staff")
                        .param("username", "staff_mismatch")
                        .param("password", "123456")
                        .param("confirmPassword", "654321")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_staff"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void adminAccountsPageShouldReturnAccountsView() throws Exception {
        mockMvc.perform(get("/admin/accounts")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_accounts"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void appointmentsManageWithStaffShouldReturnView() throws Exception {
        mockMvc.perform(get("/appointments/manage")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments_manage"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    void appointmentsManageStatusShouldRedirect() throws Exception {
        mockMvc.perform(post("/appointments/manage/status")
                        .param("id", "NOT_EXISTS")
                        .param("status", "bad-status")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/manage"));
    }

    @Test
    void portalProfileShouldReturnProfileView() throws Exception {
        mockMvc.perform(get("/portal/profile")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "patient1")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_profile"));
    }

    @Test
    void portalProfileEditShouldReturnEditView() throws Exception {
        mockMvc.perform(get("/portal/profile/edit")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "patient1")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_profile_edit"));
    }

    @Test
    void portalPasswordMismatchShouldReturnPasswordView() throws Exception {
        mockMvc.perform(post("/portal/password")
                        .param("currentPassword", "123456")
                        .param("newPassword", "abcdef")
                        .param("confirmPassword", "ghijkl")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "patient1")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_password"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void portalAppointmentsNewShouldReturnNewAppointmentView() throws Exception {
        mockMvc.perform(get("/portal/appointments/new")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "patient1")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_appointments_new"));
    }
}
