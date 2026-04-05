package com.mcafirst.mycampus.models;

public class Announcement {
    public String title;
    public String message;
    public String date;

    public Announcement(String title, String message, String date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }
}
