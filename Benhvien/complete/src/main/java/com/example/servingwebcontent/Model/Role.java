package com.example.servingwebcontent.Model;

public enum Role {
    ADMIN,
    STAFF,
    USER;

    public static Role fromStringOrDefault(String value, Role defaultRole) {
        if (value == null) return defaultRole;
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return defaultRole;
        }
    }
}
