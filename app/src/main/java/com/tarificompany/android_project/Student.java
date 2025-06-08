package com.tarificompany.android_project;

import java.util.Date;

public class Student {
    private String stdId;
    private String name;
    private double average;
    private String stdClass;
    private String parentPhone;
    private String birthDate;

    public Student(String stdId, String name, double average, String stdClass, String parentPhone, String birthDate) {
        this.stdId = stdId;
        this.name = name;
        this.average = average;
        this.stdClass = stdClass;
        this.parentPhone = parentPhone;
        this.birthDate = birthDate;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
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

    public String getStdClass() {
        return stdClass;
    }

    public void setStdClass(String stdClass) {
        this.stdClass = stdClass;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
