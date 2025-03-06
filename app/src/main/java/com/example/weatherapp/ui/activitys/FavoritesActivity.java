package com.example.weatherapp.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorites Screen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvFavorites = findViewById(R.id.rvFavorites);
        progressBar = findViewById(R.id.progressBar);
        firestoreHelper = new FirestoreHelper(this);
        weatherRepository = new WeatherRepository(getBaseContext());

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        weatherRepository.fetchWeatherByCity(city).enqueue(new Callback<WeatherResponse>() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_name_asc) {
            favoritesAdapter.sortByName(true);
            return true;
        } else if (id == R.id.sort_name_desc) {
            favoritesAdapter.sortByName(false);
            return true;
        } else if (id == R.id.sort_temp_asc) {
            favoritesAdapter.sortByTemperature(true);
            return true;
        } else if (id == R.id.sort_temp_desc) {
            favoritesAdapter.sortByTemperature(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
