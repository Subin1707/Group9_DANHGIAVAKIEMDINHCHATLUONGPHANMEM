package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.BenhAn;
import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class BenhAnDatabaseTest {

    private static final String PATIENT_ID = "TEST_BA_PATIENT";
    private static final String BENHAN_ID = "TEST_BA999";

    private final PatientDatabase patientDatabase = new PatientDatabase();
    private final BenhanDatabase benhanDatabase = new BenhanDatabase();

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
    void insertBenhAnShouldWork() {
        assertTrue(benhanDatabase.saveOrUpdateBenhan(benhAn("Sot", "Cam cum")));

        assertNotNull(benhanDatabase.findBenhanById(BENHAN_ID));
    }

    @Test
    void updateBenhAnShouldWork() {
        benhanDatabase.saveOrUpdateBenhan(benhAn("Ho", "Viem hong"));

        assertTrue(benhanDatabase.saveOrUpdateBenhan(benhAn("Dau dau", "Cam lanh")));

        BenhAn found = benhanDatabase.findBenhanById(BENHAN_ID);
        assertNotNull(found);
        assertEquals("Dau dau", found.getTrieuChung());
        assertEquals("Cam lanh", found.getChanDoan());
    }

    @Test
    void findBenhAnByPatientIdShouldReturnList() {
        benhanDatabase.saveOrUpdateBenhan(benhAn("Met moi", "Theo doi"));

        boolean exists = benhanDatabase.findBenhanByPatientId(PATIENT_ID).stream()
                .anyMatch(benhAn -> BENHAN_ID.equals(benhAn.getId()));

        assertTrue(exists);
    }

    @Test
    void deleteBenhAnShouldWork() {
        benhanDatabase.saveOrUpdateBenhan(benhAn("Sot", "Cam cum"));

        assertTrue(benhanDatabase.deleteBenhanById(BENHAN_ID));
        assertNull(benhanDatabase.findBenhanById(BENHAN_ID));
    }

    private void cleanup() {
        benhanDatabase.deleteBenhanById(BENHAN_ID);
        patientDatabase.deletePatientById(PATIENT_ID);
    }

    private Patient patient() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -30);
        return new Patient(PATIENT_ID, "Benh an patient", dob, 30, "Nam", "Ha Noi", "0911111111");
    }

    private BenhAn benhAn(String trieuChung, String chanDoan) {
        return new BenhAn(BENHAN_ID, PATIENT_ID, Calendar.getInstance(), trieuChung, "Khong", chanDoan, null);
    }
}
