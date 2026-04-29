package com.example.myandroidapp;

public class PendingTaskFragment extends AllTaskFragment {
    @Override
    void loadTasks() {
        // This tab only shows reminders shared by other users
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getSharedTasks());
        }
    }
}
