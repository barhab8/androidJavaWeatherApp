package com.example.weatherapp.data.AI;

import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.example.weatherapp.ui.utils.UIHelper;

import java.util.List;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

public class PromptBuilder {

    public static String buildPrompt(
            String city, String temp, String windSpeed, String humidity, String visibility,
            String weatherDescription, String aqi, List<ForecastResponse.ForecastItem> forecastList
    ) {
        String tempUnitSymbol, windSpeedUnit;
        switch (UNIT) {
            case "imperial":
                tempUnitSymbol = "°F";
                windSpeedUnit = " mph";
                break;
            case "standard":
                tempUnitSymbol = " K";
                windSpeedUnit = " m/s";
                break;
            default:
                tempUnitSymbol = "°C";
                windSpeedUnit = " m/s";
                break;
        }

        StringBuilder forecastSection = new StringBuilder();

        for (ForecastResponse.ForecastItem item : forecastList) {
            String date = UIHelper.formatDate(item.getDateTime());
            String forecastTemp = String.format("%.1f%s", item.getMain().getTemp(), tempUnitSymbol);
            String desc = item.getWeather().get(0).getDescription();

            forecastSection.append("- ").append(date)
                    .append(": ").append(forecastTemp)
                    .append(", ").append(desc)
                    .append("\n");
        }

        return "Based on the provided weather data, generate a concise, one-line tip for residents of "+ city + ". This tip should offer practical advice on:\u200B\n" +
                "\n" +
                "Appropriate clothing choices considering current temperature, wind speed, humidity, visibility, and weather conditions.\u200B\n" +
                "\n" +
                "Suitable outdoor activities given the current air quality index (AQI).\u200B\n" +
                "\n" +
                "Preparations for the upcoming week's weather forecast.\u200B\n" +
                "\n" +
                "Weather Data:\n" +
                "\n" +
                "Current Temperature: " + temp + " " + tempUnitSymbol + "\n" +
                "\n" +
                "Wind Speed:" + windSpeed + " " +  windSpeedUnit + "\n" +
                "\n" +
                "Humidity:" + humidity + "%\n" +
                "\n" +
                "Visibility:" + visibility + "\n" +
                "\n" +
                "Weather Description:" + weatherDescription + "\n" +
                "\n" +
                "Air Quality Index (AQI):" + aqi + "(Air Quality Index. Possible values: 1, 2, 3, 4, 5. Where 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor)\n" +
                "\n" +
                "City:" + city + "\n" +
                "\n" +
                "5-Day Forecast:" + forecastSection + "\n" +
                "\n" +
                "Instruction: Provide a weather-conscious, action-oriented suggestion tailored for" +city + "residents and tourists.\"\u200B";
    }
}
