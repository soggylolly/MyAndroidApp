package com.example.myandroidapp;

import android.os.Bundle;

public class CompletedTaskFragment extends AllTaskFragment {
    @Override
    void loadTasks() {
        // This tab only shows reminders made on this phone
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getMyTasks());
        }
    }
}
