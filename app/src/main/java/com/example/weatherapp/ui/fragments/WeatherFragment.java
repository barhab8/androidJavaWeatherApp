package com.example.weatherapp.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.weatherapp.ui.utils.ForecastProcessor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    // UI elements
    private android.widget.EditText etCityName;
    private android.widget.ImageButton btnSearch;
    private android.widget.ImageView ivWeatherIcon;
    private android.widget.TextView tvWeatherLocation, tvWeatherTemperature, tvWeatherDescription;
    private android.widget.TextView tvWeatherHumidity, tvWeatherVisibility, tvWeatherWindSpeed, tvWeatherWindDegree;
    private RecyclerView forecastRecyclerView;
    private ForecastAdapter forecastAdapter;
    private android.widget.LinearLayout rootLayout;
    private ImageView backgroundImage;
    private ForecastProcessor forecastProcessor;
    private android.widget.TextView tvAirQualityIndex, tvMainPollutants;
    BarChart aqiBarChart;





    private FusedLocationProviderClient fusedLocationProviderClient;
    private WeatherRepository weatherRepository;

    // ActivityResultLauncher for location permissions
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, fetch location
                    fetchWeatherByLocation();
                } else {
                    // Permission denied, show a message
                    Toast.makeText(requireContext(), "Location permission is required to fetch weather for your location", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

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
        rootLayout = view.findViewById(R.id.rootLayout);
        backgroundImage = view.findViewById(R.id.backgroundImage);
        tvAirQualityIndex = view.findViewById(R.id.tvAirQualityIndex);
        aqiBarChart = view.findViewById(R.id.aqiBarChart);




        // Initialize RecyclerView for forecast
        forecastRecyclerView = view.findViewById(R.id.forecastRecyclerView);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        forecastAdapter = new ForecastAdapter();
        forecastRecyclerView.setAdapter(forecastAdapter);

        // Initialize dependencies
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        weatherRepository = new WeatherRepository();
        forecastProcessor = new ForecastProcessor();

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchWeatherByLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Handle city-based weather fetch
        btnSearch.setOnClickListener(v -> {
            String city = etCityName.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherByCity(city);
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
                    Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void fetchWeatherByCity(String city) {
        weatherRepository.fetchWeatherByCity(city, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                    fetch5DayForecast(city);
                    fetchAirQualityByCty(city);

                } else {
                    Toast.makeText(requireContext(), "City not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching weather: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeatherByCoordinates(double latitude, double longitude) {
        weatherRepository.fetchWeatherByCoordinates(latitude, longitude, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(requireContext(), "Unable to fetch weather for your location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching weather: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetch5DayForecast(String city) {
        weatherRepository.fetch5DayForecast(city, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> fullForecastList = response.body().getForecastList();

                    // Process the forecast list to get daily forecasts
                    List<ForecastResponse.ForecastItem> dailyForecastList = forecastProcessor.getDailyForecast(fullForecastList);

                    // Pass the processed forecast list to the adapter
                    forecastAdapter.setForecastList(dailyForecastList);
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch forecast", Toast.LENGTH_SHORT).show();
                    Log.e("FORECAST", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetch5DayForecastByCoordinates(double latitude, double longitude) {
        weatherRepository.fetch5DayForecastByCoordinates(latitude, longitude, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> fullForecastList = response.body().getForecastList();

                    // Process the forecast list to get daily forecasts
                    List<ForecastResponse.ForecastItem> dailyForecastList = forecastProcessor.getDailyForecast(fullForecastList);

                    // Pass the processed forecast list to the adapter
                    forecastAdapter.setForecastList(dailyForecastList);
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch forecast", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAirQuality(double latitude, double longitude) {
        weatherRepository.getAirPollutionData(latitude, longitude).enqueue(new Callback<AirPollutionResponse>() {
            @Override
            public void onResponse(Call<AirPollutionResponse> call, Response<AirPollutionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateAirQualityUI(response.body());
                    populateAQIChart(aqiBarChart, response.body().getAirQualityList());
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch air quality data", Toast.LENGTH_SHORT).show();
                    Log.e("AQI", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<AirPollutionResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching air quality data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAirQualityByCty(String cityName) {
        weatherRepository.getCoordinatesByCity(cityName).enqueue(new Callback<List<GeocodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeocodingResponse geocodingResponse = response.body().get(0);
                    double latitude = geocodingResponse.getLatitude();
                    double longitude = geocodingResponse.getLongitude();

                    // Fetch AQI for the coordinates
                    fetchAirQuality(latitude, longitude);
                } else {
                    Toast.makeText(requireContext(), "City not found", Toast.LENGTH_SHORT).show();
                    Log.e("AQI-CITY", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching city coordinates: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateAirQualityUI(AirPollutionResponse airPollutionResponse) {
        if (!airPollutionResponse.getAirQualityList().isEmpty()) {
            AirPollutionResponse.AirQuality airQuality = airPollutionResponse.getAirQualityList().get(0);
            int aqi = airQuality.getMain().getAqi();
            tvAirQualityIndex.setText(String.format("Air Quality Index (AQI): %d", aqi));

        }
    }

    private void populateAQIChart(BarChart chart, List<AirPollutionResponse.AirQuality> airQualityList) {
        // Create a list of BarEntry for the chart
        List<BarEntry> entries = new ArrayList<BarEntry>();
        List<String> labels = new ArrayList<String>();

        for (int i = 0; i < airQualityList.size(); i++) {
            AirPollutionResponse.AirQuality airQuality = airQualityList.get(i);
            entries.add(new BarEntry(i, airQuality.getMain().getAqi()));

            // Convert timestamp to readable date
            String dateLabel = new java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
                    .format(new java.util.Date(airQuality.getTimestamp() * 1000));
            labels.add(dateLabel);
        }

        // Create a dataset and give it a type
        BarDataSet dataSet = new BarDataSet(entries, "AQI Levels");
        dataSet.setColors(getAQIColors(entries)); // Set custom colors for AQI levels
        dataSet.setValueTextColor(android.graphics.Color.WHITE); // Value text color
        dataSet.setValueTextSize(10f);

        // Create the BarData object and set it to the chart
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        // Disable X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false); // Remove the X-axis entirely

        // Configure Y-axis (AQI values)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(android.graphics.Color.WHITE);
        leftAxis.setAxisMinimum(0f); // Minimum AQI
        leftAxis.setAxisMaximum(5f); // AQI ranges from 1 to 5

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        // Configure chart appearance
        chart.getLegend().setTextColor(android.graphics.Color.WHITE);
        chart.getDescription().setEnabled(false); // Disable description text
        chart.invalidate(); // Refresh the chart
    }

    private List<Integer> getAQIColors(List<BarEntry> entries) {
        List<Integer> colors = new ArrayList<>();
        for (BarEntry entry : entries) {
            float aqi = entry.getY();
            if (aqi == 1) {
                colors.add(android.graphics.Color.GREEN); // Good
            } else if (aqi == 2) {
                colors.add(android.graphics.Color.YELLOW); // Moderate
            } else if (aqi == 3) {
                colors.add(android.graphics.Color.parseColor("#FFA500")); // Unhealthy for sensitive groups
            } else if (aqi == 4) {
                colors.add(android.graphics.Color.RED); // Unhealthy
            } else if (aqi == 5) {
                colors.add(android.graphics.Color.MAGENTA); // Very Unhealthy
            } else {
                colors.add(android.graphics.Color.GRAY); // Default
            }
        }
        return colors;
    }




    private void updateUI(WeatherResponse weather) {
        tvWeatherLocation.setText(weather.getCity());
        tvWeatherTemperature.setText(String.format("%s°C", weather.getMain().getTemp()));
        tvWeatherDescription.setText(weather.getWeather()[0].getDescription());
        tvWeatherHumidity.setText(String.format("Humidity: %s%%", weather.getMain().getHumidity()));
        tvWeatherVisibility.setText(String.format("Visibility: %d km", Integer.parseInt(weather.getVisibilit()) / 1000));
        tvWeatherWindSpeed.setText(String.format("Wind Speed: %s m/s", weather.getWind().getSpeed()));
        tvWeatherWindDegree.setText(String.format("Wind Degree: %s°", weather.getWind().getDegree()));

        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getWeather()[0].getIcon() + "@2x.png";
        Picasso.get().load(iconUrl).into(ivWeatherIcon);

        updateBackground(weather.getWeather()[0].getIcon());
    }

    private void updateBackground(String weatherIcon) {
        int backgroundResId;

        switch (weatherIcon) {
            case "01d":
            case "01n":
                backgroundResId = R.drawable.clear_sky_bg;
                break;
            case "02d":
            case "02n":
                backgroundResId = R.drawable.few_clouds_bg;
                break;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                backgroundResId = R.drawable.broken_clouds_bg;
                break;
            case "09d":
            case "09n":
                backgroundResId = R.drawable.shower_rain_bg;
                break;
            case "10d":
            case "10n":
                backgroundResId = R.drawable.rain_bg;
                break;
            case "11d":
            case "11n":
                backgroundResId = R.drawable.thunderstorm_bg;
                break;
            case "13d":
            case "13n":
                backgroundResId = R.drawable.snow_bg;
                break;
            case "50d":
            case "50n":
                backgroundResId = R.drawable.mist_bg;
                break;
            default:
                backgroundResId = R.drawable.clear_sky_bg;
                break;
        }

        rootLayout.setBackgroundResource(backgroundResId);
        rootLayout.setScaleX(1.0f);
        rootLayout.setScaleY(1.0f);
    }

}
