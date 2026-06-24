package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void adminStringShouldReturnAdmin() {
        assertEquals(Role.ADMIN, Role.fromStringOrDefault("ADMIN", Role.USER));
    }

    @Test
    void staffStringShouldReturnStaff() {
        assertEquals(Role.STAFF, Role.fromStringOrDefault("staff", Role.USER));
    }

    @Test
    void userStringShouldReturnUser() {
        assertEquals(Role.USER, Role.fromStringOrDefault(" user ", Role.ADMIN));
    }

    @Test
    void invalidStringShouldReturnDefaultUser() {
        assertEquals(Role.USER, Role.fromStringOrDefault("abc", Role.USER));
    }

    @Test
    void nullStringShouldReturnDefaultUser() {
        assertEquals(Role.USER, Role.fromStringOrDefault(null, Role.USER));
    }
}
