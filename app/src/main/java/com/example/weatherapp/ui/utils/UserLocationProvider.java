package com.example.weatherapp.ui.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class UserLocationProvider {

    private static Double cachedLatitude = null;
    private static Double cachedLongitude = null;

    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
        void onError(String errorMessage);
    }

    public static void getCurrentLocation(Context context, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permission not granted.");
            return;
        }

        // Return from cache if available
        if (cachedLatitude != null && cachedLongitude != null) {
            callback.onLocationReceived(cachedLatitude, cachedLongitude);
            // Also update in background (non-blocking)
            updateLocationInBackground(context);
            return;
        }

        fetchAndCacheLocation(context, callback);
    }

    @SuppressLint("MissingPermission")
    private static void fetchAndCacheLocation(Context context, LocationCallback callback) {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        var task = fusedLocationClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null) {
                cachedLatitude = location.getLatitude();
                cachedLongitude = location.getLongitude();
                callback.onLocationReceived(cachedLatitude, cachedLongitude);
            } else {
                callback.onError("Unable to retrieve location.");
            }
        });

        task.addOnFailureListener(e -> {
            callback.onError("Location error: " + e.getMessage());
        });
    }

    @SuppressLint("MissingPermission")
    private static void updateLocationInBackground(Context context) {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        var task = fusedLocationClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null) {
                cachedLatitude = location.getLatitude();
                cachedLongitude = location.getLongitude();
            }
        });
    }
}
