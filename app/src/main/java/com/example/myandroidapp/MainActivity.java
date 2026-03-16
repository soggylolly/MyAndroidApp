package com.example.myandroidapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.app.DatePickerDialog;

import java.util.Calendar;

import android.app.TimePickerDialog;

import androidx.room.Room;

import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.ImageView;

import java.io.File;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
/* The above imports are used to allow for code to use classes, methods, and resources
 from other packages already available in android studio without the need to make all
  code from scratch */

/* The Mainactivity class is used for creating new tasks in the application */
public class MainActivity extends AppCompatActivity {

    // Reference to the Room database used to store tasks
    AppDatabase db;

    // URI used to store the location of a captured image
    Uri imageUri;

    // Launcher used to open the camera and receive the result
    ActivityResultLauncher<Intent> cameraLauncher;

    /* onCreate() is called when the activity is first created.
    This is where the layout is loaded and the app components are initialised. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge layout
        EdgeToEdge.enable(this);

        // Loads the activity_main.xml layout
        setContentView(R.layout.activity_main);

        /* Ensures the layout adjusts correctly around system bars
         (such as the status bar and navigation bar). */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Creates the Room database instance. */
        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "task-database"
        ).allowMainThreadQueries().build();

        // Retrieve the save task button from the UI layout
        Button saveButton = findViewById(R.id.saveTaskButton);

        /* Event listener triggered when the Save Task button is clicked.
        This collects user input and stores the task in the database. */
        saveButton.setOnClickListener(v -> {

            // Retrieve the text fields where the user enters task details
            EditText title = findViewById(R.id.taskTitleView);
            EditText description = findViewById(R.id.taskDescriptionView);
            EditText date = findViewById(R.id.taskDueDateView);
            EditText time = findViewById(R.id.taskDueTimeView);

            // Create a new Task object
            Task task = new Task();

            // Store user input into the task object
            task.title = title.getText().toString();
            task.description = description.getText().toString();
            task.dueDate = date.getText().toString();
            task.dueTime = time.getText().toString();

            // If the user captured an image, store the image URI
            if(imageUri != null){
                task.imageUri = imageUri.toString();
            }

            // Insert the task into the database using the DAO
            db.taskDao().insert(task);

            // Display confirmation message
            Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show();

            // Log message for debugging
            Log.d("ToDoApp","Save button clicked");

            // Close the activity after saving the task
            finish();
        });

        /* Register a camera activity launcher.
        This allows the app to open the device camera and receive the captured image. */
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    // Check if the camera successfully captured an image
                    if (result.getResultCode() == RESULT_OK) {

                        // Display the captured image in the ImageView
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(imageUri);

                        Log.d("Camera", "Image captured");

                    }

                });

    }

    /* Opens a DatePicker dialog allowing the user to select a due date for the task. */
    public void openDatePicker(View view){

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, y, m, d) -> {

                    // Insert the selected date into the text field
                    EditText dateInput = findViewById(R.id.taskDueDateView);
                    dateInput.setText(d + "/" + (m+1) + "/" + y);

                },
                year, month, day
        );

        dialog.show();
    }

    /* Opens a TimePicker dialog allowing the user to select a due time for the task. */
    public void openTimePicker(View view) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (timePicker, h, m) -> {
                    // Insert selected time into the text field
                    EditText timeInput = findViewById(R.id.taskDueTimeView);
                    timeInput.setText(String.format("%02d:%02d", h, m));
                },
                hour, minute, true
        );

        dialog.show();
    }

    /* Launches the device camera so the user can capture an image related to the task. */
    public void onCameraClick(View view) {

        // Create a file where the captured image will be stored
        File imageFile = new File(
                getFilesDir(),
                System.currentTimeMillis() + ".jpg"
        );

        /* FileProvider generates a secure URI allowing external apps
        (camera) to write the image file. */
        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".file provider",
                imageFile
        );

        // Create an intent to launch the device camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Tell the camera where to save the captured image
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // Launch the camera
        cameraLauncher.launch(cameraIntent);

    }
}