package com.example.weatherapp.ui.utils.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.weatherapp.data.weather.model.WeatherResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "notification";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered! Getting user's location...");

        // Check for location permission before fetching location
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fetch current location
            fetchLocationAndNotify(context);
        } else {
            // If permission is not granted, use saved coordinates or default location
            Log.e(TAG, "Location permission not granted. Using saved location.");
            fetchWeatherForSavedLocation(context);
        }
    }

    private void fetchLocationAndNotify(Context context) {
        // Double-check permission before accessing location
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission is not granted. Using saved location.");
            fetchWeatherForSavedLocation(context);
            return; // Stop execution if permission is missing
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Task<android.location.Location> locationTask = fusedLocationClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d(TAG, "User's location: Lat=" + latitude + ", Lon=" + longitude);
                fetchWeatherAndSendNotification(context, latitude, longitude);
            } else {
                Log.e(TAG, "Location is null. Using saved location.");
                fetchWeatherForSavedLocation(context);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get location: " + e.getMessage());
            fetchWeatherForSavedLocation(context);
        });
    }


    private void fetchWeatherForSavedLocation(Context context) {
        fetchWeatherAndSendNotification(context, 37.7749, 122.4194); // default to san fransisco
    }

    private void fetchWeatherAndSendNotification(Context context, double latitude, double longitude) {
        WeatherRepository weatherRepository = new WeatherRepository(context);
        String unit = getUnitPreference(context);

        weatherRepository.fetchWeatherByCoordinates(latitude, longitude)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double temperature = response.body().getMain().getTemp();
                            String unitSymbol = getUnitSymbol(unit);
                            String city = response.body().getCity();
                            String message = "Today's temperature: " + temperature + unitSymbol + " in " + city;
                            NotificationHelper.showWeatherNotification(context, message);
                        } else {
                            Log.e(TAG, "Failed to fetch weather data.");
                            NotificationHelper.showWeatherNotification(context, "Check the weather today!");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed: " + t.getMessage());
                        NotificationHelper.showWeatherNotification(context, "Check the weather today!");
                    }
                });

        // Reschedule the next day's notification
        NotificationScheduler.rescheduleNextDay(context);
    }
    private String getUnitPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(UNIT_KEY, "metric"); // Default to metric
    }

    private String getUnitSymbol(String unit) {
        switch (unit) {
            case "imperial":
                return "°F"; // Fahrenheit
            case "standard":
                return " K"; // Kelvin
            default:
                return "°C"; // Celsius (metric)
        }
    }
}
