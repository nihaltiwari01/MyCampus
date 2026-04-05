package com.mcafirst.mycampus.models;

public class Faculty {
    public String name;
    public String nodeId;
    public String status; // present, in-class, unavailable
    public String officeHours;
    public String roomNumber;

    public Faculty(String name, String nodeId, String status, String officeHours, String roomNumber) {
        this.name = name;
        this.nodeId = nodeId;
        this.status = status;
        this.officeHours = officeHours;
        this.roomNumber = roomNumber;
    }
}
