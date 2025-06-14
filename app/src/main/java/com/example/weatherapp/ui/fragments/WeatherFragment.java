package com.example.weatherapp.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.weatherapp.data.weather.model.AirPollutionResponse;
import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.example.weatherapp.data.weather.model.GeocodingResponse;
import com.example.weatherapp.data.weather.model.WeatherResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.example.weatherapp.ui.adapters.ForecastAdapter;
import com.example.weatherapp.ui.dialogs.WeatherTipDialog;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.example.weatherapp.ui.utils.UIHelper;
import com.example.weatherapp.ui.utils.UserLocationProvider;
import com.github.mikephil.charting.charts.LineChart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    // Constants
    public static String UNIT = "metric";
    public static final String ICON_URL_TEMPLATE = "https://openweathermap.org/img/wn/%s@2x.png";

    // UI Elements
    private android.widget.EditText etCityName;
    private android.widget.ImageButton btnSearch;
    private android.widget.ImageButton btnFavorite;
    private android.widget.ImageView ivWeatherIcon;
    private android.widget.TextView tvWeatherLocation, tvWeatherTemperature, tvWeatherDescription;
    private android.widget.TextView tvWeatherHumidity, tvWeatherVisibility, tvWeatherWindSpeed, tvWeatherWindDegree;
    private android.widget.TextView tvAirQualityIndex;
    private RecyclerView forecastRecyclerView;
    private ForecastAdapter forecastAdapter;
    private android.widget.LinearLayout rootLayout;
    private LineChart forecastChart;
    private android.widget.ImageButton btnAskAI;


    // Settings variables
    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    // Data & Utilities
    private WeatherRepository weatherRepository;
    private List<ForecastResponse.ForecastItem> currentForecastList = new ArrayList<>();

    // boolean for weather the star is filled or not - city favorite or not.
    private boolean isCityFavorite = false;


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

        // city-search button.
        btnSearch.setOnClickListener(v -> {
            String city = etCityName.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherDataByCity(city, true);
            } else {
                showToast("Please enter a city name.");
            }
        });
        // favorite button
        btnFavorite.setOnClickListener(v -> {
            if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
            String city = tvWeatherLocation.getText().toString().trim();
            if (!city.isEmpty()) {
                if (isCityFavorite) {
                    FirebaseUtils.removeCityFromFavorites(getContext(), city);
                    btnFavorite.setImageResource(R.drawable.ic_star_outline);
                    isCityFavorite = false;
                } else {
                    FirebaseUtils.saveCityToFavorites(getContext(), city);
                    btnFavorite.setImageResource(R.drawable.ic_star_filled);
                    isCityFavorite = true;
                }
            } else {
                showToast("No city found");
            }
        });

        btnAskAI.setOnClickListener(v -> {
            if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
            WeatherTipDialog askAiDialog = new WeatherTipDialog(requireContext(), tvWeatherLocation.getText().toString(),tvWeatherTemperature.getText().toString(), tvWeatherWindSpeed.getText().toString(), tvWeatherHumidity.getText().toString(), tvWeatherVisibility.getText().toString(), tvWeatherDescription.getText().toString(), tvAirQualityIndex.getText().toString(), currentForecastList);
            askAiDialog.show();
        });


        return view;
    }

    private void initializeUI(View view) {
        etCityName = view.findViewById(R.id.etCityName);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnFavorite = view.findViewById(R.id.btnFavorite);
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
        btnAskAI = view.findViewById(R.id.btnAskAI);


        // charts
        forecastChart = view.findViewById(R.id.forecastChart);

        // RecyclerView setup for forecast
        forecastRecyclerView = view.findViewById(R.id.forecastRecyclerView);
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        forecastAdapter = new ForecastAdapter(getContext());
        forecastRecyclerView.setAdapter(forecastAdapter);
    }

    private void initializeDependencies() {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        weatherRepository = new WeatherRepository(getContext());
    }

    @Override
    public void onResume() {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        super.onResume();
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String newUnit = prefs.getString(UNIT_KEY, "metric");

        if (!newUnit.equals(UNIT)) { // If unit changed, update and refresh data
            UNIT = newUnit;
            weatherRepository.setUnit(requireContext(), newUnit); // Update the repository
            refreshWeatherData(); // Fetch data again with new unit
        }
    }

    // Method to refresh weather data based on the last searched city or location
    private void refreshWeatherData() {
        String lastCity = tvWeatherLocation.getText().toString().trim();

        if (!lastCity.isEmpty()) {
            fetchWeatherDataByCity(lastCity, false);
        } else {
            fetchWeatherByLocation(); // If no city is set, fetch by location
        }
    }

    private void fetchWeatherByLocation() {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        UserLocationProvider.getCurrentLocation(requireContext(), new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                fetchWeatherDataByCoordinates(latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                showToast(errorMessage);
            }
        });
    }


    //City-based API calls
    public void fetchWeatherDataByCity(String city, boolean toast) {
        // Fetch weather data by city
        weatherRepository.fetchWeatherByCity(city).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWeatherUI(response.body());
                } else {
                    if(toast) {
                        showToast("City not found.");
                    }
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showToast("Error fetching weather: " + t.getMessage());
            }
        });
        // Fetch 5-day forecast by city
        weatherRepository.fetch5DayForecast(city).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> forecastList = response.body().getForecastList();
                    Log.d("Forecast", UIHelper.getDailyForecast(forecastList).toString());
                    forecastAdapter.setForecastList(UIHelper.getDailyForecast(forecastList));
                    currentForecastList = UIHelper.getDailyForecast(forecastList);
                    UIHelper.populateForecastChart(forecastChart, forecastList);
                } else {
                    if(toast) {
                        showToast("Failed to fetch forecast.");
                    }

                }
            }
            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                showToast("Error fetching forecast: " + t.getMessage());
            }
        });
        // Fetch air quality by first getting coordinates for the city
        weatherRepository.getCoordinatesByCity(city).enqueue(new Callback<List<GeocodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeocodingResponse>> call, Response<List<GeocodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeocodingResponse geocodingResponse = response.body().get(0);
                    double latitude = geocodingResponse.getLatitude();
                    double longitude = geocodingResponse.getLongitude();
                    weatherRepository.getAirPollutionData(latitude, longitude).enqueue(new Callback<AirPollutionResponse>() {
                        @Override
                        public void onResponse(Call<AirPollutionResponse> call, Response<AirPollutionResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UIHelper.updateAirQualityUI(response.body(), tvAirQualityIndex);
                            } else {
                                if(toast) {
                                    showToast("Failed to fetch air quality data.");
                                }

                            }
                        }
                        @Override
                        public void onFailure(Call<AirPollutionResponse> call, Throwable t) {
                            showToast("Error fetching air quality data: " + t.getMessage());
                        }
                    });
                } else {
                    if(toast) {
                        showToast("City not found.");
                    }

                }
            }
            @Override
            public void onFailure(Call<List<GeocodingResponse>> call, Throwable t) {
                showToast("Error fetching city coordinates: " + t.getMessage());
            }
        });
    }

    //Latitude/Longitude-based API calls
    private void fetchWeatherDataByCoordinates(double latitude, double longitude) {
        // Fetch weather data by coordinates
        weatherRepository.fetchWeatherByCoordinates(latitude, longitude).enqueue(new Callback<WeatherResponse>() {
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
        // Fetch 5-day forecast by coordinates
        weatherRepository.fetch5DayForecastByCoordinates(latitude, longitude, UNIT).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> forecastList = response.body().getForecastList();
                    forecastAdapter.setForecastList(UIHelper.getDailyForecast(forecastList));
                    currentForecastList = UIHelper.getDailyForecast(forecastList);
                    UIHelper.populateForecastChart(forecastChart, forecastList);
                } else {
                    showToast("Failed to fetch forecast.");
                }
            }
            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                showToast("Error fetching forecast: " + t.getMessage());
            }
        });
        // Fetch air quality by coordinates
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

    private void updateWeatherUI(WeatherResponse weather) {
        tvWeatherLocation.setText(weather.getCity());
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        checkIfCityIsFavorite(getContext(), weather.getCity());
        String tempUnitSymbol;
        String windSpeedUnit;
        double windSpeed = weather.getWind().getSpeed();

        switch (UNIT) {
            case "imperial":
                tempUnitSymbol = "°F";
                windSpeedUnit = " mph";
                break;
            case "standard":
                tempUnitSymbol = " K";
                windSpeedUnit = " m/s";
                break;
            default:
                tempUnitSymbol = "°C";
                windSpeedUnit = " m/s";
                break;
        }

        tvWeatherLocation.setText(weather.getCity());
        tvWeatherTemperature.setText(String.format("%s%s", weather.getMain().getTemp(), tempUnitSymbol));
        tvWeatherDescription.setText(weather.getWeather()[0].getDescription());
        tvWeatherHumidity.setText(String.format("Humidity: %s%%", weather.getMain().getHumidity()));
        tvWeatherVisibility.setText(String.format("Visibility: %d km", Integer.parseInt(weather.getVisibility()) / 1000));
        tvWeatherWindSpeed.setText(String.format("Wind Speed: %.1f%s", windSpeed, windSpeedUnit));
        tvWeatherWindDegree.setText(String.format("Wind Degree: %s°", weather.getWind().getDegree()));

        // Load weather icon
        String iconUrl = String.format(ICON_URL_TEMPLATE, weather.getWeather()[0].getIcon());
        Picasso.get().load(iconUrl).into(ivWeatherIcon);

        // Update background
        UIHelper.updateBackground(rootLayout, weather.getWeather()[0].getIcon());
    }

    private void showToast(String message) {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void checkIfCityIsFavorite(Context context, String city) {
        FirebaseUtils.loadFavoriteCities(context, favoriteCities -> {
            isCityFavorite = favoriteCities.contains(city);
            btnFavorite.setImageResource(isCityFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        });
    }


}
