package com.example.servingwebcontent.security;

import com.example.servingwebcontent.database.PatientDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityInputTest {

    @Test
    void sqlInjectionSearchShouldNotCrashApplication() {
        PatientDatabase patientDatabase = new PatientDatabase();

        assertDoesNotThrow(() -> {
            patientDatabase.searchPatientsByName("' OR '1'='1");
        });
    }

    @Test
    void xssPayloadShouldBeTreatedAsNormalKeyword() {
        PatientDatabase patientDatabase = new PatientDatabase();

        assertDoesNotThrow(() -> {
            patientDatabase.searchPatientsByName("<script>alert(1)</script>");
        });
    }
}
