package com.example.myandroidapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

}
