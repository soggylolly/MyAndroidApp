package com.example.myandroidapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import androidx.room.Update;
import androidx.room.Delete;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

}
