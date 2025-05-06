package com.tarificompany.android_project;

public class Student {
    private String stdId;
    private String name;
    private double average;
    private int stdGrade;
    private String parentPhone;

    public Student(String name, int stdGrade, String parentPhone) {
        this.name = name;
        this.stdGrade = stdGrade;
        this.parentPhone = parentPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getStdGrade() {
        return stdGrade;
    }

    public void setStdGrade(int stdGrade) {
        this.stdGrade = stdGrade;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getStdId() {
        return stdId;
    }

    @Override
    public String toString() {
        return "[ID=" + stdId + ", Name=" + name + ", Class=" + stdGrade + "Parent PhoneNo=" + parentPhone + "]";
    }
}
