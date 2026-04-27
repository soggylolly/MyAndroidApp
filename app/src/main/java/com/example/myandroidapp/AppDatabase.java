package com.example.myandroidapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
/* The above imports are used to allow for code to use classes, methods, and resources
 from other packages already available in android studio without the need to make all
  code from scratch */

/* AppDatabase defines the Room database used by the application.
This class connects the data model (Task) with the Data Access Object (TaskDao). */
@Database(
        entities = {Task.class}, // Defines the tables stored in the database
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    // Provides access to the TaskDao.
    public abstract TaskDao taskDao();

}
