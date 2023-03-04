package com.example.class_management_android.model;

public class Attendance {
    private String idStudent;
    private String nameStudent;
    private int state;
    public Attendance(){

    }

    public Attendance(String idStudent, String nameStudent, int state) {
        this.idStudent = idStudent;
        this.nameStudent = nameStudent;
        this.state = state;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getNameStudent() {
        return nameStudent;
    }

    public void setNameStudent(String nameStudent) {
        this.nameStudent = nameStudent;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
