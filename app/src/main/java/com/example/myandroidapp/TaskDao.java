package com.example.myandroidapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import androidx.room.Update;
import androidx.room.Delete;
/* Task class represents the data model for a task in the ToDo application
@Entity means that this class should be stored as a
table in the application's SQLite database. */

/* TaskDao (Data Access Object) defines how the application interacts with the Room database.
 It acts as an interface between the code and SQLite database.*/
@Dao
public interface TaskDao {

    /* This query retrieves all tasks stored in the database.
    - Retrieve every column
    - From the Task table
    The result is returned as a List of Task objects. */

    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

    // Inserts a new task into the database.
    @Insert
    void insert(Task task);

    // Updates an existing task in the database.
    @Update
    void update(Task task);

    // Deletes a task from the database.
    @Delete
    void delete(Task task);

    @Query("SELECT * FROM Task WHERE done = 1")
    List<Task> getCompletedTasks();

    @Query("SELECT * FROM Task WHERE done = 0")
    List<Task> getPendingTasks();

}
