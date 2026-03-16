package com.example.myandroidapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/* The above imports are used to allow for code to use classes, methods, and resources
 from other packages already available in android studio without the need to make all
  code from scratch */

/* Task class represents the data model for a task in the ToDo application
@Entity means that this class should be stored as a
table in the application's SQLite database. */
@Entity
public class Task {

    /* Primary key uniquely identifies each task in the database.
    autoGenerate = true means Room will automatically assign a unique ID
    whenever a new task is inserted. */
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Title of the task entered by the user
    public String title;

    // Description providing additional details about the task
    public String description;

    // Due date selected using the DatePicker dialog
    public String dueDate;

    // Due time selected using the TimePicker dialog
    public String dueTime;

    // Boolean value used to track whether the task has been completed or not.
    public boolean done;

    // Stores the URI of an image captured by the camera.
    public String imageUri;


}