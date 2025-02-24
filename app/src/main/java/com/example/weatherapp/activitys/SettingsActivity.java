package com.example.weatherapp.activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerUnits;
    private Button btnSave;
    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerUnits = findViewById(R.id.spinnerUnits);
        btnSave = findViewById(R.id.btnSave);

        // Load saved unit preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUnit = prefs.getString(UNIT_KEY, "metric"); // Default is metric
        spinnerUnits.setSelection(getUnitIndex(savedUnit));

        // Handle save button click
        btnSave.setOnClickListener(v -> saveUnitPreference());
    }

    private void saveUnitPreference() {
        String selectedUnit = getUnitFromIndex(spinnerUnits.getSelectedItemPosition());

        // Save selection
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNIT_KEY, selectedUnit);
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
}
