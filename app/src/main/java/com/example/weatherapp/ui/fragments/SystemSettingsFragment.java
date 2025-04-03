package com.example.weatherapp.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.weatherapp.R;
import com.example.weatherapp.ui.utils.widget.WeatherWidgetProvider;

public class SystemSettingsFragment extends Fragment {

    private static final String TAG = "SystemSettings"; // Log Tag

    private Spinner spinnerUnits, spinnerMaps, spinnerTheme;
    private Button btnSave;

    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";
    private static final String MAP_KEY = "map_provider";
    private static final String THEME_KEY = "theme";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_settings, container, false);

        spinnerUnits = view.findViewById(R.id.spinnerUnits);
        spinnerMaps = view.findViewById(R.id.spinnerMaps);
        spinnerTheme = view.findViewById(R.id.spinnerTheme);
        btnSave = view.findViewById(R.id.btnSave);

        // Load saved preferences
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        spinnerUnits.setSelection(getUnitIndex(prefs.getString(UNIT_KEY, "metric")));
        spinnerMaps.setSelection(getMapIndex(prefs.getString(MAP_KEY, "world_weather")));
        spinnerTheme.setSelection(getThemeIndex(prefs.getString(THEME_KEY, "system")));

        // Handle save button click
        btnSave.setOnClickListener(v -> savePreferences());

        return view;
    }

    private void savePreferences() {
        String selectedUnit = getUnitFromIndex(spinnerUnits.getSelectedItemPosition());
        String selectedMap = getMapFromIndex(spinnerMaps.getSelectedItemPosition());
        String selectedTheme = getThemeFromIndex(spinnerTheme.getSelectedItemPosition());

        Log.d(TAG, "Saving preferences: Unit=" + selectedUnit + ", Map=" + selectedMap + ", Theme=" + selectedTheme);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNIT_KEY, selectedUnit);
        editor.putString(MAP_KEY, selectedMap);
        editor.putString(THEME_KEY, selectedTheme);
        editor.apply();


        Intent intent = new Intent(requireContext(), WeatherWidgetProvider.class);
        intent.setAction(WeatherWidgetProvider.ACTION_UPDATE_WIDGET);
        requireContext().sendBroadcast(intent);

        applyTheme(selectedTheme);
        Toast.makeText(requireContext(), "Saved units: " + selectedUnit + " ,saved map: " + selectedMap + " ,saved theme: " + selectedTheme , Toast.LENGTH_LONG).show();
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

    private int getThemeIndex(String theme) {
        switch (theme) {
            case "light": return 1;
            case "dark": return 2;
            default: return 0;
        }
    }

    private String getThemeFromIndex(int index) {
        switch (index) {
            case 1: return "light";
            case 2: return "dark";
            default: return "system";
        }
    }

    private void applyTheme(String theme) {
        Log.d(TAG, "Applying theme: " + theme);

        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
