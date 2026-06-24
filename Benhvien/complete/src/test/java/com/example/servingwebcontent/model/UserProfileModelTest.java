package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.UserProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileModelTest {

    @Test
    void createUserProfileWithFullConstructor() {
        UserProfile profile = new UserProfile("patient1", Role.USER, "P001", "2026-01-01");

        assertEquals("patient1", profile.getUsername());
        assertEquals(Role.USER, profile.getRole());
        assertEquals("P001", profile.getPatientId());
        assertEquals("2026-01-01", profile.getCreatedAt());
    }

    @Test
    void nullRoleShouldDefaultUser() {
        UserProfile profile = new UserProfile("patient2", null, "P002", "2026-01-01");

        assertEquals(Role.USER, profile.getRole());
    }

    @Test
    void settersShouldUpdateUserProfileFields() {
        UserProfile profile = new UserProfile();

        profile.setUsername("staff1");
        profile.setRole(Role.STAFF);
        profile.setPatientId("P009");
        profile.setCreatedAt("today");

        assertEquals("staff1", profile.getUsername());
        assertEquals(Role.STAFF, profile.getRole());
        assertEquals("P009", profile.getPatientId());
        assertEquals("today", profile.getCreatedAt());
    }
}
