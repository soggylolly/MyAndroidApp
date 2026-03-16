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

import androidx.recyclerview.widget.ItemTouchHelper;
/* The above imports are used to allow for code to use classes, methods, and resources
 from other packages already available in android studio without the need to make all
  code from scratch */

/* This class is responsible for displaying all tasks stored in the database */
public class ToDoListActivity extends AppCompatActivity {

    // Reference to the Room database
    AppDatabase db;

    // RecyclerView used to display the list of tasks
    RecyclerView recyclerView;

    // Adapter used to bind task data to the RecyclerView
    TaskListAdapter adapter;

    /* onCreate() is called when the activity is first created.
    This method initialises the UI and database connection. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge UI layout
        EdgeToEdge.enable(this);

        // Loads the layout file activity_to_do_list_layout.xml
        setContentView(R.layout.activity_to_do_list_layout);

        /* Adjusts layout padding so the UI fits correctly around system bars such as the status bar. */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Creates a connection to the Room database.
        This database stores all tasks entered by the user. */
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "task-database"
        ).allowMainThreadQueries().build();

        // Retrieve the RecyclerView component from the layout
        recyclerView = findViewById(R.id.taskRecyclerView);

        // LinearLayoutManager arranges RecyclerView items in a vertical scrolling list.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter which connects task data to the RecyclerView UI elements.
        adapter = new TaskListAdapter(db);

        // Attach the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        /* ItemTouchHelper enables swipe gestures on list items. In this application,
        swiping left or right deletes a task. */
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    // Drag-and-drop movement is not used in this app
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    /* Triggered when a task item is swiped.
                    The corresponding task is removed from the database. */
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        int position = viewHolder.getAdapterPosition();

                        // Delete the task at the swiped position
                        adapter.deleteTask(position);

                    }
                };

        // Attach swipe functionality to the RecyclerView
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    /* onResume() runs whenever the activity becomes visible again. */
    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve all tasks from the database
        List<Task> tasks = db.taskDao().getAllTasks();

        // Send the task list to the adapter so it can update the UI
        adapter.setTasks(tasks);
    }

    /* openNewTask is triggered when the "New Task" button is clicked
    opening the MainActivity where the user can create a new task. */
    public void openNewTask(View view) {
        Log.d("ToDoAPP", "onNewTaskClicked");

        // Create an intent to open the task creation activity
        Intent intent = new Intent(this, MainActivity.class);

        // Start the activity
        startActivity(intent);



    }


}