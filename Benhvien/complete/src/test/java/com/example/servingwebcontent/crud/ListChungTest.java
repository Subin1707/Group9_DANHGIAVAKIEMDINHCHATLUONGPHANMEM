package com.example.servingwebcontent.crud;

import com.example.servingwebcontent.CRUD.ListChung;
import com.example.servingwebcontent.Model.Patient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ListChungTest {

    @Test
    void addShouldStoreItem() {
        ListChung<Patient> list = newList();

        list.them(patient("P1", "A"));

        assertTrue(list.tonTai("p1"));
        assertEquals(1, list.getList().size());
    }

    @Test
    void duplicateAddShouldKeepOriginalItem() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "Original"));

        list.them(patient("P1", "Duplicate"));

        assertEquals(1, list.getList().size());
        assertEquals("Original", list.timKiem("P1").getName());
    }

    @Test
    void editExistingItemShouldReplaceIt() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "Old"));

        list.sua("p1", patient("P1", "New"));

        assertEquals("New", list.timKiem("P1").getName());
    }

    @Test
    void editMissingItemShouldNotChangeList() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "Old"));

        list.sua("missing", patient("P2", "New"));

        assertEquals(1, list.getList().size());
        assertNull(list.timKiem("P2"));
    }

    @Test
    void deleteExistingItemShouldReturnTrue() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "A"));

        assertTrue(list.xoa("p1"));
        assertFalse(list.tonTai("P1"));
    }

    @Test
    void deleteMissingItemShouldReturnFalse() {
        ListChung<Patient> list = newList();

        assertFalse(list.xoa("missing"));
    }

    @Test
    void searchManyByLinkedIdShouldReturnMatchingIds() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "A"));
        list.them(patient("P2", "B"));

        assertEquals(1, list.timKiemNhieuTheoIdLienKet("p1").size());
    }

    @Test
    void addListShouldOnlyRunOnce() {
        ListChung<Patient> list = newList();
        ArrayList<Patient> sample = new ArrayList<>();
        sample.add(patient("P1", "A"));
        sample.add(patient("P2", "B"));

        list.addList(sample);
        list.addList(sample);

        assertTrue(list.isDaThemMau());
        assertEquals(2, list.getList().size());
    }

    @Test
    void iteratorShouldIterateStoredItems() {
        ListChung<Patient> list = newList();
        list.them(patient("P1", "A"));

        Iterator<Patient> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("P1", iterator.next().getId());
    }

    @Test
    void printEmptyAndNonEmptyListShouldNotThrow() {
        ListChung<Patient> list = newList();

        assertDoesNotThrow(list::inDanhSach);

        list.them(patient("P1", "A"));
        assertDoesNotThrow(list::inDanhSach);
    }

    private ListChung<Patient> newList() {
        ListChung<Patient> list = new ListChung<>();
        list.setHienThongBao(false);
        return list;
    }

    private Patient patient(String id, String name) {
        Calendar dob = Calendar.getInstance();
        dob.add(Calendar.YEAR, -20);
        return new Patient(id, name, dob, 20, "Nam", "Ha Noi", "0900000000");
    }
}
