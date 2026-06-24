package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void validAdminRoleShouldReturnAdmin() {
        Role role = Role.fromStringOrDefault("ADMIN", Role.USER);

        assertEquals(Role.ADMIN, role);
    }

    @Test
    void lowercaseStaffShouldReturnStaff() {
        Role role = Role.fromStringOrDefault("staff", Role.USER);

        assertEquals(Role.STAFF, role);
    }

    @Test
    void invalidRoleShouldReturnDefault() {
        Role role = Role.fromStringOrDefault("abc", Role.USER);

        assertEquals(Role.USER, role);
    }

    @Test
    void nullRoleShouldReturnDefault() {
        Role role = Role.fromStringOrDefault(null, Role.USER);

        assertEquals(Role.USER, role);
    }
}
