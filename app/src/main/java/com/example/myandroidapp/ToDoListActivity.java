package com.example.myandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ToDoListActivity extends AppCompatActivity {

    // ✅ Fragments from worksheet
    AllTaskFragment allTaskFragment = new AllTaskFragment();
    CompletedTaskFragment completedTaskFragment = new CompletedTaskFragment();
    PendingTaskFragment pendingTaskFragment = new PendingTaskFragment();

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

        // ✅ Default fragment (All Tasks)
        loadFragment(allTaskFragment);

        // ✅ Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_all) {
                loadFragment(allTaskFragment);
                return true;
            }

            if (item.getItemId() == R.id.nav_completed) {
                loadFragment(completedTaskFragment);
                return true;
            }

            if (item.getItemId() == R.id.nav_pending) {
                loadFragment(pendingTaskFragment);
                return true;
            }

            return false;
        });
    }

    // ✅ REQUIRED METHOD (worksheet)
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void openNewTask(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}