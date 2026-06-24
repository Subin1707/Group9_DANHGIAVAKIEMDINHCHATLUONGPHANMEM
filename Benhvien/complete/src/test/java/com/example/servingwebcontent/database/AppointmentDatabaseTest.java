package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Appointment;
import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentDatabaseTest {

    private static final String PATIENT_ID = "TEST_AP_PATIENT";
    private static final String APPOINTMENT_ID = "TEST_AP999";

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final AppointmentDatabase appointmentDatabase = new AppointmentDatabase();

    @BeforeEach
    void setUp() {
        cleanup();
        patientDatabase.saveOrUpdatePatient(patient());
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    void createAppointmentShouldWork() {
        assertTrue(appointmentDatabase.createAppointment(appointment("PENDING")));

        assertNotNull(appointmentDatabase.findById(APPOINTMENT_ID));
    }

    @Test
    void findByPatientIdShouldReturnAppointment() {
        appointmentDatabase.createAppointment(appointment("PENDING"));

        boolean exists = appointmentDatabase.findByPatientId(PATIENT_ID).stream()
                .anyMatch(item -> APPOINTMENT_ID.equals(item.getId()));

        assertTrue(exists);
    }

    @Test
    void updateStatusShouldWork() {
        appointmentDatabase.createAppointment(appointment("PENDING"));

        assertTrue(appointmentDatabase.updateStatus(APPOINTMENT_ID, "CONFIRMED"));

        assertEquals("CONFIRMED", appointmentDatabase.findById(APPOINTMENT_ID).getStatus());
    }

    @Test
    void deleteByIdShouldWork() {
        appointmentDatabase.createAppointment(appointment("PENDING"));

        assertTrue(appointmentDatabase.deleteById(APPOINTMENT_ID));
        assertNull(appointmentDatabase.findById(APPOINTMENT_ID));
    }

    @Test
    void generateNextAppointmentIdShouldReturnApPrefix() {
        String nextId = appointmentDatabase.generateNextAppointmentId();

        assertTrue(nextId.startsWith("AP"));
    }

    private Patient patient() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -22);
        return new Patient(PATIENT_ID, "Appointment patient", dob, 22, "Nam", "Ha Noi", "0933333333");
    }

    private Appointment appointment(String status) {
        return new Appointment(APPOINTMENT_ID, PATIENT_ID, null,
                Timestamp.valueOf("2026-07-01 08:30:00"), "Kham test", status);
    }

    private void cleanup() {
        appointmentDatabase.deleteById(APPOINTMENT_ID);
        patientDatabase.deletePatientById(PATIENT_ID);
    }
}
