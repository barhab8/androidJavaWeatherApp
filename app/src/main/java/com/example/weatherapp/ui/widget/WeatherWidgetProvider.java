package com.example.weatherapp.ui.widget;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;
import com.example.weatherapp.ui.activitys.MainScreenActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_UPDATE_WIDGET = "com.example.weatherapp.ACTION_UPDATE_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            getUserLocationAndUpdateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, WeatherWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

            for (int appWidgetId : appWidgetIds) {
                getUserLocationAndUpdateWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    private void getUserLocationAndUpdateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    updateWidget(context, appWidgetManager, appWidgetId, latitude, longitude);
                } else {
                    updateWidgetWithError(context, appWidgetManager, appWidgetId, "Location Error");
                }
            }).addOnFailureListener(e -> updateWidgetWithError(context, appWidgetManager, appWidgetId, "Location Unavailable"));
        } else {
            updateWidgetWithError(context, appWidgetManager, appWidgetId, "Permission Needed");
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, double latitude, double longitude) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setTextViewText(R.id.widget_city, "Fetching...");
        views.setTextViewText(R.id.widget_temperature, "--");
        appWidgetManager.updateAppWidget(appWidgetId, views);

        WeatherRepository weatherRepo = new WeatherRepository(context);
        weatherRepo.fetchWeatherByCoordinates(latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String tempUnitSymbol;
                    switch (UNIT) {
                        case "imperial":
                            tempUnitSymbol = "°F";
                            break;
                        case "standard":
                            tempUnitSymbol = " K";
                            break;
                        default:
                            tempUnitSymbol = "°C";
                            break;
                    }
                    String temperature = weather.getMain().getTemp() + tempUnitSymbol;

                    views.setTextViewText(R.id.widget_city, weather.getCity());
                    views.setTextViewText(R.id.widget_temperature, temperature);
                } else {
                    updateWidgetWithError(context, appWidgetManager, appWidgetId, "Weather Error");
                }
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                updateWidgetWithError(context, appWidgetManager, appWidgetId, "No Data");
            }
        });
    }

    private void updateWidgetWithError(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String errorMessage) {
        Intent intent = new Intent(context, MainScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setOnClickPendingIntent(R.id.widget_weather, pendingIntent);
        views.setTextViewText(R.id.widget_city, errorMessage);
        views.setTextViewText(R.id.widget_temperature, "--");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
