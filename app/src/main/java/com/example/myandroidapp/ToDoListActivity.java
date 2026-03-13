package com.example.myandroidapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.List;

import androidx.room.Room;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ToDoListActivity extends AppCompatActivity {

    AppDatabase db;
    RecyclerView recyclerView;
    TaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do_list_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "task-database"
        ).allowMainThreadQueries().build();

        recyclerView = findViewById(R.id.taskRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskListAdapter();

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Task> tasks = db.taskDao().getAllTasks();
        adapter.setTasks(tasks);
    }

    public void openNewTask(View view) {
        Log.d("ToDoAPP", "onNewTaskClicked");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);



    }


}