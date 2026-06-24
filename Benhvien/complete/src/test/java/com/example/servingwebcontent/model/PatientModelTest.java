package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class PatientModelTest {

    @Test
    void createPatientWithFullConstructor() {
        Calendar dob = Calendar.getInstance();
        Patient patient = new Patient("P_TEST", "Nguyen Van A", dob, 21, "Nam", "Ha Noi", "0900000000");

        assertEquals("P_TEST", patient.getId());
        assertEquals("Nguyen Van A", patient.getName());
        assertSame(dob, patient.getDob());
        assertEquals(21, patient.getAge());
        assertEquals("Nam", patient.getGender());
        assertEquals("Ha Noi", patient.getAddress());
        assertEquals("0900000000", patient.getPhone());
    }

    @Test
    void createPatientWithDefaultConstructor() {
        Patient patient = new Patient();

        assertNull(patient.getId());
        assertNull(patient.getName());
        assertEquals(0, patient.getAge());
    }

    @Test
    void setIdShouldUpdateId() {
        Patient patient = new Patient();
        patient.setId("P001");

        assertEquals("P001", patient.getId());
    }

    @Test
    void setNameShouldUpdateName() {
        Patient patient = new Patient();
        patient.setName("Tran Thi B");

        assertEquals("Tran Thi B", patient.getName());
    }

    @Test
    void setDobShouldUpdateDob() {
        Calendar dob = Calendar.getInstance();
        Patient patient = new Patient();
        patient.setDob(dob);

        assertSame(dob, patient.getDob());
    }

    @Test
    void setDobShouldCalculateAge() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -24);

        Patient patient = new Patient();
        patient.setDob(dob);

        assertTrue(patient.getAge() >= 23);
    }

    @Test
    void setAgeShouldUpdateAge() {
        Patient patient = new Patient();
        patient.setAge(30);

        assertEquals(30, patient.getAge());
    }

    @Test
    void setGenderShouldUpdateGender() {
        Patient patient = new Patient();
        patient.setGender("Nu");

        assertEquals("Nu", patient.getGender());
    }

    @Test
    void setAddressShouldUpdateAddress() {
        Patient patient = new Patient();
        patient.setAddress("Da Nang");

        assertEquals("Da Nang", patient.getAddress());
    }

    @Test
    void setPhoneShouldUpdatePhone() {
        Patient patient = new Patient();
        patient.setPhone("0911111111");

        assertEquals("0911111111", patient.getPhone());
    }

    @Test
    void nullDobShouldNotCrash() {
        Patient patient = new Patient();

        assertDoesNotThrow(() -> patient.setDob(null));
        assertNull(patient.getDob());
    }

    @Test
    void toStringShouldContainPatientInfo() {
        Calendar dob = Calendar.getInstance();
        Patient patient = new Patient("P002", "Le Van C", dob, 20, "Nam", "Hue", "0922222222");

        String text = patient.toString();

        assertTrue(text.contains("P002"));
        assertTrue(text.contains("Le Van C"));
        assertTrue(text.contains("0922222222"));
    }
}
