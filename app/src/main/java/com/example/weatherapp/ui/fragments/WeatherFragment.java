package com.example.weatherapp.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.model.AirPollutionResponse;
import com.example.weatherapp.data.model.ForecastResponse;
import com.example.weatherapp.data.model.GeocodingResponse;
import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;
import com.example.weatherapp.ui.adapters.ForecastAdapter;
import com.example.weatherapp.ui.utils.ChartHelper;
import com.example.weatherapp.ui.utils.UIHelper;
import com.example.weatherapp.ui.utils.ForecastProcessor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    // Constants
    private static final String TAG = "WeatherFragment";
    private static final String UNIT = "metric";
    private static final String ICON_URL_TEMPLATE = "https://openweathermap.org/img/wn/%s@2x.png";

    // UI Elements
    private android.widget.EditText etCityName;
    private android.widget.ImageButton btnSearch;
    private android.widget.ImageView ivWeatherIcon;
    private android.widget.TextView tvWeatherLocation, tvWeatherTemperature, tvWeatherDescription;
    private android.widget.TextView tvWeatherHumidity, tvWeatherVisibility, tvWeatherWindSpeed, tvWeatherWindDegree;
    private android.widget.TextView tvAirQualityIndex;
    private RecyclerView forecastRecyclerView;
    private ForecastAdapter forecastAdapter;
    private android.widget.LinearLayout rootLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private BarChart aqiBarChart;
    private LineChart forecastChart;



    // Data & Utilities
    private WeatherRepository weatherRepository;
    private ForecastProcessor forecastProcessor;

    // Location Permission Launcher
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchWeatherByLocation();
                } else {
                    showToast("Location permission is required to fetch weather for your location.");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        initializeUI(view);
        initializeDependencies();

        // Handle location-based weather fetching
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchWeatherByLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Handle city-based weather fetching
        btnSearch.setOnClickListener(v -> {
            String city = etCityName.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherByCity(city);
            } else {
                showToast("Please enter a city name.");
            }
        });

        return view;
    }

    private void initializeUI(View view) {
        // Initialize UI elements
        etCityName = view.findViewById(R.id.etCityName);
        btnSearch = view.findViewById(R.id.btnSearch);
        ivWeatherIcon = view.findViewById(R.id.ivWeatherIcon);
        tvWeatherLocation = view.findViewById(R.id.tvWeatherLocation);
        tvWeatherTemperature = view.findViewById(R.id.tvWeatherTemperature);
        tvWeatherDescription = view.findViewById(R.id.tvWeatherDescription);
        tvWeatherHumidity = view.findViewById(R.id.tvWeatherHumidity);
        tvWeatherVisibility = view.findViewById(R.id.tvWeatherVisibility);
        tvWeatherWindSpeed = view.findViewById(R.id.tvWeatherWindSpeed);
        tvWeatherWindDegree = view.findViewById(R.id.tvWeatherWindDegree);
        tvAirQualityIndex = view.findViewById(R.id.tvAirQualityIndex);
        rootLayout = view.findViewById(R.id.rootLayout);

        // charts
        forecastChart = view.findViewById(R.id.forecastChart);


        // RecyclerView setup for forecast
        forecastRecyclerView = view.findViewById(R.id.forecastRecyclerView);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        forecastAdapter = new ForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);
    }

    private void initializeDependencies() {
        // Initialize dependencies
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        weatherRepository = new WeatherRepository();
        forecastProcessor = new ForecastProcessor();
    }

    private void fetchWeatherByLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    fetchWeatherByCoordinates(latitude, longitude);
                    fetch5DayForecastByCoordinates(latitude, longitude);
                    fetchAirQuality(latitude, longitude);
                } else {
                    showToast("Unable to fetch location.");
                }
            }).addOnFailureListener(e -> showToast("Error fetching location: " + e.getMessage()));
        }
    }

    private void fetchWeatherByCity(String city) {
        weatherRepository.fetchWeatherByCity(city, UNIT).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWeatherUI(response.body());
                    fetch5DayForecast(city);
                    fetchAirQualityByCity(city);
                } else {
                    showToast("City not found.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showToast("Error fetching weather: " + t.getMessage());
            }
        });
    }

    private void fetchWeatherByCoordinates(double latitude, double longitude) {
        weatherRepository.fetchWeatherByCoordinates(latitude, longitude, UNIT).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWeatherUI(response.body());
                } else {
                    showToast("Unable to fetch weather for your location.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showToast("Error fetching weather: " + t.getMessage());
            }
        });
    }

    private void fetch5DayForecast(String city) {
        weatherRepository.fetch5DayForecast(city, UNIT).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> forecastList = response.body().getForecastList();
                    forecastAdapter.setForecastList(forecastList);
                    ChartHelper.populateForecastChart(forecastChart, forecastList);


                } else {
                    showToast("Failed to fetch forecast.");
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                showToast("Error fetching forecast: " + t.getMessage());
            }
        });
    }

    private void fetch5DayForecastByCoordinates(double latitude, double longitude) {
        weatherRepository.fetch5DayForecastByCoordinates(latitude, longitude, UNIT).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Use raw data for chart and daily forecast for RecyclerView
                    List<ForecastResponse.ForecastItem> forecastList = response.body().getForecastList();
                    forecastAdapter.setForecastList(forecastProcessor.getDailyForecast(forecastList));
                    ChartHelper.populateForecastChart(forecastChart, forecastList);

                } else {
                    showToast("Failed to fetch forecast.");
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                showToast("Error fetching forecast: " + t.getMessage());
            }
        });
    }


    private void fetchAirQuality(double latitude, double longitude) {
        weatherRepository.getAirPollutionData(latitude, longitude).enqueue(new Callback<AirPollutionResponse>() {
            @Override
            public void onResponse(Call<AirPollutionResponse> call, Response<AirPollutionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UIHelper.updateAirQualityUI(response.body(), tvAirQualityIndex);
                } else {
                    showToast("Failed to fetch air quality data.");
                }
            }
            @Override
            public void onFailure(Call<AirPollutionResponse> call, Throwable t) {
                showToast("Error fetching air quality data: " + t.getMessage());
            }
        });
    }



    private void fetchAirQualityByCity(String city) {
        weatherRepository.getCoordinatesByCity(city).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeocodingResponse geocodingResponse = response.body().get(0);
                    double latitude = geocodingResponse.getLatitude();
                    double longitude = geocodingResponse.getLongitude();
                    fetchAirQuality(latitude, longitude);
                } else {
                    showToast("City not found.");
                }
            }

            @Override
            public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                showToast("Error fetching city coordinates: " + t.getMessage());
            }
        });
    }


    private void updateWeatherUI(WeatherResponse weather) {
        tvWeatherLocation.setText(weather.getCity());
        tvWeatherTemperature.setText(String.format("%s°C", weather.getMain().getTemp()));
        tvWeatherDescription.setText(weather.getWeather()[0].getDescription());
        tvWeatherHumidity.setText(String.format("Humidity: %s%%", weather.getMain().getHumidity()));
        tvWeatherVisibility.setText(String.format("Visibility: %d km", Integer.parseInt(weather.getVisibilit()) / 1000));
        tvWeatherWindSpeed.setText(String.format("Wind Speed: %s m/s", weather.getWind().getSpeed()));
        tvWeatherWindDegree.setText(String.format("Wind Degree: %s°", weather.getWind().getDegree()));

        // Load weather icon
        String iconUrl = String.format(ICON_URL_TEMPLATE, weather.getWeather()[0].getIcon());
        Picasso.get().load(iconUrl).into(ivWeatherIcon);

        // Update background
        UIHelper.updateBackground(rootLayout, weather.getWeather()[0].getIcon());
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
