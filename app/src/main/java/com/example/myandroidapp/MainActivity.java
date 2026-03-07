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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void onSaveClick(View view) {

        Log.d("ToDoApp","Save button clicked");

    }

    public void openDatePicker(View view){

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, y, m, d) -> {

                    EditText dateInput = findViewById(R.id.taskDueDateView);
                    dateInput.setText(d + "/" + (m+1) + "/" + y);

                },
                year, month, day
        );

        dialog.show();
    }
    public void openTimePicker(View view) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (timePicker, h, m) -> {
                    EditText timeInput = findViewById(R.id.taskDueTimeView);
                    timeInput.setText(h + ":" + m);
                },
                hour, minute, true
        );

        dialog.show();
    }
}