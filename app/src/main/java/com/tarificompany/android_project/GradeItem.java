package com.tarificompany.android_project;

public class GradeItem {
    private String subjectName;
    private String subjectCode;
    private String examName;
    private double score;
    private String publishedAt;

    public GradeItem(String subjectName, String subjectCode, String examName, double score, String publishedAt) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.examName = examName;
        this.score = score;
        this.publishedAt = publishedAt;
    }

    // Getters
    public String getSubjectName() { return subjectName; }
    public String getSubjectCode() { return subjectCode; }
    public String getExamName() { return examName; }
    public double getScore() { return score; }
    public String getPublishedAt() { return publishedAt; }
}