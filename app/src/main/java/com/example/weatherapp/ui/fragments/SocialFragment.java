package com.example.weatherapp.ui.fragments;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.model.PostModel;
import com.example.weatherapp.data.model.WeatherResponse;
import com.example.weatherapp.data.repository.WeatherRepository;
import com.example.weatherapp.ui.adapters.PostsAdapter;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherRepository weatherRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_social, container, false);

        recyclerViewPosts = root.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        weatherRepository = new WeatherRepository(requireContext());

        FloatingActionButton fabAddPost = root.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v -> showAddPostDialog());

        loadPosts();

        return root;
    }
    private void loadPosts() {
        try {
            FirebaseUtils.loadWeatherPosts(getContext(), posts -> {
                postList.clear();
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddPostDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_post, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        EditText editTextDialogPost = dialogView.findViewById(R.id.editTextDialogPost);
        EditText editTextCityName = dialogView.findViewById(R.id.editTextCityName);
        TextView textViewWeatherPreview = dialogView.findViewById(R.id.textViewWeatherPreview);
        Button buttonDialogPost = dialogView.findViewById(R.id.buttonDialogPost);
        Button buttonUseCurrentLocation = dialogView.findViewById(R.id.buttonUseCurrentLocation);
        Button buttonFetchWeather = dialogView.findViewById(R.id.buttonFetchWeather);

        final String[] locationName = {null};
        final String[] weatherSummary = {null};

        // 🔥 Auto-fetch on open
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    weatherRepository.fetchWeatherByCoordinates(lat, lon).enqueue(new Callback<WeatherResponse>() {
                        @Override
                        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                WeatherResponse weather = response.body();
                                locationName[0] = weather.getCity() != null ? weather.getCity() : "Unknown";

                                String tempSymbol = getTemperatureUnitSymbol();
                                String temp = weather.getMain() != null ? weather.getMain().getTemp() + tempSymbol : "N/A";
                                String desc = (weather.getWeather() != null && weather.getWeather().length > 0)
                                        ? weather.getWeather()[0].getDescription()
                                        : "N/A";

                                weatherSummary[0] = desc + ", " + temp;

                                // 🔥 Auto-fill UI
                                editTextCityName.setText(locationName[0]);
                                textViewWeatherPreview.setText(locationName[0] + ": " + weatherSummary[0]);
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherResponse> call, Throwable t) {
                            textViewWeatherPreview.setText("Failed to get weather.");
                        }
                    });
                }
            });
        } else {
            textViewWeatherPreview.setText("Location permission not granted.");
        }

        buttonUseCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                textViewWeatherPreview.setText("Location permission not granted.");
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    weatherRepository.fetchWeatherByCoordinates(lat, lon).enqueue(new Callback<WeatherResponse>() {
                        @Override
                        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                WeatherResponse weather = response.body();
                                locationName[0] = weather.getCity() != null ? weather.getCity() : "Unknown";

                                String tempSymbol = getTemperatureUnitSymbol();
                                String temp = weather.getMain() != null ? weather.getMain().getTemp() + tempSymbol : "N/A";
                                String desc = (weather.getWeather() != null && weather.getWeather().length > 0)
                                        ? weather.getWeather()[0].getDescription()
                                        : "N/A";

                                weatherSummary[0] = desc + ", " + temp;
                                editTextCityName.setText(locationName[0]);
                                textViewWeatherPreview.setText(locationName[0] + ": " + weatherSummary[0]);
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherResponse> call, Throwable t) {
                            textViewWeatherPreview.setText("Failed to get weather.");
                        }
                    });
                }
            });
        });

        buttonFetchWeather.setOnClickListener(v -> {
            String city = editTextCityName.getText().toString().trim();
            if (city.isEmpty()) {
                editTextCityName.setError("City name required");
                return;
            }

            weatherRepository.fetchWeatherByCity(city).enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse weather = response.body();
                        locationName[0] = weather.getCity() != null ? weather.getCity() : city;

                        String tempSymbol = getTemperatureUnitSymbol();
                        String temp = weather.getMain() != null ? weather.getMain().getTemp() + tempSymbol : "N/A";
                        String desc = (weather.getWeather() != null && weather.getWeather().length > 0)
                                ? weather.getWeather()[0].getDescription()
                                : "N/A";

                        weatherSummary[0] = desc + ", " + temp;
                        textViewWeatherPreview.setText(locationName[0] + ": " + weatherSummary[0]);
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    textViewWeatherPreview.setText("Failed to get weather.");
                }
            });
        });

        buttonDialogPost.setOnClickListener(v -> {
            String postText = editTextDialogPost.getText().toString().trim();
            if (postText.isEmpty()) {
                editTextDialogPost.setError("Cannot post empty");
                return;
            }

            if (locationName[0] == null || weatherSummary[0] == null) {
                textViewWeatherPreview.setText("Please fetch weather first");
                return;
            }

            FirebaseUtils.submitWeatherPost(
                    getContext(),
                    postText,
                    locationName[0],
                    weatherSummary[0],
                    () -> {
                        loadPosts();
                        dialog.dismiss();
                    },
                    () -> {
                        // FirebaseUtils will show error
                    }
            );
        });

        dialog.show();
    }


    private String getTemperatureUnitSymbol() {
        switch (UNIT) {
            case "imperial": return "°F";
            case "standard": return " K";
            default: return "°C";
        }
    }


}
