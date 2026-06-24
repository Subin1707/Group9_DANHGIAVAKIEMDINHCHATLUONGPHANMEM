package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomModelTest {

    @Test
    void createRoomBasicConstructorShouldDefaultActiveStatus() {
        Room room = new Room("R001", "Phong noi tru", "BS A");

        assertEquals("R001", room.getId());
        assertEquals("Phong noi tru", room.getName());
        assertEquals("BS A", room.getDoctorName());
        assertEquals("Hoạt động", room.getStatus());
    }

    @Test
    void createRoomFullConstructorShouldSetAllFields() {
        Room room = new Room("R002", "Phong cap cuu", "BS B", 10, "Bao tri");

        assertEquals("R002", room.getId());
        assertEquals("Phong cap cuu", room.getName());
        assertEquals("BS B", room.getDoctorName());
        assertEquals(10, room.getCapacity());
        assertEquals("Bao tri", room.getStatus());
    }

    @Test
    void setIdShouldUpdateId() {
        Room room = new Room();
        room.setId("R003");

        assertEquals("R003", room.getId());
    }

    @Test
    void setNameShouldUpdateName() {
        Room room = new Room();
        room.setName("Phong xet nghiem");

        assertEquals("Phong xet nghiem", room.getName());
    }

    @Test
    void setDoctorNameShouldUpdateDoctorName() {
        Room room = new Room();
        room.setDoctorName("BS C");

        assertEquals("BS C", room.getDoctorName());
    }

    @Test
    void setCapacityShouldUpdateCapacity() {
        Room room = new Room();
        room.setCapacity(25);

        assertEquals(25, room.getCapacity());
    }

    @Test
    void setStatusShouldUpdateStatus() {
        Room room = new Room();
        room.setStatus("Bao tri");

        assertEquals("Bao tri", room.getStatus());
    }

    @Test
    void nullStatusShouldUseDefaultStatus() {
        Room room = new Room("R004", "Phong test", "BS D", 5, null);

        assertEquals("Hoạt động", room.getStatus());
    }
}
