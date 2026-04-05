package com.mcafirst.mycampus.models;

public class ScheduleItem {
    public String day;
    public String courseCode;
    public String courseName;
    public String startTime;
    public String endTime;
    public String nodeId;
    public String note;

    public ScheduleItem(String day, String courseCode, String courseName, String startTime, String endTime, String nodeId, String note) {
        this.day = day;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nodeId = nodeId;
        this.note = note;
    }
}
