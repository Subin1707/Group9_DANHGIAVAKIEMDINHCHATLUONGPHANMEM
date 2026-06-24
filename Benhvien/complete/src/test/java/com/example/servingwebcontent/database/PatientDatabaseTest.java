package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PatientDatabaseTest {

    private static final String PATIENT_ID = "TEST_DB_PATIENT";

    private final PatientDatabase patientDatabase = new PatientDatabase();

    @BeforeEach
    void setUp() {
        patientDatabase.deletePatientById(PATIENT_ID);
    }

    @AfterEach
    void tearDown() {
        patientDatabase.deletePatientById(PATIENT_ID);
    }

    @Test
    void insertPatientShouldWork() {
        assertTrue(patientDatabase.saveOrUpdatePatient(patient("Nguyen Test", 22)));

        assertNotNull(patientDatabase.findPatientById(PATIENT_ID));
    }

    @Test
    void updatePatientShouldWork() {
        patientDatabase.saveOrUpdatePatient(patient("Ten cu", 22));

        assertTrue(patientDatabase.saveOrUpdatePatient(patient("Ten moi", 23)));

        Patient found = patientDatabase.findPatientById(PATIENT_ID);
        assertNotNull(found);
        assertEquals("Ten moi", found.getName());
        assertEquals(23, found.getAge());
    }

    @Test
    void findPatientByIdShouldReturnPatient() {
        patientDatabase.saveOrUpdatePatient(patient("Nguoi benh", 24));

        Patient found = patientDatabase.findPatientById(PATIENT_ID);

        assertNotNull(found);
        assertEquals(PATIENT_ID, found.getId());
    }

    @Test
    void findAllPatientsShouldContainInsertedPatient() {
        patientDatabase.saveOrUpdatePatient(patient("Nguoi benh list", 25));

        boolean exists = patientDatabase.getPatientList().stream()
                .anyMatch(patient -> PATIENT_ID.equals(patient.getId()));

        assertTrue(exists);
    }

    @Test
    void deletePatientShouldWork() {
        patientDatabase.saveOrUpdatePatient(patient("Nguoi benh delete", 26));

        assertTrue(patientDatabase.deletePatientById(PATIENT_ID));
        assertNull(patientDatabase.findPatientById(PATIENT_ID));
    }

    private Patient patient(String name, int age) {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -age);
        return new Patient(PATIENT_ID, name, dob, age, "Nam", "Ha Noi", "0900000000");
    }
}
