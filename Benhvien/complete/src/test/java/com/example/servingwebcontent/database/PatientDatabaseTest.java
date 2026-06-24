package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PatientDatabaseTest {

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final String testPatientId = "TEST_P999";

    @AfterEach
    void cleanup() {
        patientDatabase.deletePatientById(testPatientId);
    }

    @Test
    void savePatientShouldInsertIntoDockerDatabase() {
        Calendar dob = Calendar.getInstance();
        dob.set(2002, Calendar.JANUARY, 1);

        Patient patient = new Patient(
                testPatientId,
                "Bệnh nhân test",
                dob,
                22,
                "Nam",
                "Hà Nội",
                "0900000000"
        );

        boolean saved = patientDatabase.saveOrUpdatePatient(patient);
        Patient found = patientDatabase.findPatientById(testPatientId);

        assertTrue(saved);
        assertNotNull(found);
        assertEquals(testPatientId, found.getId());
        assertEquals("Bệnh nhân test", found.getName());
    }

    @Test
    void findPatientNotExistsShouldReturnNull() {
        Patient found = patientDatabase.findPatientById("NOT_EXISTS_ID");

        assertNull(found);
    }
}
