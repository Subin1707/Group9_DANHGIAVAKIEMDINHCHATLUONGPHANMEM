package com.example.servingwebcontent.model;

import com.example.servingwebcontent.Model.BenhAn;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class BenhAnModelTest {

    @Test
    void createBenhAnWithFullConstructor() {
        Calendar ngayKham = Calendar.getInstance();
        BenhAn benhAn = new BenhAn("BA001", "P001", ngayKham, "Sot", "Khong", "Cam cum", "R001");

        assertEquals("BA001", benhAn.getId());
        assertEquals("P001", benhAn.getPatientId());
        assertSame(ngayKham, benhAn.getNgayKham());
        assertEquals("Sot", benhAn.getTrieuChung());
        assertEquals("Khong", benhAn.getTienSuBenh());
        assertEquals("Cam cum", benhAn.getChanDoan());
        assertEquals("R001", benhAn.getRoomId());
    }

    @Test
    void createBenhAnWithDefaultConstructor() {
        BenhAn benhAn = new BenhAn();

        assertNull(benhAn.getId());
        assertNull(benhAn.getPatientId());
    }

    @Test
    void setIdShouldUpdateId() {
        BenhAn benhAn = new BenhAn();
        benhAn.setId("BA002");

        assertEquals("BA002", benhAn.getId());
    }

    @Test
    void setPatientIdShouldUpdatePatientId() {
        BenhAn benhAn = new BenhAn();
        benhAn.setPatientId("P002");

        assertEquals("P002", benhAn.getPatientId());
    }

    @Test
    void setNgayKhamShouldUpdateNgayKham() {
        Calendar ngayKham = Calendar.getInstance();
        BenhAn benhAn = new BenhAn();
        benhAn.setNgayKham(ngayKham);

        assertSame(ngayKham, benhAn.getNgayKham());
    }

    @Test
    void setTrieuChungShouldUpdateTrieuChung() {
        BenhAn benhAn = new BenhAn();
        benhAn.setTrieuChung("Ho");

        assertEquals("Ho", benhAn.getTrieuChung());
    }

    @Test
    void setTienSuBenhShouldUpdateTienSuBenh() {
        BenhAn benhAn = new BenhAn();
        benhAn.setTienSuBenh("Hen");

        assertEquals("Hen", benhAn.getTienSuBenh());
    }

    @Test
    void setChanDoanShouldUpdateChanDoan() {
        BenhAn benhAn = new BenhAn();
        benhAn.setChanDoan("Viem hong");

        assertEquals("Viem hong", benhAn.getChanDoan());
    }

    @Test
    void setRoomIdShouldUpdateRoomId() {
        BenhAn benhAn = new BenhAn();
        benhAn.setRoomId("R002");

        assertEquals("R002", benhAn.getRoomId());
    }
}
