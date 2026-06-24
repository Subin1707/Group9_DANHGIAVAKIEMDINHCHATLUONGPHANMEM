package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PatientModelTest {

    @Test
    void createPatientShouldSetAllFields() {
        Calendar dob = Calendar.getInstance();
        dob.set(2003, Calendar.JANUARY, 1);

        Patient patient = new Patient(
                "TEST_P001",
                "Nguyễn Văn A",
                dob,
                21,
                "Nam",
                "Hà Nội",
                "0987654321"
        );

        assertEquals("TEST_P001", patient.getId());
        assertEquals("Nguyễn Văn A", patient.getName());
        assertEquals(21, patient.getAge());
        assertEquals("Nam", patient.getGender());
        assertEquals("Hà Nội", patient.getAddress());
        assertEquals("0987654321", patient.getPhone());
    }

    @Test
    void setDobShouldCalculateAge() {
        Calendar dob = Calendar.getInstance();
        dob.set(2000, Calendar.JANUARY, 1);

        Patient patient = new Patient();
        patient.setDob(dob);

        assertTrue(patient.getAge() >= 20);
    }

    @Test
    void updatePatientNameShouldWork() {
        Patient patient = new Patient();
        patient.setName("Tên cũ");

        patient.setName("Tên mới");

        assertEquals("Tên mới", patient.getName());
    }
}
