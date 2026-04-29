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

    // These are used for the database and the list on screen
    AppDatabase db;
    TaskListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // This loads the fragment layout with the RecyclerView
        return inflater.inflate(R.layout.fragment_all_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Open the Room database
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "task-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Set up the RecyclerView list
        RecyclerView recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskListAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);
        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload tasks when coming back to the screen
        loadTasks();
    }

    void loadTasks() {
        // Shows every reminder in the database
        if (db != null && adapter != null) {
            adapter.setTasks(db.taskDao().getAllTasks());
        }
    }

    @Override
    public void editReminder(Task task) {
        // Open MainActivity and pass the id so it can edit this reminder
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("taskId", task.id);
        startActivity(intent);
    }

    @Override
    public void deleteReminder(Task task) {
        // Delete reminder from the database
        db.taskDao().delete(task);
        loadTasks();
    }

    @Override
    public void shareReminder(Task task) {
        // Sends the reminder to the main activity so it can share it
        if (getActivity() instanceof ToDoListActivity) {
            ((ToDoListActivity) getActivity()).shareReminder(task);
        }
    }
}
