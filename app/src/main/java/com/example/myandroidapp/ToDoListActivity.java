package com.example.myandroidapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    // Codes used for permissions and notifications
    private static final int LOCATION_PERMISSION_CODE = 22;
    private static final String CHANNEL_ID = "reminder_channel";

    // Main objects used by this screen
    AppDatabase db;
    FirebaseFirestore firestore;
    ListenerRegistration listenerRegistration;
    LocationManager locationManager;
    LocationListener locationListener;
    SensorManager sensorManager;
    Sensor lightSensor;
    Sensor significantMotionSensor;
    SensorEventListener lightSensorListener;
    TriggerEventListener motionListener;
    String userId;

    AllTaskFragment allTaskFragment = new AllTaskFragment();
    CompletedTaskFragment myTaskFragment = new CompletedTaskFragment();
    PendingTaskFragment sharedTaskFragment = new PendingTaskFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_layout);

        // Open the local Room database
        db = Room.databaseBuilder(this, AppDatabase.class, "task-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Connect to Firebase so reminders can be shared
        firestore = FirebaseFirestore.getInstance();
        userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Setup notifications and sensors when the app starts
        createNotificationChannel();
        setupSensors();
        requestNotificationPermission();

        // Show all reminders first
        loadFragment(allTaskFragment);

        // Bottom menu changes between all, mine and shared reminders
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_all) {
                loadFragment(allTaskFragment);
                return true;
            }
            if (item.getItemId() == R.id.nav_completed) {
                loadFragment(myTaskFragment);
                return true;
            }
            if (item.getItemId() == R.id.nav_pending) {
                loadFragment(sharedTaskFragment);
                return true;
            }
            return false;
        });

        // Start checking where the user is
        startLocationChecking();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Listen for reminders shared by other users
        listenForSharedReminders();

        // Start the light sensor again when the app is visible
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Also restart location checks when the screen comes back
        registerMotionSensor();
        startLocationChecking();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop Firebase listener when app is not in front
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        // Stop sensor and location work to save battery
        if (sensorManager != null && lightSensorListener != null) {
            sensorManager.unregisterListener(lightSensorListener);
        }

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void setupSensors() {
        // Get the sensor manager from Android
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager == null) {
            return;
        }

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Change the screen brightness based on light level
                float brightness = Math.min(1f, event.values[0] / 1000f);
                android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = brightness;
                getWindow().setAttributes(params);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        significantMotionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        motionListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                // If the phone moves, check the location again
                startLocationChecking();
                registerMotionSensor();
            }
        };
    }

    private void registerMotionSensor() {
        if (sensorManager != null && significantMotionSensor != null && motionListener != null) {
            sensorManager.requestTriggerSensor(motionListener, significantMotionSensor);
        }
    }

    public void shareReminder(Task task) {
        // Save this reminder to Firestore so other users can get it
        task.owner = userId;
        task.shared = false;

        firestore.collection("reminders")
                .document(String.valueOf(task.id) + "-" + userId)
                .set(task)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Reminder shared", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Share failed", Toast.LENGTH_SHORT).show());
    }

    private void listenForSharedReminders() {
        // Listen for reminders in Firebase
        listenerRegistration = firestore.collection("reminders")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }

                    for (QueryDocumentSnapshot document : value) {
                        Task task = document.toObject(Task.class);

                        // Do not save reminders that belong to this phone
                        if (task.owner == null || task.owner.equals(userId)) {
                            continue;
                        }

                        task.remoteId = document.getId();
                        task.shared = true;

                        Task existing = db.taskDao().getTaskByRemoteId(task.remoteId);
                        if (existing == null) {
                            // Save shared reminders locally so they show in the list
                            task.id = 0;
                            db.taskDao().insert(task);
                        }
                    }

                    refreshLists();
                });
    }

    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void openNewTask(View view) {
        // Opens the add reminder screen
        startActivity(new Intent(this, MainActivity.class));
    }

    private void refreshLists() {
        // Reload the lists after Firebase changes
        if (allTaskFragment.isAdded()) {
            allTaskFragment.loadTasks();
        }

        if (myTaskFragment.isAdded()) {
            myTaskFragment.loadTasks();
        }

        if (sharedTaskFragment.isAdded()) {
            sharedTaskFragment.loadTasks();
        }
    }

    private void startLocationChecking() {
        // Ask for location permission if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            ActivityCompat.requestPermissions(
                    this,
                    permissions.toArray(new String[0]),
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = this::checkRemindersNearLocation;

        // Try to get updates from GPS and network location
        if (locationManager != null) {
            Location lastLocation = null;

            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            10000,
                            10,
                            locationListener
                    );
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            10000,
                            10,
                            locationListener
                    );

                    if (lastLocation == null) {
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            } catch (SecurityException ignored) {
            }

            // Also check straight away if Android already knows the location
            if (lastLocation != null) {
                checkRemindersNearLocation(lastLocation);
            }
        }
    }

    private void checkRemindersNearLocation(Location currentLocation) {
        // Go through all reminders and check the distance
        List<Task> tasks = db.taskDao().getAllTasks();

        for (Task task : tasks) {
            if (!task.hasLocation || task.notified) {
                continue;
            }

            float[] distance = new float[1];
            Location.distanceBetween(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    task.latitude,
                    task.longitude,
                    distance
            );

            // If the user is close, show the notification
            if (distance[0] < 100) {
                showReminderNotification(task);
                task.notified = true;
                db.taskDao().update(task);
            }
        }
    }

    private void showReminderNotification(Task task) {
        // Android 13 and newer needs notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
            return;
        }

        Intent intent = new Intent(this, ToDoListActivity.class);

        // This opens the app when the user taps the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                task.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        // Add the photo to the notification if there is one
        Bitmap bitmap = getReminderBitmap(task);
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        }

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.id, notification);
    }

    private void requestNotificationPermission() {
        // Ask permission for notifications on newer Android phones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    LOCATION_PERMISSION_CODE
            );
        }
    }

    private Bitmap getReminderBitmap(Task task) {
        // Loads the reminder photo for the notification
        if (task.imageUri == null || task.imageUri.isEmpty()) {
            return null;
        }

        try {
            InputStream stream = getContentResolver().openInputStream(Uri.parse(task.imageUri));
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private void createNotificationChannel() {
        // Android needs a notification channel before showing notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationChecking();
        }
    }
}
