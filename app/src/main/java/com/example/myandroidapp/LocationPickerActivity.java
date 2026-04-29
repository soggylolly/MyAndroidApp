package com.example.myandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class LocationPickerActivity extends AppCompatActivity {

    // This is the map view from the XML layout
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads the osmdroid settings for the map
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_location_picker);

        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
        mapView.getController().setCenter(new GeoPoint(52.195, -2.225));

        // This detects when the user presses on the map
        MapEventsOverlay overlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // Put a marker on the map where the user pressed
                Marker marker = new Marker(mapView);
                marker.setPosition(p);
                marker.setTitle("Reminder location");
                mapView.getOverlays().add(marker);
                mapView.invalidate();

                // Send the selected location back to MainActivity
                Intent result = new Intent();
                result.putExtra("latitude", p.getLatitude());
                result.putExtra("longitude", p.getLongitude());
                setResult(RESULT_OK, result);
                Toast.makeText(LocationPickerActivity.this, "Location selected", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
        });

        mapView.getOverlays().add(overlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
