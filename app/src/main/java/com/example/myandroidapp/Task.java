package com.example.myandroidapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String dueDate;
    public String dueTime;
    public String imageUri;

    public double latitude;
    public double longitude;
    public boolean hasLocation;

    public boolean shared;
    public String owner;
    public String remoteId;
    public boolean notified;
}
