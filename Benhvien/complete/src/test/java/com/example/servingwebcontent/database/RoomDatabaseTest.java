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

    @Test
    void addRoomShouldRejectInvalidInput() {
        assertFalse(roomDatabase.addRoom(null));
        assertFalse(roomDatabase.addRoom(new Room("TEST_INVALID_ROOM", "", "BS Test")));
        assertFalse(roomDatabase.addRoom(new Room("TEST_INVALID_ROOM", "Phong test", "")));
    }

    @Test
    void addRoomShouldAutoGenerateIdWhenMissing() {
        Room room = new Room("", "Auto room", "BS Auto");

        assertTrue(roomDatabase.addRoom(room));
        assertNotNull(room.getId());
        assertTrue(room.getId().startsWith("R"));

        roomDatabase.deleteRoom(room.getId());
    }

    @Test
    void addDuplicateRoomShouldReturnFalse() {
        assertTrue(roomDatabase.addRoom(new Room(ROOM_ID, "Phong duplicate", "BS A")));

        assertFalse(roomDatabase.addRoom(new Room(ROOM_ID, "Phong duplicate", "BS B")));
    }

    @Test
    void updateRoomShouldRejectInvalidInputAndMissingRoom() {
        assertFalse(roomDatabase.updateRoom(null));
        assertFalse(roomDatabase.updateRoom(new Room("", "Phong", "BS")));
        assertFalse(roomDatabase.updateRoom(new Room("TEST_MISSING_ROOM", "Phong", "BS")));
        assertFalse(roomDatabase.updateRoom(new Room(ROOM_ID, "", "BS")));
        assertFalse(roomDatabase.updateRoom(new Room(ROOM_ID, "Phong", "")));
    }

    @Test
    void deleteRoomShouldRejectInvalidInput() {
        assertFalse(roomDatabase.deleteRoom(null));
        assertFalse(roomDatabase.deleteRoom(""));
    }

    @Test
    void searchRoomsShouldFindByIdNameAndDoctor() {
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong Tim Kiem", "BS Search"));

        assertTrue(roomDatabase.searchRooms(ROOM_ID).stream().anyMatch(room -> ROOM_ID.equals(room.getId())));
        assertTrue(roomDatabase.searchRooms("tim kiem").stream().anyMatch(room -> ROOM_ID.equals(room.getId())));
        assertTrue(roomDatabase.searchRooms("search").stream().anyMatch(room -> ROOM_ID.equals(room.getId())));
        assertFalse(roomDatabase.searchRooms("").isEmpty());
    }

    @Test
    void roomExistsAndCountShouldWork() {
        roomDatabase.saveOrUpdateRoom(new Room(ROOM_ID, "Phong count", "BS Count"));

        assertTrue(roomDatabase.roomExists(ROOM_ID));
        assertTrue(roomDatabase.getRoomCount() >= 1);
    }

    @Test
    void generateNextRoomIdShouldReturnRoomPattern() {
        assertTrue(roomDatabase.generateNextRoomId().matches("R\\d{3,}"));
    }

    @Test
    void saveOrUpdateRoomShouldRejectInvalidInput() {
        assertFalse(roomDatabase.saveOrUpdateRoom(null));
        assertFalse(roomDatabase.saveOrUpdateRoom(new Room("TEST_INVALID_ROOM", "", "BS Test")));
        assertFalse(roomDatabase.saveOrUpdateRoom(new Room("TEST_INVALID_ROOM", "Phong test", "")));
    }
}
