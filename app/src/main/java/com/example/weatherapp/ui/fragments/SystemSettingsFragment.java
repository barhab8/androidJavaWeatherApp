package com.example.weatherapp.ui.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.ReverseGeocodingResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.example.weatherapp.ui.utils.UserLocationProvider;
import com.example.weatherapp.ui.widget.WeatherWidgetProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SystemSettingsFragment extends Fragment {

    private static final String TAG = "SystemSettings"; // Log Tag

    private Spinner spinnerUnits, spinnerMaps, spinnerTheme;
    private Button btnSave;
    private EditText cityEditText;


    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";
    private static final String MAP_KEY = "map_provider";
    private static final String THEME_KEY = "theme";
    private static final String CITY_KEY = "city";

    private WeatherRepository weatherRepository;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_settings, container, false);

        spinnerUnits = view.findViewById(R.id.spinnerUnits);
        spinnerMaps = view.findViewById(R.id.spinnerMaps);
        spinnerTheme = view.findViewById(R.id.spinnerTheme);
        btnSave = view.findViewById(R.id.btnSave);
        cityEditText = view.findViewById(R.id.cityEditText);

        ImageView infoTempUnit = view.findViewById(R.id.infoTempUnit);
        ImageView infoMapProvider = view.findViewById(R.id.infoMapProvider);
        ImageView infoTheme = view.findViewById(R.id.infoTheme);
        ImageView infoCity = view.findViewById(R.id.infoWidgetCity);
        ImageView btnDetectCity = view.findViewById(R.id.btnDetectCity);


        btnDetectCity.setOnClickListener(v -> detectCity());
        weatherRepository = new WeatherRepository(requireContext());



        // Messages for info dialogs
        infoTempUnit.setOnClickListener(v -> showInfoDialog(
                "Temperature Unit",
                "Choose how temperatures are displayed throughout the app:\n\n" +
                        "• Metric (°C): Common in most countries.\n" +
                        "• Imperial (°F): Common in the United States.\n" +
                        "• Standard (Kelvin): Scientific scale.\n\n" +
                        "Your selection will affect all temperature values shown, including in widgets and notifications."));

        infoMapProvider.setOnClickListener(v -> showInfoDialog(
                "Map Provider",
                "Select the service that provides the weather maps:\n\n" +
                        "• World Weather: Default map with essential overlays.\n" +
                        "• Open Weather: Offers more detailed weather visualization.\n" +
                        "• Zoom Earth: Satellite imagery and radar layers.\n\n" +
                        "The chosen provider may affect loading speed and visual style."));

        infoTheme.setOnClickListener(v -> showInfoDialog(
                "App Theme",
                "Customize the appearance of the app interface:\n\n" +
                        "• Light: Bright interface, great for daylight use.\n" +
                        "• Dark: Battery-saving and eye-friendly in low light.\n" +
                        "• System: Follows your device’s current system theme.\n\n" +
                        "This affects all screens within the app immediately after saving."));

        infoCity.setOnClickListener(v -> showInfoDialog(
                "Widget City",
                "Enter the name of the city you want the home screen widget to show weather for, or press the icon on the right for your current location.\n\n" +
                        "After saving:\n" +
                        "1. Long-press on an empty space on your home screen.\n" +
                        "2. Tap \"Widgets\".\n" +
                        "3. Scroll to find \"WeatherApp\".\n" +
                        "4. Drag the widget to your home screen.\n\n" +
                        "Once placed, it will automatically update to show the weather for the city you entered here.\n\n" +
                        "You can change the city anytime from this settings screen."));



        // Load saved preferences
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences widgetPrefs = requireContext().getSharedPreferences("WIDGET_PREFS", Context.MODE_PRIVATE);
        spinnerUnits.setSelection(getUnitIndex(prefs.getString(UNIT_KEY, "metric")));
        spinnerMaps.setSelection(getMapIndex(prefs.getString(MAP_KEY, "world_weather")));
        spinnerTheme.setSelection(getThemeIndex(prefs.getString(THEME_KEY, "system")));

        cityEditText.setText(widgetPrefs.getString("city", ""));

        // Handle save button click
        btnSave.setOnClickListener(v -> savePreferences());

        return view;
    }

    private void savePreferences() {
        String selectedUnit = getUnitFromIndex(spinnerUnits.getSelectedItemPosition());
        String selectedMap = getMapFromIndex(spinnerMaps.getSelectedItemPosition());
        String selectedTheme = getThemeFromIndex(spinnerTheme.getSelectedItemPosition());
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
        saveCity();
        Toast.makeText(requireContext(),"Settings saved",Toast.LENGTH_SHORT).show();
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

    private void saveCity() {
        String city = cityEditText.getText().toString().trim();
        if (!city.isEmpty()) {
            SharedPreferences widgetPrefs = requireContext().getSharedPreferences("WIDGET_PREFS", Context.MODE_PRIVATE);
            widgetPrefs.edit().putString(CITY_KEY, city).apply();
            updateWidget(requireContext());
        } else {
            Toast.makeText(getContext(), "Please enter a city", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWidget(Context context) {
        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void showInfoDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }



    private void detectCity() {
        UserLocationProvider.getCurrentLocation(requireContext(), new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                fetchCityFromCoordinates(latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Location Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCityFromCoordinates(double lat, double lon) {
        weatherRepository.getCityByCoordinates(lat, lon).enqueue(new Callback<List<ReverseGeocodingResponse>>() {
            @Override
            public void onResponse(Call<List<ReverseGeocodingResponse>> call, Response<List<ReverseGeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ReverseGeocodingResponse data = response.body().get(0);
                    String cityName = data.getName();
                    if (cityName != null) {
                        cityEditText.setText(cityName);
                        Toast.makeText(requireContext(), "Detected city: " + cityName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "City name not found in response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get city from coordinates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReverseGeocodingResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
