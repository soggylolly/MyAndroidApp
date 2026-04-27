package com.example.myandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class AllTaskFragment extends Fragment implements TaskListAdapter.ReminderClickListener {

    AppDatabase db;
    TaskListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "task-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        RecyclerView recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskListAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);
        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    void loadTasks() {
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getAllTasks());
        }
    }

    @Override
    public void editReminder(Task task) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("taskId", task.id);
        startActivity(intent);
    }

    @Override
    public void deleteReminder(Task task) {
        db.taskDao().delete(task);
        loadTasks();
    }

    @Override
    public void shareReminder(Task task) {
        if (getActivity() instanceof ToDoListActivity) {
            ((ToDoListActivity) getActivity()).shareReminder(task);
        }
    }
}
