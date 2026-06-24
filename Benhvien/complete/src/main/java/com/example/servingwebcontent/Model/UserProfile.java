package com.example.servingwebcontent.Model;

public class UserProfile {
    private String username;
    private Role role;
    private String patientId;
    private String createdAt;

    public UserProfile() {}

    public UserProfile(String username, Role role, String patientId, String createdAt) {
        this.username = username;
        this.role = role != null ? role : Role.USER;
        this.patientId = patientId;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
