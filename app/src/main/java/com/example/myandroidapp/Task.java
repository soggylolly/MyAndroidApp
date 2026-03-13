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

    public boolean done;
    public String imageUri;


}