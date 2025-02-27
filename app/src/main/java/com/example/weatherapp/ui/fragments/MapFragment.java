package com.example.weatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;

public class MapFragment extends Fragment {

    private WebView webView;
    private static final String PREFS_NAME = "weather_prefs";
    private static final String MAP_KEY = "map_provider";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize the WebView
        webView = view.findViewById(R.id.webView);
        setupWebView();
        // Load the selected map URL
        loadSelectedMap();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());
    }

    private void loadSelectedMap() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedMap = prefs.getString(MAP_KEY, "world_weather");

        String mapUrl;
        switch (selectedMap) {
            case "open_weather":
                mapUrl = "https://openweathermap.org/weathermap?basemap=map&cities=true&layer=temperature&lat=30&lon=-20&zoom=5";
                break;
            case "zoom_earth":
                mapUrl = "https://zoom.earth/maps/precipitation/#view=32.32,34.85,5z/model=icon";
                break;
            default:
                mapUrl = "https://map.worldweatheronline.com/temperature?lat=44.10919404116584&lng=-11.77734375";
                break;
        }

        webView.loadUrl(mapUrl);
    }
}
