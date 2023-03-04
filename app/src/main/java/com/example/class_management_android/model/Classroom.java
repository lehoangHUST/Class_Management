package com.example.class_management_android.model;

public class Classroom
{
    private String id;
    private String subjectName;
    private String startTime;
    private String endTime;
    private String classroomName;
    private String weekDay;

    public Classroom()
    {

    }

    public Classroom(String id, String subjectName, String startTime, String endTime, String classroomName, String weekDay) {
        this.id = id;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomName = classroomName;
        this.weekDay = weekDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }
}
