package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomModelTest {

    @Test
    void createRoomShouldSetDefaultStatus() {
        Room room = new Room("R_TEST", "Phòng test", "BS. Test");

        assertEquals("R_TEST", room.getId());
        assertEquals("Phòng test", room.getName());
        assertEquals("BS. Test", room.getDoctorName());
        assertEquals("Hoạt động", room.getStatus());
    }

    @Test
    void createRoomWithCapacityShouldWork() {
        Room room = new Room("R002", "Phòng cấp cứu", "BS. A", 10, "Bảo trì");

        assertEquals(10, room.getCapacity());
        assertEquals("Bảo trì", room.getStatus());
    }

    @Test
    void nullStatusShouldUseDefaultStatus() {
        Room room = new Room("R003", "Phòng nội trú", "BS. B", 20, null);

        assertEquals("Hoạt động", room.getStatus());
    }
}
