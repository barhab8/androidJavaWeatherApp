package com.example.weatherapp.ui.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "notification";
    private final WeatherRepository weatherRepository = new WeatherRepository();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered! Fetching weather data...");

        double latitude = getSavedLatitude(context);
        double longitude = getSavedLongitude(context);
        String units = "metric"; // Use "imperial" for Fahrenheit

        // Fetch weather data
        weatherRepository.fetchWeatherByCoordinates(latitude, longitude, units)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double temperature = response.body().getMain().getTemp();
                            String message = "Today's temperature: " + temperature + "°C";
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

    private double getSavedLatitude(Context context) {
        return 37.7749f;
    }

    private double getSavedLongitude(Context context) {
        return -122.4194f;
    }
}
