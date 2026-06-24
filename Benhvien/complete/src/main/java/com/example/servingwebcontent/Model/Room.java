package com.example.servingwebcontent.Model;

public class Room implements CoId {
    private String id;
    private String name;
    private String doctorName;
    private Integer capacity;
    private String status;

    public Room() {}

    public Room(String id, String name, String doctorName) {
        this.id = id;
        this.name = name;
        this.doctorName = doctorName;
        this.capacity = null;
        this.status = "Hoạt động";
    }

    public Room(String id, String name, String doctorName, Integer capacity, String status) {
        this.id = id;
        this.name = name;
        this.doctorName = doctorName;
        this.capacity = capacity;
        this.status = status != null ? status : "Hoạt động";
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDoctorName() {
        return doctorName;
    }
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return String.format(
            "Mã phòng: %s | Tên phòng: %s\nBác sĩ phụ trách: %s",
            id, name, doctorName);
    }
}
