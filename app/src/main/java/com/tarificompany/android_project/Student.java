package com.tarificompany.android_project;

public class Student {
    private String stdId;
    private String name;
    private String email;
    private String stdClass;
    private String parentPhone;
    private String birthDate;

    public Student(String stdId, String name, String email, String stdClass, String parentPhone, String birthDate) {
        this.stdId = stdId;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
