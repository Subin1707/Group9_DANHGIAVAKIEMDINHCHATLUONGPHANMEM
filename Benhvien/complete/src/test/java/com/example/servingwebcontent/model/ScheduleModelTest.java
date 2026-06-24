package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.Schedule;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleModelTest {

    @Test
    void createScheduleShouldSetAllFields() {
        Calendar date = Calendar.getInstance();

        Schedule schedule = new Schedule(
                "BT_TEST",
                "BA_TEST",
                "P_TEST",
                date,
                "Paracetamol",
                "10 viên"
        );

        assertEquals("BT_TEST", schedule.getId());
        assertEquals("BA_TEST", schedule.getBenhanId());
        assertEquals("P_TEST", schedule.getPatientId());
        assertEquals("Paracetamol", schedule.getTenthuoc());
        assertEquals("10 viên", schedule.getSoluong());
    }

    @Test
    void updateMedicineShouldWork() {
        Schedule schedule = new Schedule();

        schedule.setTenthuoc("Vitamin C");
        schedule.setSoluong("5 viên");

        assertEquals("Vitamin C", schedule.getTenthuoc());
        assertEquals("5 viên", schedule.getSoluong());
    }
}
