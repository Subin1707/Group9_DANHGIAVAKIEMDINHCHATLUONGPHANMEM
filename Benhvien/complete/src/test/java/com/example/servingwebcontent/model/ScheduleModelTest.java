package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Schedule;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleModelTest {

    @Test
    void createScheduleWithFullConstructor() {
        Calendar date = Calendar.getInstance();
        Schedule schedule = new Schedule("BT001", "BA001", "P001", date, "Paracetamol", "10 vien");

        assertEquals("BT001", schedule.getId());
        assertEquals("BA001", schedule.getBenhanId());
        assertEquals("P001", schedule.getPatientId());
        assertSame(date, schedule.getDate());
        assertEquals("Paracetamol", schedule.getTenthuoc());
        assertEquals("10 vien", schedule.getSoluong());
    }

    @Test
    void createScheduleWithDefaultConstructor() {
        Schedule schedule = new Schedule();

        assertNull(schedule.getId());
        assertNull(schedule.getTenthuoc());
    }

    @Test
    void setIdShouldUpdateId() {
        Schedule schedule = new Schedule();
        schedule.setId("BT002");

        assertEquals("BT002", schedule.getId());
    }

    @Test
    void setBenhanIdShouldUpdateBenhanId() {
        Schedule schedule = new Schedule();
        schedule.setBenhanId("BA002");

        assertEquals("BA002", schedule.getBenhanId());
    }

    @Test
    void setPatientIdShouldUpdatePatientId() {
        Schedule schedule = new Schedule();
        schedule.setPatientId("P002");

        assertEquals("P002", schedule.getPatientId());
    }

    @Test
    void setDateShouldUpdateDate() {
        Calendar date = Calendar.getInstance();
        Schedule schedule = new Schedule();
        schedule.setDate(date);

        assertSame(date, schedule.getDate());
    }

    @Test
    void setTenthuocShouldUpdateTenthuoc() {
        Schedule schedule = new Schedule();
        schedule.setTenthuoc("Vitamin C");

        assertEquals("Vitamin C", schedule.getTenthuoc());
    }

    @Test
    void setSoluongShouldUpdateSoluong() {
        Schedule schedule = new Schedule();
        schedule.setSoluong("5 vien");

        assertEquals("5 vien", schedule.getSoluong());
    }
}
