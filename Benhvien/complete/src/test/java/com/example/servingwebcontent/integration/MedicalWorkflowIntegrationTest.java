package com.example.servingwebcontent.integration;

import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Room;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.PatientDatabase;
import com.example.servingwebcontent.database.RoomDatabase;
import com.example.servingwebcontent.database.ScheduleDatabase;
import com.example.servingwebcontent.database.DatabaseConfig;
import com.example.servingwebcontent.security.AuthConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.sql.Connection;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.example.servingwebcontent.testutil.CsrfTestSupport.csrfPost;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Timeout(30)
class MedicalWorkflowIntegrationTest {

    private static final String PATIENT_ID = "TEST_FLOW_PATIENT";
    private static final String PATIENT_ID_2 = "TEST_FLOW_PATIENT_2";
    private static final String ROOM_ID = "TEST_FLOW_ROOM";
    private static final String BENHAN_ID = "TEST_FLOW_BA";
    private static final String SCHEDULE_ID = "TEST_FLOW_BT";

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void requireDatabase() {
        assumeTrue(databaseIsAvailable(),
                "MySQL test database is unavailable at localhost:3306; start Docker Compose first");
    }

    private static boolean databaseIsAvailable() {
        try (Connection ignored = DatabaseConfig.getConnection()) {
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final RoomDatabase roomDatabase = new RoomDatabase();
    private final BenhanDatabase benhanDatabase = new BenhanDatabase();
    private final ScheduleDatabase scheduleDatabase = new ScheduleDatabase();

    @BeforeEach
    void setUp() {
        cleanup();
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Flow room", "BS Flow", 5, "Hoat dong"));
        patientDatabase.saveOrUpdatePatient(patient(PATIENT_ID, "Flow Patient"));
        patientDatabase.saveOrUpdatePatient(patient(PATIENT_ID_2, "Flow Patient 2"));
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void postPatientShouldCreatePatientAndShowSuccess() throws Exception {
        patientDatabase.deletePatientById("TEST_POST_PATIENT");

        mockMvc.perform(csrfPost("/patients")
                        .param("id", "TEST_POST_PATIENT")
                        .param("name", "Post Patient")
                        .param("dob", "2001-01-01")
                        .param("age", "25")
                        .param("gender", "Nam")
                        .param("address", "Ha Noi")
                        .param("phone", "0900000999")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));

        patientDatabase.deletePatientById("TEST_POST_PATIENT");
    }

    @Test
    void updatePatientShouldShowSuccess() throws Exception {
        mockMvc.perform(csrfPost("/patients/update")
                        .param("id", PATIENT_ID)
                        .param("name", "Updated Patient")
                        .param("dob", "2001-01-01")
                        .param("age", "25")
                        .param("gender", "Nam")
                        .param("address", "Da Nang")
                        .param("phone", "0900000111")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    void deleteMissingPatientShouldShowError() throws Exception {
        mockMvc.perform(get("/patients/delete/TEST_MISSING_PATIENT")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postRoomShouldCreateRoomAndShowSuccess() throws Exception {
        roomDatabase.deleteRoom("TEST_POST_ROOM");

        mockMvc.perform(csrfPost("/room")
                        .param("id", "TEST_POST_ROOM")
                        .param("name", "Post Room")
                        .param("doctorName", "BS Post")
                        .param("capacity", "8")
                        .param("status", "Hoat dong")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("phongdieutri"))
                .andExpect(model().attributeExists("roomList"));

        roomDatabase.deleteRoom("TEST_POST_ROOM");
    }

    @Test
    void updateRoomShouldShowSuccess() throws Exception {
        mockMvc.perform(csrfPost("/room/update")
                        .param("id", ROOM_ID)
                        .param("name", "Updated Room")
                        .param("doctorName", "BS Updated")
                        .param("capacity", "12")
                        .param("status", "Bao tri")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("phongdieutri"))
                .andExpect(model().attributeExists("roomList"));
    }

    @Test
    void deleteMissingRoomShouldShowError() throws Exception {
        mockMvc.perform(get("/room/delete/TEST_MISSING_ROOM")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("phongdieutri"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postBenhanShouldCreateRecordAndShowSuccess() throws Exception {
        mockMvc.perform(csrfPost("/benhan")
                        .param("id", BENHAN_ID)
                        .param("patientId", PATIENT_ID)
                        .param("ngayKham", "2026-01-10")
                        .param("trieuChung", "Sot")
                        .param("tienSuBenh", "Khong")
                        .param("chanDoan", "Theo doi")
                        .param("roomId", ROOM_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void duplicateBenhanForPatientShouldShowError() throws Exception {
        createBenhan();

        mockMvc.perform(csrfPost("/benhan")
                        .param("id", "TEST_FLOW_BA_2")
                        .param("patientId", PATIENT_ID)
                        .param("ngayKham", "2026-01-11")
                        .param("trieuChung", "Ho")
                        .param("tienSuBenh", "Khong")
                        .param("chanDoan", "Trung")
                        .param("roomId", ROOM_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("errorMessage"));

        benhanDatabase.deleteBenhanById("TEST_FLOW_BA_2");
    }

    @Test
    void editBenhanShouldReturnEditModel() throws Exception {
        createBenhan();

        mockMvc.perform(get("/benhan/edit/" + BENHAN_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("editBenhan"));
    }

    @Test
    void updateBenhanShouldShowSuccess() throws Exception {
        createBenhan();

        mockMvc.perform(csrfPost("/benhan/update")
                        .param("id", BENHAN_ID)
                        .param("patientId", PATIENT_ID)
                        .param("ngayKham", "2026-01-12")
                        .param("trieuChung", "Dau dau")
                        .param("tienSuBenh", "Khong")
                        .param("chanDoan", "On dinh")
                        .param("roomId", ROOM_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void deleteMissingBenhanShouldShowError() throws Exception {
        mockMvc.perform(get("/benhan/delete/TEST_MISSING_BA")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("benhan"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postScheduleShouldCreateScheduleAndShowSuccess() throws Exception {
        createBenhan();

        mockMvc.perform(csrfPost("/schedule")
                        .param("id", SCHEDULE_ID)
                        .param("benhanId", BENHAN_ID)
                        .param("patientId", PATIENT_ID)
                        .param("date", "2026-01-13")
                        .param("tenthuoc", "Vitamin C")
                        .param("soluong", "5 vien")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void duplicateScheduleDayShouldShowError() throws Exception {
        createBenhan();
        createSchedule();

        mockMvc.perform(csrfPost("/schedule")
                        .param("id", "TEST_FLOW_BT_2")
                        .param("benhanId", BENHAN_ID)
                        .param("patientId", PATIENT_ID)
                        .param("date", "2026-01-13")
                        .param("tenthuoc", "Paracetamol")
                        .param("soluong", "10 vien")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("errorMessage"));

        scheduleDatabase.deleteScheduleById("TEST_FLOW_BT_2");
    }

    @Test
    void editScheduleShouldReturnEditModel() throws Exception {
        createBenhan();
        createSchedule();

        mockMvc.perform(get("/schedule/edit/" + SCHEDULE_ID)
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("editSchedule"));
    }

    @Test
    void updateScheduleShouldShowSuccess() throws Exception {
        createBenhan();
        createSchedule();

        mockMvc.perform(csrfPost("/schedule/update")
                        .param("id", SCHEDULE_ID)
                        .param("benhanId", BENHAN_ID)
                        .param("patientId", PATIENT_ID)
                        .param("date", "2026-01-14")
                        .param("tenthuoc", "Vitamin B")
                        .param("soluong", "3 vien")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    void deleteMissingScheduleShouldShowError() throws Exception {
        mockMvc.perform(get("/schedule/delete/TEST_MISSING_BT")
                        .sessionAttr(AuthConstants.SESSION_USERNAME, "staff")
                        .sessionAttr(AuthConstants.SESSION_ROLE, "STAFF"))
                .andExpect(status().isOk())
                .andExpect(view().name("lichcapthuoc"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    private void createBenhan() {
        benhanDatabase.saveOrUpdateBenhan(new com.example.servingwebcontent.Model.BenhAn(
                BENHAN_ID, PATIENT_ID, Calendar.getInstance(), "Sot", "Khong", "Theo doi", ROOM_ID));
    }

    private void createSchedule() {
        Calendar date = Calendar.getInstance();
        date.set(2026, Calendar.JANUARY, 13);
        scheduleDatabase.saveOrUpdateSchedule(new com.example.servingwebcontent.Model.Schedule(
                SCHEDULE_ID, BENHAN_ID, PATIENT_ID, date, "Vitamin C", "5 vien"));
    }

    private void cleanup() {
        scheduleDatabase.deleteScheduleById(SCHEDULE_ID);
        scheduleDatabase.deleteScheduleById("TEST_FLOW_BT_2");
        benhanDatabase.deleteBenhanById(BENHAN_ID);
        benhanDatabase.deleteBenhanById("TEST_FLOW_BA_2");
        patientDatabase.deletePatientById(PATIENT_ID);
        patientDatabase.deletePatientById(PATIENT_ID_2);
        roomDatabase.deleteRoom(ROOM_ID);
    }

    private Patient patient(String id, String name) {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -24);
        return new Patient(id, name, dob, 24, "Nam", "Ha Noi", "0933333333");
    }
}
