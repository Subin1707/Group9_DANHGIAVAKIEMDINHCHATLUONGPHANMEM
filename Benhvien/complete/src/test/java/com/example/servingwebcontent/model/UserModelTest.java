package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Role;
import com.example.servingwebcontent.Model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void createUserDefaultConstructor() {
        User user = new User();

        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    void createUserWithUsernamePassword() {
        User user = new User("user1", "123456");

        assertEquals("user1", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void createUserWithExplicitRole() {
        User user = new User("staff1", "123456", Role.STAFF);

        assertEquals(Role.STAFF, user.getRole());
    }

    @Test
    void createUserWithNullRoleShouldDefaultUser() {
        User user = new User("user2", "123456", null);

        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void setUsernameShouldUpdateUsername() {
        User user = new User();
        user.setUsername("admin");

        assertEquals("admin", user.getUsername());
    }

    @Test
    void setPasswordShouldUpdatePassword() {
        User user = new User();
        user.setPassword("secret");

        assertEquals("secret", user.getPassword());
    }

    @Test
    void setRoleShouldUpdateRole() {
        User user = new User();
        user.setRole(Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void setRoleNullShouldStoreNull() {
        User user = new User("user3", "123456", Role.USER);
        user.setRole(null);

        assertNull(user.getRole());
    }
}
