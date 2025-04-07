package com.example.weatherapp.ui.fragments;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFragment extends Fragment {

    private EditText editTextPost;
    private Button buttonPost;
    private RecyclerView recyclerViewPosts;
    private PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherRepository weatherRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_social, container, false);

        editTextPost = root.findViewById(R.id.editTextPost);
        buttonPost = root.findViewById(R.id.buttonPost);
        recyclerViewPosts = root.findViewById(R.id.recyclerViewPosts);

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        weatherRepository = new WeatherRepository(requireContext());

        buttonPost.setOnClickListener(v -> {
            String text = editTextPost.getText().toString().trim();
            if (!text.isEmpty()) {
                fetchLocationAndWeather(text);
            }
        });

        loadPosts();

        return root;
    }

    private void fetchLocationAndWeather(String userPost) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission outside fragment (e.g., in activity)
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        // Fetch weather info
                        weatherRepository.fetchWeatherByCoordinates(lat, lon).enqueue(new Callback<WeatherResponse>() {
                            @Override
                            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
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
                                    WeatherResponse weather = response.body();
                                    String locationName = weather.getCity();
                                    String weatherInfo = weather.getWeather()[0].getDescription();
                                    String temprature = String.format("%s%s", weather.getMain().getTemp(), tempUnitSymbol);

                                    FirebaseUtils.submitWeatherPost(
                                            getContext(),
                                            userPost,
                                            locationName,
                                            weatherInfo + ", " + temprature,
                                            () -> {
                                                editTextPost.setText("");
                                                loadPosts();
                                            },
                                            () -> {
                                                // FirebaseUtils already shows error
                                            }
                                    );
                                }
                            }

                            @Override
                            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
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
}
