package com.tarificompany.android_project;

public class ClassSchedule {
    private String className;
    private String time;
    private String classGroup;

    public ClassSchedule(String className, String time, String classGroup) {
        this.className = className;
        this.time = time;
        this.classGroup = classGroup;
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
}