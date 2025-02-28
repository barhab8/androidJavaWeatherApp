package com.example.weatherapp.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;
import com.example.weatherapp.ui.adapters.FavoritesAdapter;
import com.example.weatherapp.ui.utils.FirestoreHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private FavoritesAdapter favoritesAdapter;
    private ProgressBar progressBar;
    private FirestoreHelper firestoreHelper;
    private WeatherRepository weatherRepository;
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = findViewById(R.id.rvFavorites);
        progressBar = findViewById(R.id.progressBar);
        firestoreHelper = new FirestoreHelper(this);
        weatherRepository = new WeatherRepository();

        // Load unit preference from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        unit = prefs.getString("unit", "metric");

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new FavoritesAdapter(this, city -> {
            Intent intent = new Intent();
            intent.putExtra("selected_city", city);
            setResult(RESULT_OK, intent);
            finish();
        });

        rvFavorites.setAdapter(favoritesAdapter);
        loadFavorites();
    }

    private void loadFavorites() {
        progressBar.setVisibility(View.VISIBLE);
        firestoreHelper.loadFavoriteCities(favoriteCities -> {
            progressBar.setVisibility(View.GONE);
            if (favoriteCities.isEmpty()) {
                Toast.makeText(this, "No favorite cities yet!", Toast.LENGTH_SHORT).show();
            }
            favoritesAdapter.setCities(favoriteCities);
            for (String city : favoriteCities) {
                fetchTemperature(city);
            }
        });
    }

    private void fetchTemperature(String city) {
        weatherRepository.fetchWeatherByCity(city, unit).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String temp = String.valueOf(weather.getMain().getTemp());
                    favoritesAdapter.updateCityTemperature(city, temp);
                    String iconCode = weather.getWeather()[0].getIcon();
                    String iconUrl = String.format("https://openweathermap.org/img/wn/%s@2x.png", iconCode);
                    favoritesAdapter.updateCityIcon(city, iconUrl);
                }
            }
            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                favoritesAdapter.updateCityTemperature(city, "N/A");
            }
        });
    }
}
