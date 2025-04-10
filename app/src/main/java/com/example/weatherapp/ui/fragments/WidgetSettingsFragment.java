package com.example.weatherapp.ui.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.widget.WeatherWidgetProvider;

public class WidgetSettingsFragment extends Fragment {
    private EditText cityEditText;
    private Button saveButton;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget_settings, container, false);

        cityEditText = view.findViewById(R.id.cityEditText);
        saveButton = view.findViewById(R.id.saveButton);
        sharedPreferences = requireContext().getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE);

        // Load the saved city name
        cityEditText.setText(sharedPreferences.getString("city", ""));

        saveButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();
            if (!city.isEmpty()) {
                sharedPreferences.edit().putString("city", city).apply();
                updateWidget(requireContext());
                Toast.makeText(getContext(), "Location Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateWidget(Context context) {
        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
