package com.example.weatherapp.ui.utils;

import com.example.weatherapp.data.weather.model.ForecastResponse.ForecastItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ForecastProcessor {

    private static final String TARGET_TIME = "12:00:00";  // Target time to select daily forecast

    /**
     * Processes the forecast list to get a single daily forecast closest to the target time.
     * @param forecastList List of forecast items (raw API response).
     * @return List of daily forecasts (one per day).
     */
    public List<ForecastItem> getDailyForecast(List<ForecastItem> forecastList) {
        Map<String, List<ForecastItem>> groupedByDate = forecastList.stream()
                .collect(Collectors.groupingBy(item -> item.getDateTime().substring(0, 10)));

        List<ForecastItem> dailyForecasts = new ArrayList<>();

        for (String date : groupedByDate.keySet()) {
            List<ForecastItem> dailyItems = groupedByDate.get(date);

            ForecastItem closestToMidday = dailyItems.stream()
                    .min(Comparator.comparing(item -> timeDifference(item.getDateTime(), TARGET_TIME)))
                    .orElse(null);

            if (closestToMidday != null) {
                dailyForecasts.add(closestToMidday);
            }
        }

        // Sort daily forecasts by date in ascending order
        dailyForecasts.sort(Comparator.comparing(item -> item.getDateTime().substring(0, 10)));

        return dailyForecasts;
    }

    /**
     * Helper method to calculate the time difference from the target time.
     * @param dateTime The full date-time string (e.g., "2025-01-14 21:00:00").
     * @param targetTime The target time string (e.g., "12:00:00").
     * @return The absolute time difference in milliseconds.
     */
    private long timeDifference(String dateTime, String targetTime) {
        try {
            String timePart = dateTime.substring(11);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date forecastTime = sdf.parse(timePart);
            Date target = sdf.parse(targetTime);

            return Math.abs(forecastTime.getTime() - target.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.MAX_VALUE;
    }

    /**
     * Formats a raw date-time string into a more user-friendly format.
     * @param dateTime The full date-time string (e.g., "2025-01-14 21:00:00").
     * @return A formatted date string (e.g., "Tue, Jan 14").
     */
    public String formatDate(String dateTime) {
        try {
            // Parse the input date-time string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date); // Return the formatted date
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime; // Fallback to the original format if parsing fails
        }
    }
}
