package com.mcafirst.mycampus.models;

public class DirectoryItem {
    public enum Type { ROOM, FACULTY }

    public String name;
    public String details;
    public String status;
    public String nodeId;
    public String officeHours; // Added officeHours
    public Type type;

    public DirectoryItem(String name, String details, String status, String nodeId, Type type) {
        this(name, details, status, nodeId, type, null);
    }

    public DirectoryItem(String name, String details, String status, String nodeId, Type type, String officeHours) {
        this.name = name;
        this.details = details;
        this.status = status;
        this.nodeId = nodeId;
        this.type = type;
        this.officeHours = officeHours;
    }
}
