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

import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.room.Room;

import android.widget.ImageView;
import android.net.Uri;

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

            View taskView = getLayoutInflater().inflate(R.layout.task_layout, null);

            ImageView imageView = taskView.findViewById(R.id.taskListImage);
            TextView title = taskView.findViewById(R.id.taskListTitle);
            TextView desc = taskView.findViewById(R.id.taskListDesc);

            title.setText(task.title);
            desc.setText(task.description);

            if(task.imageUri != null){
                Uri uri = Uri.parse(task.imageUri);
                imageView.setImageURI(uri);
            }

            taskListLayout.addView(taskView);
        }
    }

    public void openNewTask(View view) {
        Log.d("ToDoAPP", "onNewTaskClicked");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);



    }


}