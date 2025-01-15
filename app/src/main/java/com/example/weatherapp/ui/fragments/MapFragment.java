package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.weatherapp.R;

public class MapFragment extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize the WebView
        webView = view.findViewById(R.id.webView);
        setupWebView();

        // Load the OpenWeatherMap web map URL
        String openWeatherMapUrl = "https://map.worldweatheronline.com/temperature?lat=44.10919404116584&lng=-11.77734375";
        webView.loadUrl(openWeatherMapUrl);

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webSettings.setDomStorageEnabled(true); // Enable DOM storage for better performance
        webSettings.setUseWideViewPort(true); // Enable responsive layout
        webSettings.setLoadWithOverviewMode(true); // Zoom out to fit the content

        // Handle URL loading inside the WebView (instead of opening a browser)
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Destroy the WebView to prevent memory leaks
        if (webView != null) {
            webView.destroy();
        }
    }
}
