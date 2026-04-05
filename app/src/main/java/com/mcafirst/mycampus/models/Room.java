package com.mcafirst.mycampus.models;

public class Room {
    public String id;
    public String name;
    public String nodeId;
    public int floor;
    public int currentOccupancy;
    public int maxCapacity;

    public Room(String id, String name, String nodeId, int floor) {
        this.id = id;
        this.name = name;
        this.nodeId = nodeId;
        this.floor = floor;
        this.currentOccupancy = 0;
        this.maxCapacity = 30; // Default capacity
    }

    public Room(String id, String name, String nodeId, int floor, int currentOccupancy, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.nodeId = nodeId;
        this.floor = floor;
        this.currentOccupancy = currentOccupancy;
        this.maxCapacity = maxCapacity;
    }
}
