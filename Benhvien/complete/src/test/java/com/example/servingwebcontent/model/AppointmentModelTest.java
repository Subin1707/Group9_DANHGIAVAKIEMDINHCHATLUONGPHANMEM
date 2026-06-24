package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Appointment;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentModelTest {

    @Test
    void createAppointmentWithFullConstructor() {
        Timestamp time = Timestamp.valueOf("2026-07-01 08:30:00");
        Appointment appointment = new Appointment("AP001", "P001", "R001", time, "Kham sang", "PENDING");

        assertEquals("AP001", appointment.getId());
        assertEquals("P001", appointment.getPatientId());
        assertEquals("R001", appointment.getRoomId());
        assertSame(time, appointment.getAppointmentTime());
        assertEquals("Kham sang", appointment.getNote());
        assertEquals("PENDING", appointment.getStatus());
    }

    @Test
    void defaultConstructorShouldCreateEmptyAppointment() {
        Appointment appointment = new Appointment();

        assertNull(appointment.getId());
        assertNull(appointment.getPatientId());
    }

    @Test
    void settersShouldUpdateAppointmentFields() {
        Timestamp time = Timestamp.valueOf("2026-07-02 09:00:00");
        Appointment appointment = new Appointment();

        appointment.setId("AP002");
        appointment.setPatientId("P002");
        appointment.setRoomId("R002");
        appointment.setAppointmentTime(time);
        appointment.setNote("Tai kham");
        appointment.setStatus("CONFIRMED");

        assertEquals("AP002", appointment.getId());
        assertEquals("P002", appointment.getPatientId());
        assertEquals("R002", appointment.getRoomId());
        assertSame(time, appointment.getAppointmentTime());
        assertEquals("Tai kham", appointment.getNote());
        assertEquals("CONFIRMED", appointment.getStatus());
    }
}
