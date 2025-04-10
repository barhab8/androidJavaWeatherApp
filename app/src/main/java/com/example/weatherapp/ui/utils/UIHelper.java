package com.example.weatherapp.ui.utils;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.AirPollutionResponse;

public class UIHelper {

    /**
     * Updates the background of the root layout based on the weather icon.
     *
     * @param rootLayout The root layout to update.
     * @param weatherIcon The weather icon string from the API.
     */
    public static void updateBackground(LinearLayout rootLayout, String weatherIcon) {
        int backgroundResId;

        switch (weatherIcon) {
            case "01d":
            case "01n":
                backgroundResId = R.drawable.clear_sky_bg;
                break;
            case "02d":
            case "02n":
                backgroundResId = R.drawable.few_clouds_bg;
                break;
            case "03d":
            case "03n":
            case "04d":
            case "04n":
                backgroundResId = R.drawable.broken_clouds_bg;
                break;
            case "09d":
            case "09n":
                backgroundResId = R.drawable.shower_rain_bg;
                break;
            case "10d":
            case "10n":
                backgroundResId = R.drawable.rain_bg;
                break;
            case "11d":
            case "11n":
                backgroundResId = R.drawable.thunderstorm_bg;
                break;
            case "13d":
            case "13n":
                backgroundResId = R.drawable.snow_bg;
                break;
            case "50d":
            case "50n":
                backgroundResId = R.drawable.mist_bg;
                break;
            default:
                backgroundResId = R.drawable.clear_sky_bg;
                break;
        }

        rootLayout.setBackgroundResource(backgroundResId);
    }

    /**
     * Updates the UI for the Air Quality Index (AQI).
     *
     * @param airPollutionResponse The air pollution response from the API.
     * @param tvAirQualityIndex    The TextView to display the AQI.
     */
    public static void updateAirQualityUI(AirPollutionResponse airPollutionResponse, TextView tvAirQualityIndex) {
        if (!airPollutionResponse.getAirQualityList().isEmpty()) {
            AirPollutionResponse.AirQuality airQuality = airPollutionResponse.getAirQualityList().get(0);
            int aqi = airQuality.getMain().getAqi();

            // Set AQI text
            tvAirQualityIndex.setText(String.format("Air Quality Index (AQI): %d", aqi));

            // Change text color based on AQI level
            int color;
            switch (aqi) {
                case 1:
                    color = 0xFF00E676; // Green - Good
                    break;
                case 2:
                    color = 0xFFFFFF00; // Yellow - Moderate
                    break;
                case 3:
                    color = 0xFFFF9800; // Orange - Unhealthy for Sensitive Groups
                    break;
                case 4:
                    color = 0xFFFF5722; // Red - Unhealthy
                    break;
                case 5:
                    color = 0xFFB71C1C; // Dark Red - Hazardous
                    break;
                default:
                    color = 0xFFFFFFFF; // White - Unknown
                    break;
            }
            tvAirQualityIndex.setTextColor(color);
        }
    }

}

