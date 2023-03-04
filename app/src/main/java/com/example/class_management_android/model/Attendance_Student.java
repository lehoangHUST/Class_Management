package com.example.class_management_android.model;

public class Attendance_Student {
    private String id;
    private String name;
    private int missed; // Số ngày không đi học trên lớp
    private int attend; // Số ngày có mặt trên lớp học

    // Khởi tạo class không có tham số truyền vào
    public Attendance_Student(){

    }

    // Khởi tạo class có tham số truyền vào
    public Attendance_Student(String id, String name, int missed, int attend){
        this.id = id;
        this.name = name;
        this.missed = missed;
        this.attend = attend;
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

    public int getAttend() {
        return attend;
    }

    public int getMissed() {
        return missed;
    }

    public void setAttend(int attend) {
        this.attend = attend;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }
}
