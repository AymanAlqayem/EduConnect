package com.tarificompany.android_project;

public class Teacher {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String subject;
    private String joiningDate;
    private String notes;

    public Teacher(String id, String fullName, String email, String phone, String gender, String subject, String joiningDate, String notes) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.subject = subject;
        this.joiningDate = joiningDate;
        this.notes = notes;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getSubject() {
        return subject;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public String getNotes() {
        return notes;
    }
}