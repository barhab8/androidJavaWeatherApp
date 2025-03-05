package com.example.weatherapp.ui.activitys;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.example.weatherapp.R;
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "notification"; // Log Tag

    private Spinner spinnerUnits, spinnerMaps, spinnerTheme;
    private Button btnSave, btnNotificationSettings;
    private TimePicker timePicker;

    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";
    private static final String MAP_KEY = "map_provider";
    private static final String THEME_KEY = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerUnits = findViewById(R.id.spinnerUnits);
        spinnerMaps = findViewById(R.id.spinnerMaps);
        spinnerTheme = findViewById(R.id.spinnerTheme);
        btnSave = findViewById(R.id.btnSave);
        btnNotificationSettings = findViewById(R.id.btnNotificationSettings);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        spinnerUnits.setSelection(getUnitIndex(prefs.getString(UNIT_KEY, "metric")));
        spinnerMaps.setSelection(getMapIndex(prefs.getString(MAP_KEY, "world_weather")));
        spinnerTheme.setSelection(getThemeIndex(prefs.getString(THEME_KEY, "system")));

        // Handle save button click
        btnSave.setOnClickListener(v -> savePreferences());
        btnNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, NotificationSettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void savePreferences() {
        String selectedUnit = getUnitFromIndex(spinnerUnits.getSelectedItemPosition());
        String selectedMap = getMapFromIndex(spinnerMaps.getSelectedItemPosition());
        String selectedTheme = getThemeFromIndex(spinnerTheme.getSelectedItemPosition());

        Log.d(TAG, "Saving preferences: Unit=" + selectedUnit + ", Map=" + selectedMap + ", Theme=" + selectedTheme);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UNIT_KEY, selectedUnit);
        editor.putString(MAP_KEY, selectedMap);
        editor.putString(THEME_KEY, selectedTheme);
        editor.apply();

        applyTheme(selectedTheme);

        Log.d(TAG, "Preferences saved. Restarting SettingsActivity to apply theme changes.");
        finish();
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
