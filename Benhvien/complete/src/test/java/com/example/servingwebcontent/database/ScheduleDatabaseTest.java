package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Schedule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDatabaseTest {

    private static final String PATIENT_ID = "TEST_SCHEDULE_PATIENT";
    private static final String BENHAN_ID = "TEST_SCHEDULE_BA";
    private static final String SCHEDULE_ID = "TEST_BT999";

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final BenhanDatabase benhanDatabase = new BenhanDatabase();
    private final ScheduleDatabase scheduleDatabase = new ScheduleDatabase();

    @BeforeEach
    void setUp() {
        cleanup();
        patientDatabase.saveOrUpdatePatient(patient());
        benhanDatabase.saveOrUpdateBenhan(benhAn());
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void insertScheduleShouldWork() {
        assertTrue(scheduleDatabase.saveOrUpdateSchedule(schedule("Paracetamol", "10 vien")));

        assertNotNull(scheduleDatabase.findScheduleById(SCHEDULE_ID));
    }

    @Test
    void updateScheduleShouldWork() {
        scheduleDatabase.saveOrUpdateSchedule(schedule("Thuoc cu", "1 vien"));

        assertTrue(scheduleDatabase.saveOrUpdateSchedule(schedule("Thuoc moi", "2 vien")));

        Schedule found = scheduleDatabase.findScheduleById(SCHEDULE_ID);
        assertNotNull(found);
        assertEquals("Thuoc moi", found.getTenthuoc());
        assertEquals("2 vien", found.getSoluong());
    }

    @Test
    void findScheduleByPatientIdShouldReturnList() {
        scheduleDatabase.saveOrUpdateSchedule(schedule("Vitamin C", "5 vien"));

        boolean exists = scheduleDatabase.findScheduleByPatientId(PATIENT_ID).stream()
                .anyMatch(schedule -> SCHEDULE_ID.equals(schedule.getId()));

        assertTrue(exists);
    }

    @Test
    void deleteScheduleShouldWork() {
        scheduleDatabase.saveOrUpdateSchedule(schedule("Thuoc xoa", "3 vien"));

        assertTrue(scheduleDatabase.deleteScheduleById(SCHEDULE_ID));
        assertNull(scheduleDatabase.findScheduleById(SCHEDULE_ID));
    }

    private void cleanup() {
        scheduleDatabase.deleteScheduleById(SCHEDULE_ID);
        benhanDatabase.deleteBenhanById(BENHAN_ID);
        patientDatabase.deletePatientById(PATIENT_ID);
    }

    private Patient patient() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -28);
        return new Patient(PATIENT_ID, "Schedule patient", dob, 28, "Nu", "Hue", "0922222222");
    }

    private BenhAn benhAn() {
        return new BenhAn(BENHAN_ID, PATIENT_ID, Calendar.getInstance(), "Ho", "Khong", "Theo doi", null);
    }

    private Schedule schedule(String tenthuoc, String soluong) {
        return new Schedule(SCHEDULE_ID, BENHAN_ID, PATIENT_ID, Calendar.getInstance(), tenthuoc, soluong);
    }
}
