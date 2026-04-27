package com.example.myandroidapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY id DESC")
    List<Task> getAllTasks();

    @Query("SELECT * FROM Task WHERE shared = 0 ORDER BY id DESC")
    List<Task> getMyTasks();

    @Query("SELECT * FROM Task WHERE shared = 1 ORDER BY id DESC")
    List<Task> getSharedTasks();

    @Query("SELECT * FROM Task WHERE id = :id LIMIT 1")
    Task getTaskById(int id);

    @Query("SELECT * FROM Task WHERE remoteId = :remoteId LIMIT 1")
    Task getTaskByRemoteId(String remoteId);

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
