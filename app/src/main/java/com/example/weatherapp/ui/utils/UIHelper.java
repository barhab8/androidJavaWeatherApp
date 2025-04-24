package com.example.weatherapp.ui.utils;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.AirPollutionResponse;
import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.stream.Collectors;

/**
 * A consolidated utility class for UI and data processing related to weather and air quality.
 */
public class UIHelper {

    private static final String TARGET_TIME = "12:00:00";  // Target time to select daily forecast

    // ----- UI BACKGROUND UPDATE -----
    public static void updateBackground(LinearLayout rootLayout, String weatherIcon) {
        int backgroundResId;

        switch (weatherIcon) {
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

    // ----- AIR QUALITY UI -----
    public static void updateAirQualityUI(AirPollutionResponse airPollutionResponse, TextView tvAirQualityIndex) {
        if (!airPollutionResponse.getAirQualityList().isEmpty()) {
            AirPollutionResponse.AirQuality airQuality = airPollutionResponse.getAirQualityList().get(0);
            int aqi = airQuality.getMain().getAqi();

            tvAirQualityIndex.setText(String.format("Air Quality Index (AQI): %d", aqi));

            int color;
            switch (aqi) {
                case 1:
                    color = 0xFF00E676; // Green
                    break;
                case 2:
                    color = 0xFFFFFF00; // Yellow
                    break;
                case 3:
                    color = 0xFFFF9800; // Orange
                    break;
                case 4:
                    color = 0xFFFF5722; // Red
                    break;
                case 5:
                    color = 0xFFB71C1C; // Dark Red
                    break;
                default:
                    color = 0xFFFFFFFF; // White
                    break;
            }
            tvAirQualityIndex.setTextColor(color);
        }
    }

    // ----- DAILY FORECAST PROCESSING -----
    public static List<ForecastResponse.ForecastItem> getDailyForecast(List<ForecastResponse.ForecastItem> forecastList) {
        Map<String, List<ForecastResponse.ForecastItem>> groupedByDate = forecastList.stream()
                .collect(Collectors.groupingBy(item -> item.getDateTime().substring(0, 10)));

        List<ForecastResponse.ForecastItem> dailyForecasts = new ArrayList<>();

        for (String date : groupedByDate.keySet()) {
            List<ForecastResponse.ForecastItem> dailyItems = groupedByDate.get(date);

            ForecastResponse.ForecastItem closestToMidday = dailyItems.stream()
                    .min(Comparator.comparing(item -> timeDifference(item.getDateTime(), TARGET_TIME)))
                    .orElse(null);

            if (closestToMidday != null) {
                dailyForecasts.add(closestToMidday);
            }
        }

        dailyForecasts.sort(Comparator.comparing(item -> item.getDateTime().substring(0, 10)));

        return dailyForecasts;
    }

    private static long timeDifference(String dateTime, String targetTime) {
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

    public static String formatDate(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    // ----- FORECAST CHART -----
    public static void populateForecastChart(LineChart chart, List<ForecastResponse.ForecastItem> forecastList) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        for (int i = 0; i < forecastList.size(); i++) {
            ForecastResponse.ForecastItem item = forecastList.get(i);
            entries.add(new Entry(i, (float) item.getMain().getTemp()));
            String dateLabel = dateFormat.format(new Date(Integer.parseInt(item.getDt()) * 1000L));
            labels.add(dateLabel);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(8f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
}
