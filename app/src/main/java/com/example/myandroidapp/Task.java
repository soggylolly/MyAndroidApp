package com.example.myandroidapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    // This is the unique id for the Room database
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Main reminder details
    public String title;
    public String description;
    public String dueDate;
    public String dueTime;
    public String imageUri;

    // Location for the reminder
    public double latitude;
    public double longitude;
    public boolean hasLocation;

    // Sharing and notification fields
    public boolean shared;
    public String owner;
    public String remoteId;
    public boolean notified;
}
