package com.example.myandroidapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // This number is used when asking Android for location permission
    private static final int LOCATION_PERMISSION_CODE = 33;

    // These variables store the database and the reminder details
    AppDatabase db;
    Task editingTask;
    Uri imageUri;
    double latitude;
    double longitude;
    boolean hasLocation;

    EditText titleInput;
    EditText descriptionInput;
    EditText dueDateInput;
    EditText dueTimeInput;
    TextView locationText;
    ImageView imageView;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> mapLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This opens the Room database so reminders can be saved
        db = Room.databaseBuilder(this, AppDatabase.class, "task-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // These connect the Java code to the views in the XML layout
        titleInput = findViewById(R.id.taskTitleView);
        descriptionInput = findViewById(R.id.taskDescriptionView);
        dueDateInput = findViewById(R.id.taskDueDateView);
        dueTimeInput = findViewById(R.id.taskDueTimeView);
        locationText = findViewById(R.id.locationText);
        imageView = findViewById(R.id.imageView);
        Button saveButton = findViewById(R.id.saveTaskButton);

        // This gets the result after the user takes a photo
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && imageUri != null) {
                        imageView.setImageURI(imageUri);
                    }
                }
        );

        // This gets the latitude and longitude back from the map screen
        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        latitude = result.getData().getDoubleExtra("latitude", 0);
                        longitude = result.getData().getDoubleExtra("longitude", 0);
                        hasLocation = true;
                        updateLocationText();
                    }
                }
        );

        // If a task id is sent to this screen then the user is editing a reminder
        int taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            editingTask = db.taskDao().getTaskById(taskId);
            loadTaskForEditing();
        }

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void loadTaskForEditing() {
        if (editingTask == null) {
            return;
        }

        // Fill the boxes with the reminder details already saved
        TextView formTitle = findViewById(R.id.formTitle);
        formTitle.setText("Edit Reminder");

        titleInput.setText(editingTask.title);
        descriptionInput.setText(editingTask.description);
        dueDateInput.setText(editingTask.dueDate);
        dueTimeInput.setText(editingTask.dueTime);
        latitude = editingTask.latitude;
        longitude = editingTask.longitude;
        hasLocation = editingTask.hasLocation;

        if (editingTask.imageUri != null && !editingTask.imageUri.isEmpty()) {
            imageUri = Uri.parse(editingTask.imageUri);
            imageView.setImageURI(imageUri);
        }

        updateLocationText();
    }

    private void saveReminder() {
        // Get the values typed by the user
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        String dueTime = dueTimeInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleInput.setError("Title needed");
            return;
        }

        if (!hasLocation) {
            Toast.makeText(this, "Please save a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Either make a new reminder or update the old one
        Task task = editingTask == null ? new Task() : editingTask;
        task.title = title;
        task.description = description;
        task.dueDate = dueDate;
        task.dueTime = dueTime;
        task.latitude = latitude;
        task.longitude = longitude;
        task.hasLocation = hasLocation;
        task.shared = false;
        task.owner = "Me";
        task.notified = false;

        if (imageUri != null) {
            task.imageUri = imageUri.toString();
        }

        // Save the reminder into the local database
        if (editingTask == null) {
            db.taskDao().insert(task);
        } else {
            db.taskDao().update(task);
        }

        Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCameraClick(View view) {
        // Create a file for the camera photo
        File imageFile = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");
        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                imageFile
        );

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(cameraIntent);
    }

    public void openDatePicker(View view) {
        // Shows the normal Android date picker
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, year, month, day) ->
                        dueDateInput.setText(day + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    public void openTimePicker(View view) {
        // Shows the normal Android time picker
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (timePicker, hour, minute) ->
                        dueTimeInput.setText(String.format("%02d:%02d", hour, minute)),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    public void saveCurrentLocation(View view) {
        // Ask for permission before trying to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        // Try GPS first, then network location if GPS is empty
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (location == null) {
            Toast.makeText(this, "Location not found. Try again outside.", Toast.LENGTH_LONG).show();
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        hasLocation = true;
        updateLocationText();
    }

    public void openMap(View view) {
        // Opens the map activity so the user can pick a place
        mapLauncher.launch(new Intent(this, LocationPickerActivity.class));
    }

    private void updateLocationText() {
        // Updates the text so the user can see if a location is saved
        if (hasLocation) {
            locationText.setText("Location: " + latitude + ", " + longitude);
        } else {
            locationText.setText("No location saved");
        }
    }
}
