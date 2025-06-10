package com.tarificompany.android_project;

public class Teacher {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String subject;
    private String joiningDate;
    private String notes;

    public Teacher(String id, String fullName, String email, String phone, String subject, String joiningDate, String notes) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
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

    public String getSubject() {
        return subject;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}