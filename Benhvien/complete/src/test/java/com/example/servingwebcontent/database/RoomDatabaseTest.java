package com.example.servingwebcontent.database;

import com.example.servingwebcontent.Model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomDatabaseTest {

    private static final String ROOM_ID = "TEST_R999";

    private final RoomDatabase roomDatabase = new RoomDatabase();

    @BeforeEach
    void setUp() {
        roomDatabase.deleteRoom(ROOM_ID);
    }

    @AfterEach
    void tearDown() {
        roomDatabase.deleteRoom(ROOM_ID);
    }

    @Test
    void insertRoomShouldWork() {
        assertTrue(roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong test", "BS Test")));

        assertNotNull(roomDatabase.findRoomById(ROOM_ID));
    }

    @Test
    void updateRoomShouldWork() {
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong cu", "BS Cu"));

        assertTrue(roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong moi", "BS Moi")));

        Room found = roomDatabase.findRoomById(ROOM_ID);
        assertNotNull(found);
        assertEquals("Phong moi", found.getName());
        assertEquals("BS Moi", found.getDoctorName());
    }

    @Test
    void findRoomByIdShouldReturnRoom() {
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong tim", "BS Tim"));

        Room found = roomDatabase.findRoomById(ROOM_ID);

        assertNotNull(found);
        assertEquals(ROOM_ID, found.getId());
    }

    @Test
    void deleteRoomShouldWork() {
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong xoa", "BS Xoa"));

        assertTrue(roomDatabase.deleteRoom(ROOM_ID));
        assertNull(roomDatabase.findRoomById(ROOM_ID));
    }
}
