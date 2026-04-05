package com.mcafirst.mycampus.models;

public class Event {
    public String name;
    public String nodeId;
    public String time;
    public String category;

    public Event(String name, String nodeId, String time, String category) {
        this.name = name;
        this.nodeId = nodeId;
        this.time = time;
        this.category = category;
    }
}
