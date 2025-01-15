package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.weatherapp.ui.fragments.MapFragment;
import com.example.weatherapp.ui.fragments.UserDetailsFragment;
import com.example.weatherapp.ui.fragments.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        loadFragment(new WeatherFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.nav_weather) {
                selectedFragment = new WeatherFragment();
            } else if (item.getItemId() == R.id.nav_user_details) {
                selectedFragment = new UserDetailsFragment();
            }
            else if (item.getItemId() == R.id.map) {
                selectedFragment = new MapFragment();
            }
            else {
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
}
