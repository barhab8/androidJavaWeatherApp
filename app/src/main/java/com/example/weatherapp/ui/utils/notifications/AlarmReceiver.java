package com.example.weatherapp.ui.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "notification";
    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    @Override
    public void onReceive(Context context, Intent intent) {
        final WeatherRepository weatherRepository = new WeatherRepository(context);
        Log.d(TAG, "Alarm triggered! Fetching weather data...");

        double latitude = getSavedLatitude();
        double longitude = getSavedLongitude();

        // Get unit preference
        String unit = getUnitPreference(context);

        // Fetch weather data
        weatherRepository.fetchWeatherByCoordinates(latitude, longitude)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double temperature = response.body().getMain().getTemp();
                            String unitSymbol = getUnitSymbol(unit);
                            String message = "Today's temperature: " + temperature + unitSymbol;
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

    private double getSavedLatitude() {
        return 37.7749f;
    }

    private double getSavedLongitude() {
        return -122.4194f;
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
