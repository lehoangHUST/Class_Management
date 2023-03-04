package com.example.class_management_android.model;

public class Student {

    private String id;
    private String name;
    private String birthday;
    private int gender;
    private String classID;
    private String phoneNumber;
    private String email;
    private String group;
    private String midterm;
    private String finalterm;
    public Student() {

    }

    public Student(String id, String name, String birthday, int gender,String phoneNumber, String email,String classID, String group, String midterm, String finalterm) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.classID = classID;
        this.group = group;
        this.midterm = midterm;
        this.finalterm = finalterm;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassId() {
        return classID;
    }

    public void setClassId(String classID) {
        this.classID = classID;
    }

    public void setGroup(String group) {this.group = group;}

    public void setMidterm(String midterm) {
        this.midterm = midterm;
    }

    public void setFinalterm(String finalterm) {
        this.finalterm = finalterm;
    }

    public String getGroup() {
        return group;
    }

    public String getMidterm() {
        return midterm;
    }

    public String getFinalterm() {
        return finalterm;
    }

}