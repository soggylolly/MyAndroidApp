package com.example.myandroidapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    // Get every reminder
    @Query("SELECT * FROM Task ORDER BY id DESC")
    List<Task> getAllTasks();

    // Get reminders created by this user
    @Query("SELECT * FROM Task WHERE shared = 0 ORDER BY id DESC")
    List<Task> getMyTasks();

    // Get reminders shared from Firebase
    @Query("SELECT * FROM Task WHERE shared = 1 ORDER BY id DESC")
    List<Task> getSharedTasks();

    // Used when editing a reminder
    @Query("SELECT * FROM Task WHERE id = :id LIMIT 1")
    Task getTaskById(int id);

    // Stops the same shared reminder being saved twice
    @Query("SELECT * FROM Task WHERE remoteId = :remoteId LIMIT 1")
    Task getTaskByRemoteId(String remoteId);

    // Add, update and delete reminders
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
