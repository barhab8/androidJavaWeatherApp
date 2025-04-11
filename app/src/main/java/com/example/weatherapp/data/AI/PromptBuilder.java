package com.example.weatherapp.data.AI;

import static com.example.weatherapp.ui.fragments.WeatherFragment.UNIT;

public class PromptBuilder {

    public static String buildPrompt(
            String city, String temp, String windSpeed, String humidity, String visibility,
            String weatherDescription, String aqi
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

        return "**Analyze the following weather data, and generate a one-line weather tip that focuses on practical advice for what people should do or wear, considering the current weather and air quality. Keep the tip relevant to the city and suggest any outdoor activities that would be ideal for the current weather and what you should wear. **\n\n" +
                "- **Current Temperature:** " + temp + " " + tempUnitSymbol + "\n" +
                "- **Current Wind Speed:** " + windSpeed +" " + windSpeedUnit +" \n" +
                "- **Current Humidity:** " + humidity + "%\n" +
                "- **Current Visibility:** " + visibility + "\n" +
                "- **Current Weather Description:** " + weatherDescription + "\n" +
                "- **Air Quality Index (AQI):** " + aqi + "\n" +
                "- **City:** " + city + "\n\n" +
                "**Tip:** Provide a weather-conscious, action-oriented suggestion for the city, considering current conditions.";
    }


}
