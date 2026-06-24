package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Patient;
import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.User;
import com.example.servingwebcontent.Model.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class UserDatabaseTest {

    private static final String USERNAME = "test_user_db";
    private static final String PATIENT_ID = "TEST_USER_PATIENT";

    private final UserDatabase userDatabase = new UserDatabase();
    private final PatientDatabase patientDatabase = new PatientDatabase();

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
    void registerUserShouldCreateUserWithRole() {
        assertTrue(userDatabase.registerUser(USERNAME, "123456", Role.STAFF));

        UserProfile profile = userDatabase.findUserProfile(USERNAME);
        assertNotNull(profile);
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(Role.STAFF, profile.getRole());
    }

    @Test
    void duplicateUserShouldReturnFalse() {
        assertTrue(userDatabase.registerUser(USERNAME, "123456", Role.USER));

        assertFalse(userDatabase.registerUser(USERNAME, "123456", Role.USER));
    }

    @Test
    void authenticateShouldReturnUserWithRole() {
        userDatabase.registerUser(USERNAME, "123456", Role.STAFF);

        User user = userDatabase.authenticate(USERNAME, "123456");

        assertNotNull(user);
        assertEquals(USERNAME, user.getUsername());
        assertEquals(Role.STAFF, user.getRole());
    }

    @Test
    void wrongPasswordShouldNotAuthenticate() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertFalse(userDatabase.authenticateUser(USERNAME, "wrong-password"));
    }

    @Test
    void updateLinkedPatientIdShouldWork() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertTrue(userDatabase.updateLinkedPatientId(USERNAME, PATIENT_ID));

        assertEquals(PATIENT_ID, userDatabase.getLinkedPatientId(USERNAME));
    }

    @Test
    void updateRoleShouldWork() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertTrue(userDatabase.updateUserRole(USERNAME, Role.ADMIN));

        UserProfile profile = userDatabase.findUserProfile(USERNAME);
        assertNotNull(profile);
        assertEquals(Role.ADMIN, profile.getRole());
    }

    @Test
    void updatePasswordShouldWork() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertTrue(userDatabase.updatePassword(USERNAME, "abcdef"));

        assertTrue(userDatabase.authenticateUser(USERNAME, "abcdef"));
    }

    @Test
    void invalidPasswordUpdateShouldReturnFalse() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertFalse(userDatabase.updatePassword(USERNAME, "123"));
    }

    @Test
    void deleteUserShouldWork() {
        userDatabase.registerUser(USERNAME, "123456", Role.USER);

        assertTrue(userDatabase.deleteUser(USERNAME));

        assertFalse(userDatabase.userExists(USERNAME));
    }

    @Test
    void adminUserShouldNotBeDeleted() {
        assertFalse(userDatabase.deleteUser("admin"));
    }

    private void cleanup() {
        userDatabase.deleteUser(USERNAME);
        patientDatabase.deletePatientById(PATIENT_ID);
    }

    private Patient patient() {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -28);
        return new Patient(PATIENT_ID, "User linked patient", dob, 28, "Nam", "Ha Noi", "0922222222");
    }
}
