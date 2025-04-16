package com.example.weatherapp.ui.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class UserLocationProvider {

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

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        @SuppressLint("MissingPermission")
        var task = fusedLocationClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
            } else {
                callback.onError("Unable to retrieve location.");
            }
        });

        task.addOnFailureListener(e -> {
            callback.onError("Location error: " + e.getMessage());
        });
    }
}
