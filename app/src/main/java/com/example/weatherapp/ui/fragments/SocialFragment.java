package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.social.PostModel;
import com.example.weatherapp.data.weather.model.ReverseGeocodingResponse;
import com.example.weatherapp.data.weather.repository.WeatherRepository;
import com.example.weatherapp.ui.adapters.PostsAdapter;
import com.example.weatherapp.ui.dialogs.AddPostDialog;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.example.weatherapp.ui.utils.UserLocationProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

public class SocialFragment extends Fragment implements AddPostDialog.OnPostAddedListener {

    private RecyclerView recyclerViewPosts;
    private PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();
    private String userCity = null;
    private String selectedTempCategory = "All";
    private String selectedLocation = "All";

    private Spinner spinnerLocationFilter, spinnerTempFilter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_social, container, false);

        // RecyclerView setup
        recyclerViewPosts = root.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        // FAB
        FloatingActionButton fabAddPost = root.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v -> showAddPostDialog());

        // Get current location to build filter list
        UserLocationProvider.getCurrentLocation(requireContext(), new UserLocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                WeatherRepository weatherRepository = new WeatherRepository(requireContext());

                weatherRepository.getCityByCoordinates(latitude, longitude).enqueue(new Callback<List<ReverseGeocodingResponse>>() {
                    @Override
                    public void onResponse(Call<List<ReverseGeocodingResponse>> call, Response<List<ReverseGeocodingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ReverseGeocodingResponse geoResponse = response.body().get(0);
                            userCity = geoResponse.getName();
                            setupFilters(root);
                        } else {
                            userCity = null;
                            setupFilters(root); // Fallback with no city
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReverseGeocodingResponse>> call, Throwable t) {
                        userCity = null;
                        setupFilters(root); // Fallback with no city
                    }
                });
            }



            @Override
            public void onError(String errorMessage) {
                setupFilters(root); // fallback
            }
        });

        // Load posts from Firebase
        loadPosts();

        return root;
    }

    private void showAddPostDialog() {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        AddPostDialog dialog = new AddPostDialog(requireContext(), this);
        dialog.show();
    }

    @Override
    public void onPostAdded() {
        loadPosts();
    }

    private void loadPosts() {
        try {
            if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
            FirebaseUtils.loadWeatherPosts(getContext(), posts -> {
                postList.clear();
                postList.addAll(posts);
                applyFilters(); // Apply filters on new data
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFilters(View root) {
        if (!isAdded()) return; // Check if the fragment is still attached in order to use getContext or requireContext
        spinnerLocationFilter = root.findViewById(R.id.spinnerLocationFilter);
        spinnerTempFilter = root.findViewById(R.id.spinnerTempFilter);

        List<String> locationOptions = new ArrayList<>();
        locationOptions.add("All");
        if (userCity != null) locationOptions.add(userCity);


        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locationOptions);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocationFilter.setAdapter(locationAdapter);

        List<String> tempOptions = List.of("All", "Cold", "Fine", "Warm");
        ArrayAdapter<String> tempAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tempOptions);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTempFilter.setAdapter(tempAdapter);

        spinnerLocationFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = parent.getItemAtPosition(position).toString();
                applyFilters();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTempFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTempCategory = parent.getItemAtPosition(position).toString();
                applyFilters();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        List<PostModel> filtered = new ArrayList<>();

        for (PostModel post : postList) {
            boolean matchLocation = selectedLocation.equals("All") ||
                    (selectedLocation.equals(userCity) && userCity != null &&
                            normalizeCityName(userCity).equals(normalizeCityName(post.getLocationName())));


            boolean matchTemp = selectedTempCategory.equals("All") || checkTempMatch(post.getWeather());

            if (matchLocation && matchTemp) {
                filtered.add(post);
            }
        }

        adapter.updateList(filtered);
    }

    private boolean checkTempMatch(String weatherSummary) {
        try {
            String tempStr = weatherSummary.replaceAll("[^0-9.]", "");
            double originalTemp = Double.parseDouble(tempStr);

            // Extract unit: check if °F, °C, or K appears in the string
            String lower = weatherSummary.toLowerCase();
            String unit;
            if (lower.contains("°f")) {
                unit = "imperial";
            } else if (lower.contains("°c")) {
                unit = "metric";
            } else {
                unit = "standard"; // default fallback
            }

            // Convert all units to Celsius for backend filtering
            double tempCelsius;
            switch (unit) {
                case "imperial": // °F → °C
                    tempCelsius = (originalTemp - 32) * 5 / 9;
                    break;
                case "standard": // K → °C
                    tempCelsius = originalTemp - 273.15;
                    break;
                default: // metric
                    tempCelsius = originalTemp;
                    break;
            }

            // Filter logic in Celsius
            boolean match;
            switch (selectedTempCategory) {
                case "Cold":
                    match = tempCelsius < 10;
                    break;
                case "Fine":
                    match = tempCelsius >= 10 && tempCelsius <= 25;
                    break;
                case "Warm":
                    match = tempCelsius > 25;
                    break;
                default:
                    match = true;
            }

            return match;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    private String normalizeCityName(String name) {
        if (name == null) return "";
        return name.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

}
