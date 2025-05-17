package com.example.weatherapp.ui.widget;

import static com.example.weatherapp.ui.fragments.WeatherFragment.ICON_URL_TEMPLATE;
import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.GeocodingResponse;
import com.example.weatherapp.data.weather.model.WeatherResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.example.weatherapp.ui.activitys.MainScreenActivity;
import com.example.weatherapp.ui.activitys.SplashScreenActivity;
import com.example.weatherapp.ui.utils.UserLocationProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_UPDATE_WIDGET = "com.example.weatherapp.ACTION_UPDATE_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            setWidgetClickListener(context, appWidgetManager, appWidgetId);
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
        SharedPreferences prefs = context.getSharedPreferences("WIDGET_PREFS", Context.MODE_PRIVATE);
        String city = prefs.getString("city", "");
        if (!city.isEmpty()) {
            fetchWeatherByCity(context, appWidgetManager, appWidgetId, city);
        } else {
            getLocationAndFetchWeather(context, appWidgetManager, appWidgetId);
        }
    }

    private void getLocationAndFetchWeather(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        UserLocationProvider.getCurrentLocation(context, new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                updateWidget(context, appWidgetManager, appWidgetId, latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WeatherWidget", "Location error: " + errorMessage);
                updateWidgetWithError(context, appWidgetManager, appWidgetId, errorMessage);
            }
        });
    }


    private void fetchWeatherByCity(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String city) {
        WeatherRepository weatherRepo = new WeatherRepository(context);

        weatherRepo.getCoordinatesByCity(city).enqueue(new Callback<List<GeocodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeocodingResponse geoData = response.body().get(0); // Take the first result
                    double latitude = geoData.getLatitude();
                    double longitude = geoData.getLongitude();

                    // Now fetch weather using coordinates
                    updateWidget(context, appWidgetManager, appWidgetId, latitude, longitude);
                } else {
                    updateWidgetWithError(context, appWidgetManager, appWidgetId, "Location Not Found");
                }
            }

            @Override
            public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                updateWidgetWithError(context, appWidgetManager, appWidgetId, "API Error");
            }
        });
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
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                    String iconUrl = String.format(ICON_URL_TEMPLATE, weather.getWeather()[0].getIcon());

                     new Thread(() -> {
                        try {
                            Bitmap bitmap = Picasso.get().load(iconUrl).get();
                            if (bitmap != null) {
                                Log.d("WidgetScreen", "Bitmap loaded successfully.");
                                views.setImageViewBitmap(R.id.widget_icon, bitmap);
                            } else {
                                Log.e("WidgetScreen", "Bitmap is null");
                            }
                        } catch (Exception e) {
                            Log.e("WidgetScreen", "Error loading image: " + e.getMessage());
                        }
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }).start();
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
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setTextViewText(R.id.widget_city, errorMessage);
        views.setTextViewText(R.id.widget_temperature, "--");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setWidgetClickListener(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        views.setOnClickPendingIntent(R.id.widget_weather, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
