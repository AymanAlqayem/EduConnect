package com.tarificompany.android_project;

import java.io.Serializable;

public class ClassSchedule implements Serializable {
    private String className;
    private String time;
    private String classGroup;
    private String room;
    private String day;
    private int studentCount;
    private String sectionName;

    public ClassSchedule(String className, String time, String classGroup, String room, String day, int studentCount, String sectionName) {
        this.className = className;
        this.time = time;
        this.classGroup = classGroup;
        this.room = room;
        this.day = day;
        this.studentCount = studentCount;
        this.sectionName = sectionName;
    }

    public ClassSchedule(String className, String time, String classGroup) {
        this.className = className;
        this.time = time;
        this.classGroup = classGroup;
        this.room = "";
        this.day = "";
        this.studentCount = 0;
        this.sectionName = "";
    }

    public String getClassName() {
        return className;
    }

    public String getTime() {
        return time;
    }

    public String getClassGroup() {
        return classGroup;
    }

    public String getRoom() {
        return room;
    }

    public String getDay() {
        return day;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public String getSectionName() {
        return sectionName;
    }
}