package com.example.servingwebcontent.Model;

import java.sql.Timestamp;

public class Appointment implements CoId {
    private String id;
    private String patientId;
    private String roomId;
    private Timestamp appointmentTime;
    private String note;
    private String status;

    public Appointment() {}

    public Appointment(String id, String patientId, String roomId, Timestamp appointmentTime, String note, String status) {
        this.id = id;
        this.patientId = patientId;
        this.roomId = roomId;
        this.appointmentTime = appointmentTime;
        this.note = note;
        this.status = status;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Timestamp getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Timestamp appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

