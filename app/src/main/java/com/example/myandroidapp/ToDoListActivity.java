package com.example.myandroidapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.View;

import java.util.List;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.room.Room;

public class ToDoListActivity extends AppCompatActivity {

    AppDatabase db;
    LinearLayout taskListLayout;

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

        taskListLayout = findViewById(R.id.taskListLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        taskListLayout.removeAllViews();

        List<Task> tasks = db.taskDao().getAllTasks();

        for (Task task : tasks) {

            TextView textView = new TextView(this);
            textView.setText(task.title);

            taskListLayout.addView(textView);

        }
    }

    public void openNewTask(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


}