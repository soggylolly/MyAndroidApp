package com.example.myandroidapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CompletedTaskFragment extends Fragment {

    AppDatabase db;
    RecyclerView recyclerView;
    TaskListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // 1. Get database
        db = Room.databaseBuilder(
                getContext(),
                AppDatabase.class,
                "task-database"
        ).allowMainThreadQueries().build();

        // 2. Setup RecyclerView
        recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskListAdapter(db);
        recyclerView.setAdapter(adapter);

        // 3. Load data (ALL TASKS)
        List<Task> tasks = db.taskDao().getCompletedTasks();
        adapter.setTasks(tasks);

        // 4. Swipe delete (copied from activity)
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        adapter.deleteTask(position);
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}