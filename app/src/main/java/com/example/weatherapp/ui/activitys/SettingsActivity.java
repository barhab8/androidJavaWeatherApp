package com.example.weatherapp.ui.activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerUnits, spinnerMaps;
    private Button btnSave;
    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";
    private static final String MAP_KEY = "map_provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerUnits = findViewById(R.id.spinnerUnits);
        spinnerMaps = findViewById(R.id.spinnerMaps);
        btnSave = findViewById(R.id.btnSave);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        spinnerUnits.setSelection(getUnitIndex(prefs.getString(UNIT_KEY, "metric")));
        spinnerMaps.setSelection(getMapIndex(prefs.getString(MAP_KEY, "world_weather")));

        // Handle save button click
        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        String selectedUnit = getUnitFromIndex(spinnerUnits.getSelectedItemPosition());
        String selectedMap = getMapFromIndex(spinnerMaps.getSelectedItemPosition());

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNIT_KEY, selectedUnit);
        editor.putString(MAP_KEY, selectedMap);
        editor.apply();

        finish(); // Close settings after saving
    }

    private int getUnitIndex(String unit) {
        switch (unit) {
            case "imperial": return 1;
            case "standard": return 2;
            default: return 0;
        }
    }

    private String getUnitFromIndex(int index) {
        switch (index) {
            case 1: return "imperial";
            case 2: return "standard";
            default: return "metric";
        }
    }

    private int getMapIndex(String map) {
        switch (map) {
            case "open_weather": return 1;
            case "zoom_earth": return 2;
            default: return 0;
        }
    }

    private String getMapFromIndex(int index) {
        switch (index) {
            case 1: return "open_weather";
            case 2: return "zoom_earth";
            default: return "world_weather";
        }
    }
}
