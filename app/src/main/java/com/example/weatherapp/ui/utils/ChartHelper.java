package com.example.weatherapp.ui.utils;

import android.graphics.Color;

import com.example.weatherapp.data.model.AirPollutionResponse;
import com.example.weatherapp.data.model.ForecastResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartHelper {

    /**
     * Populates and configures the AQI BarChart.
     *
     * @param chart          The BarChart to populate.
     * @param airQualityList The list of air quality data points.
     */
    public static void populateAQIChart(BarChart chart, List<AirPollutionResponse.AirQuality> airQualityList) {
        // Create a list of BarEntry for the chart
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < airQualityList.size(); i++) {
            AirPollutionResponse.AirQuality airQuality = airQualityList.get(i);
            entries.add(new BarEntry(i, airQuality.getMain().getAqi()));

            // Convert timestamp to readable date
            String dateLabel = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                    .format(new Date(airQuality.getTimestamp() * 1000));
            labels.add(dateLabel);
        }

        // Create a dataset and give it a type
        BarDataSet dataSet = new BarDataSet(entries, "AQI Levels");
        dataSet.setColors(getAQIColors(entries)); // Set custom colors for AQI levels
        dataSet.setValueTextColor(Color.WHITE); // Value text color
        dataSet.setValueTextSize(10f);

        // Create the BarData object and set it to the chart
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        // Disable X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false); // Remove the X-axis entirely

        // Configure Y-axis (AQI values)
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f); // Minimum AQI
        leftAxis.setAxisMaximum(5f); // AQI ranges from 1 to 5

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        // Configure chart appearance
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false); // Disable description text
        chart.invalidate(); // Refresh the chart
    }

    /**
     * Returns the appropriate colors for the AQI levels.
     *
     * @param entries The list of BarEntry containing AQI levels.
     * @return A list of colors for the entries.
     */
    private static List<Integer> getAQIColors(List<BarEntry> entries) {
        List<Integer> colors = new ArrayList<>();
        for (BarEntry entry : entries) {
            float aqi = entry.getY();
            if (aqi == 1) {
                colors.add(Color.GREEN); // Good
            } else if (aqi == 2) {
                colors.add(Color.YELLOW); // Moderate
            } else if (aqi == 3) {
                colors.add(Color.parseColor("#FFA500")); // Unhealthy for sensitive groups
            } else if (aqi == 4) {
                colors.add(Color.RED); // Unhealthy
            } else if (aqi == 5) {
                colors.add(Color.MAGENTA); // Very Unhealthy
            } else {
                colors.add(Color.GRAY); // Default
            }
        }
        return colors;
    }

    public static void populateForecastChart(LineChart chart, List<ForecastResponse.ForecastItem> forecastList) {
        // Prepare entries for the chart
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        for (int i = 0; i < forecastList.size(); i++) {
            ForecastResponse.ForecastItem item = forecastList.get(i);

            // Add temperature data as an Entry
            entries.add(new Entry(i, (float) item.getMain().getTemp()));
            String dateLabel = dateFormat.format(new Date( Integer.parseInt(item.getDT()) * 1000));
            labels.add(dateLabel);
        }

        // Create a dataset and configure it
        LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(8f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(4f);

        // Configure the chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Configure the X-axis with a custom ValueFormatter
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f); // Ensure one label per entry
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Configure Y-axis
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false); // Disable right Y-axis
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false); // Disable description text

        // Refresh the chart
        chart.invalidate();
    }
}
