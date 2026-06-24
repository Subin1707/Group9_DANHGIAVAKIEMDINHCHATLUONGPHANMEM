package com.example.servingwebcontent.integration;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.Room;
import com.example.servingwebcontent.Model.Schedule;
import com.example.servingwebcontent.database.AppointmentDatabase;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.PatientDatabase;
import com.example.servingwebcontent.database.RoomDatabase;
import com.example.servingwebcontent.database.ScheduleDatabase;
import com.example.servingwebcontent.database.UserDatabase;
import com.example.servingwebcontent.security.AuthConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortalAndAdminIntegrationTest {

    private static final String USERNAME = "portal_test_user";
    private static final String PATIENT_ID = "TEST_PORTAL_PATIENT";
    private static final String ROOM_ID = "TEST_PORTAL_ROOM";
    private static final String BENHAN_ID = "TEST_PORTAL_BA";
    private static final String SCHEDULE_ID = "TEST_PORTAL_BT";
    private static final String APPOINTMENT_ID = "TEST_PORTAL_AP";

    @Autowired
    MockMvc mockMvc;

    private final UserDatabase userDatabase = new UserDatabase();
    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final RoomDatabase roomDatabase = new RoomDatabase();
    private final BenhanDatabase benhanDatabase = new BenhanDatabase();
    private final ScheduleDatabase scheduleDatabase = new ScheduleDatabase();
    private final AppointmentDatabase appointmentDatabase = new AppointmentDatabase();

    @BeforeEach
    void setUp() {
        cleanup();
        patientDatabase.saveOrUpdatePatient(patient());
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Portal room", "BS Portal", 10, "Hoat dong"));
        userDatabase.registerUser(USERNAME, "123456", Role.USER);
        userDatabase.updateLinkedPatientId(USERNAME, PATIENT_ID);
        createBenhan();
        createSchedule();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void portalRootForUserShouldRedirectProfile() throws Exception {
        mockMvc.perform(get("/portal").sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portal/profile"));
    }

    @Test
    void portalRootForStaffShouldRedirectDashboard() throws Exception {
        mockMvc.perform(get("/portal").sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void profileUpdateShouldRedirectProfile() throws Exception {
        mockMvc.perform(post("/portal/profile")
                        .param("name", "Portal Updated")
                        .param("phone", "0999999999")
                        .param("dob", "2000-02-02")
                        .param("gender", "Nam")
                        .param("address", "Hue")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portal/profile"));
    }

    @Test
    void passwordPageShouldReturnView() throws Exception {
        mockMvc.perform(get("/portal/password")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_password"));
    }

    @Test
    void passwordTooShortShouldReturnError() throws Exception {
        mockMvc.perform(post("/portal/password")
                        .param("currentPassword", "123456")
                        .param("newPassword", "123")
                        .param("confirmPassword", "123")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_password"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void passwordWrongCurrentShouldReturnError() throws Exception {
        mockMvc.perform(post("/portal/password")
                        .param("currentPassword", "wrong")
                        .param("newPassword", "abcdef")
                        .param("confirmPassword", "abcdef")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_password"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void passwordChangeSuccessShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/portal/password")
                        .param("currentPassword", "123456")
                        .param("newPassword", "abcdef")
                        .param("confirmPassword", "abcdef")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_password"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void portalBenhanShouldReturnRows() throws Exception {
        mockMvc.perform(get("/portal/benhan")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_benhan"))
                .andExpect(model().attributeExists("benhans"));
    }

    @Test
    void portalScheduleShouldReturnRows() throws Exception {
        mockMvc.perform(get("/portal/schedule")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_schedule"))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    void portalNotificationsShouldReturnView() throws Exception {
        mockMvc.perform(get("/portal/notifications")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_notifications"));
    }

    @Test
    void createAppointmentInvalidTimeShouldReturnError() throws Exception {
        mockMvc.perform(post("/portal/appointments")
                        .param("roomId", ROOM_ID)
                        .param("appointmentTime", "not-a-time")
                        .param("note", "Invalid")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_appointments_new"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void appointmentsPageShouldReturnAppointments() throws Exception {
        createAppointment("PENDING");

        mockMvc.perform(get("/portal/appointments")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("portal_appointments"))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    void cancelOwnPendingAppointmentShouldRedirect() throws Exception {
        createAppointment("PENDING");

        mockMvc.perform(post("/portal/appointments/cancel")
                        .param("id", APPOINTMENT_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, USERNAME)
                        .sessionAttr(AuthConstants.SESSION_ROLE, "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portal/appointments"));
    }

    @Test
    void adminCanViewAndUpdateUserProfile() throws Exception {
        mockMvc.perform(get("/admin/accounts/" + USERNAME)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_profile"))
                .andExpect(model().attributeExists("profile"));

        mockMvc.perform(post("/admin/accounts/" + USERNAME + "/role")
                        .param("role", "STAFF")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_profile"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void adminPasswordMismatchShouldReturnProfileError() throws Exception {
        mockMvc.perform(post("/admin/accounts/" + USERNAME + "/password")
                        .param("password", "abcdef")
                        .param("confirmPassword", "ghijkl")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_profile"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void adminCanUpdatePatientLinkAndDeleteUser() throws Exception {
        mockMvc.perform(post("/admin/accounts/" + USERNAME + "/patient")
                        .param("patientId", PATIENT_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_profile"))
                .andExpect(model().attributeExists("successMessage"));

        mockMvc.perform(post("/admin/accounts/" + USERNAME + "/delete")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "admin")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"));
    }

    private void createBenhan() {
        benhanDatabase.saveOrUpdateBenhan(new BenhAn(
                BENHAN_ID, PATIENT_ID, Calendar.getInstance(), "Sot", "Khong", "Theo doi", ROOM_ID));
    }

    private void createSchedule() {
        Calendar date = Calendar.getInstance();
        date.set(2026, Calendar.FEBRUARY, 5);
        scheduleDatabase.saveOrUpdateSchedule(new Schedule(
                SCHEDULE_ID, BENHAN_ID, PATIENT_ID, date, "Vitamin C", "5 vien"));
    }

    private void createAppointment(String status) {
        appointmentDatabase.createAppointment(new com.example.servingwebcontent.Model.Appointment(
                APPOINTMENT_ID, PATIENT_ID, ROOM_ID, Timestamp.valueOf("2026-03-03 09:30:00"), "Portal note", status));
    }

    private void cleanup() {
        appointmentDatabase.deleteById(APPOINTMENT_ID);
        scheduleDatabase.deleteScheduleById(SCHEDULE_ID);
        benhanDatabase.deleteBenhanById(BENHAN_ID);
        userDatabase.deleteUser(USERNAME);
        patientDatabase.deletePatientById(PATIENT_ID);
        roomDatabase.deleteRoom(ROOM_ID);
    }

    private Patient patient() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -26);
        return new Patient(PATIENT_ID, "Portal Patient", dob, 26, "Nam", "Ha Noi", "0944444444");
    }
}
