package com.example.servingwebcontent.security;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Schedule;
import com.example.servingwebcontent.database.BenhanDatabase;
import com.example.servingwebcontent.database.DatabaseConfig;
import com.example.servingwebcontent.database.PatientDatabase;
import com.example.servingwebcontent.database.ScheduleDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class InputSecurityTest {

    private static final String PATIENT_ID = "SEC_PATIENT";
    private static final String BENHAN_ID = "SEC_BA";
    private static final String SCHEDULE_ID = "SEC_BT";

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final BenhanDatabase benhanDatabase = new BenhanDatabase();
    private final ScheduleDatabase scheduleDatabase = new ScheduleDatabase();

    @BeforeEach
    void setUp() {
        cleanup();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void searchPatientSqlInjectionShouldNotCrash() {
        assertDoesNotThrow(() -> patientDatabase.searchPatientsByName("' OR '1'='1"));
    }

    @Test
    void searchPatientXssShouldNotCrash() {
        assertDoesNotThrow(() -> patientDatabase.searchPatientsByName("<script>alert(1)</script>"));
    }

    @Test
    void createPatientWithXssNameShouldNotExecuteScript() {
        Patient patient = patient("<script>alert(1)</script>");

        assertDoesNotThrow(() -> patientDatabase.saveOrUpdatePatient(patient));

        Patient found = patientDatabase.findPatientById(PATIENT_ID);
        assertNotNull(found);
        assertEquals("<script>alert(1)</script>", found.getName());
    }

    @Test
    void createPatientWithSqlInjectionNameShouldNotBreakDatabase() {
        Patient patient = patient("Robert'); DROP TABLE patient; --");

        assertDoesNotThrow(() -> patientDatabase.saveOrUpdatePatient(patient));
        assertTrue(patientTableStillExists());
    }

    @Test
    void createBenhAnWithXssShouldNotExecuteScript() {
        patientDatabase.saveOrUpdatePatient(patient("Security patient"));
        BenhAn benhAn = new BenhAn(BENHAN_ID, PATIENT_ID, Calendar.getInstance(),
                "<script>alert(1)</script>", "Khong", "Theo doi", null);

        assertTrue(benhanDatabase.saveOrUpdateBenhan(benhAn));

        BenhAn found = benhanDatabase.findBenhanById(BENHAN_ID);
        assertNotNull(found);
        assertEquals("<script>alert(1)</script>", found.getTrieuChung());
    }

    @Test
    void createScheduleWithSqlInjectionMedicineShouldNotBreakDatabase() {
        patientDatabase.saveOrUpdatePatient(patient("Schedule security patient"));
        benhanDatabase.saveOrUpdateBenhan(new BenhAn(BENHAN_ID, PATIENT_ID, Calendar.getInstance(),
                "Ho", "Khong", "Theo doi", null));
        Schedule schedule = new Schedule(SCHEDULE_ID, BENHAN_ID, PATIENT_ID, Calendar.getInstance(),
                "Thuoc'); DROP TABLE schedule; --", "1 vien");

        assertTrue(scheduleDatabase.saveOrUpdateSchedule(schedule));

        Schedule found = scheduleDatabase.findScheduleById(SCHEDULE_ID);
        assertNotNull(found);
        assertEquals("Thuoc'); DROP TABLE schedule; --", found.getTenthuoc());
    }

    private Patient patient(String name) {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -20);
        return new Patient(PATIENT_ID, name, dob, 20, "Nam", "Ha Noi", "0900000000");
    }

    private boolean patientTableStillExists() {
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM patient")) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    private void cleanup() {
        scheduleDatabase.deleteScheduleById(SCHEDULE_ID);
        benhanDatabase.deleteBenhanById(BENHAN_ID);
        patientDatabase.deletePatientById(PATIENT_ID);
    }
}
