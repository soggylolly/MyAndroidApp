package com.example.myandroidapp;

import android.os.Bundle;

public class CompletedTaskFragment extends AllTaskFragment {
    @Override
    void loadTasks() {
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getMyTasks());
        }
    }
}
