package com.example.weatherapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.fragments.MapFragment;
import com.example.weatherapp.ui.fragments.SocialFragment;
import com.example.weatherapp.ui.fragments.UserDetailsFragment;
import com.example.weatherapp.ui.fragments.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreenActivity extends AppCompatActivity {

    private static final int FAVORITES_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Set up the top app bar (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the default fragment (WeatherFragment)
        loadFragment(new WeatherFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.nav_weather) {
                selectedFragment = new WeatherFragment();
            } else if (item.getItemId() == R.id.nav_user_details) {
                selectedFragment = new SocialFragment();
            } else if (item.getItemId() == R.id.map) {
                selectedFragment = new MapFragment();
            } else {
                selectedFragment = null;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // Inflate the top app bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    // Handle menu item selections from the top app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_screen) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.favorites_screen) {
            // Launch FavoritesActivity for a result instead of starting it normally
            startActivityForResult(new Intent(this, FavoritesActivity.class), FAVORITES_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Capture the result from FavoritesActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FAVORITES_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedCity = data.getStringExtra("selected_city");
            if (selectedCity != null) {
                // Check if the current fragment is a WeatherFragment and update it.
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof WeatherFragment) {
                    ((WeatherFragment) currentFragment).fetchWeatherDataByCity(selectedCity, false);
                } else {
                    // Otherwise, load a new WeatherFragment with the selected city.
                    WeatherFragment weatherFragment = new WeatherFragment();
                    Bundle args = new Bundle();
                    args.putString("selected_city", selectedCity);
                    weatherFragment.setArguments(args);
                    loadFragment(weatherFragment);
                }
            }
        }
    }
}
