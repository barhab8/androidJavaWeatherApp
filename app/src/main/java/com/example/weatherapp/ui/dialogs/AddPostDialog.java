package com.example.weatherapp.ui.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.WeatherResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.example.weatherapp.ui.utils.UserLocationProvider;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

public class AddPostDialog extends Dialog {

    private EditText editTextDialogPost;
    private EditText editTextCityName;
    private TextView textViewWeatherPreview;
    private Button buttonDialogPost;
    private Button buttonUseCurrentLocation;
    private Button buttonFetchWeather;

    private final WeatherRepository weatherRepository;
    private final OnPostAddedListener listener;

    private final String[] locationName = {null};
    private final String[] weatherSummary = {null};

    public interface OnPostAddedListener {
        void onPostAdded();
    }

    public AddPostDialog(@NonNull Context context, OnPostAddedListener listener) {
        super(context);
        weatherRepository = new WeatherRepository(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_post, null);
        setContentView(dialogView);

        // Initialize views
        editTextDialogPost = dialogView.findViewById(R.id.editTextDialogPost);
        editTextCityName = dialogView.findViewById(R.id.editTextCityName);
        textViewWeatherPreview = dialogView.findViewById(R.id.textViewWeatherPreview);
        buttonDialogPost = dialogView.findViewById(R.id.buttonDialogPost);
        buttonUseCurrentLocation = dialogView.findViewById(R.id.buttonUseCurrentLocation);
        buttonFetchWeather = dialogView.findViewById(R.id.buttonFetchWeather);

        // Set click listeners
        buttonUseCurrentLocation.setOnClickListener(v -> fetchCurrentLocationWeather());
        buttonFetchWeather.setOnClickListener(v -> fetchWeatherByCity());
        buttonDialogPost.setOnClickListener(v -> submitPost());

        // Auto-fetch weather on open if location permission is granted
        autoFetchWeatherOnOpen();
    }

    private void autoFetchWeatherOnOpen() {
        UserLocationProvider.getCurrentLocation(getContext(), new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                fetchWeatherByCoordinates(latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                textViewWeatherPreview.setText(errorMessage);
            }
        });
    }

    private void fetchCurrentLocationWeather() {
        UserLocationProvider.getCurrentLocation(getContext(), new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                fetchWeatherByCoordinates(latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                textViewWeatherPreview.setText(errorMessage);
            }
        });
    }


    private void fetchWeatherByCoordinates(double lat, double lon) {
        weatherRepository.fetchWeatherByCoordinates(lat, lon).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processWeatherResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewWeatherPreview.setText("Failed to get weather.");
            }
        });
    }

    private void fetchWeatherByCity() {
        String city = editTextCityName.getText().toString().trim();
        if (city.isEmpty()) {
            editTextCityName.setError("City name required");
            return;
        }

        fetchWeatherByCityName(city);
    }

    private void fetchWeatherByCityName(String city) {
        weatherRepository.fetchWeatherByCity(city).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processWeatherResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewWeatherPreview.setText("Failed to get weather.");
            }
        });
    }

    private void processWeatherResponse(WeatherResponse weather) {
        String city = weather.getCity() != null ? weather.getCity() : "Unknown";
        locationName[0] = city;

        String tempSymbol = getTemperatureUnitSymbol();
        String temp = weather.getMain() != null ? weather.getMain().getTemp() + tempSymbol : "N/A";
        String desc = (weather.getWeather() != null && weather.getWeather().length > 0)
                ? weather.getWeather()[0].getDescription()
                : "N/A";

        weatherSummary[0] = desc + ", " + temp;

        // Update UI
        editTextCityName.setText(locationName[0]);
        textViewWeatherPreview.setText(locationName[0] + ": " + weatherSummary[0]);
    }

    private void submitPost() {
        String postText = editTextDialogPost.getText().toString().trim();
        if (postText.isEmpty()) {
            editTextDialogPost.setError("Cannot post empty");
            return;
        }

        String city = editTextCityName.getText().toString().trim();
        if (city.isEmpty()) {
            editTextCityName.setError("City name required");
            return;
        }

        if (locationName[0] != null && weatherSummary[0] != null && city.equalsIgnoreCase(locationName[0])) {
            finalizePost(postText);
        } else {
            textViewWeatherPreview.setText("Fetching weather for: " + city);

            weatherRepository.fetchWeatherByCity(city).enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        processWeatherResponse(response.body());
                        finalizePost(postText);
                    } else {
                        textViewWeatherPreview.setText("Failed to get weather for: " + city);
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    textViewWeatherPreview.setText("Failed to get weather.");
                }
            });
        }
    }

    private void finalizePost(String postText) {
        FirebaseUtils.submitWeatherPost(
                getContext(),
                postText,
                locationName[0],
                weatherSummary[0],
                () -> {
                    if (listener != null) {
                        listener.onPostAdded();
                    }
                    dismiss();
                },
                () -> {
                    // FirebaseUtils will show error
                }
        );
    }

    private String getTemperatureUnitSymbol() {
        switch (UNIT) {
            case "imperial": return "°F";
            case "standard": return " K";
            default: return "°C";
        }
    }
}