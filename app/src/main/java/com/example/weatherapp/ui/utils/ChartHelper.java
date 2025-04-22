package com.example.weatherapp.ui.utils;

import android.graphics.Color;

import com.example.weatherapp.data.weather.model.AirPollutionResponse;
import com.example.weatherapp.data.weather.model.ForecastResponse;
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
     * Returns the appropriate colors for the AQI levels.
     *
     * @param forecastList The list of BarEntry containing AQI levels.
     * @return A list of colors for the entries.
     */

    public static void populateForecastChart(LineChart chart, List<ForecastResponse.ForecastItem> forecastList) {
        // Prepare entries for the chart
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        for (int i = 0; i < forecastList.size(); i++) {
            ForecastResponse.ForecastItem item = forecastList.get(i);

            // Add temperature data as an Entry
            entries.add(new Entry(i, (float) item.getMain().getTemp()));
            String dateLabel = dateFormat.format(new Date( Integer.parseInt(item.getDt()) * 1000));
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
