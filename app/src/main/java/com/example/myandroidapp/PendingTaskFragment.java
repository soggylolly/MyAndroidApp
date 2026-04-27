package com.example.myandroidapp;

public class PendingTaskFragment extends AllTaskFragment {
    @Override
    void loadTasks() {
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getSharedTasks());
        }
    }
}
