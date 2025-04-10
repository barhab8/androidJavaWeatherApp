package com.example.weatherapp.data.AI;

public class PromptBuilder {

    public static String buildPrompt(
            String city, String temp, String windSpeed, String humidity, String visibility,
            String weatherDescription, String aqi
    ) {

        return "**Analyze the following weather and forecast data, and generate a one-line weather tip that focuses on practical advice for what people should do or wear, considering the current weather, air quality, and forecasted conditions. Keep the tip relevant to the city and suggest any outdoor activities that would be ideal for the current or upcoming weather.**\n\n" +
                "- **Current Temperature:** " + temp + " °C\n" +
                "- **Current Wind Speed:** " + windSpeed + " m/s\n" +
                "- **Current Humidity:** " + humidity + "%\n" +
                "- **Current Visibility:** " + visibility + "\n" +
                "- **Current Weather Description:** " + weatherDescription + "\n" +
                "- **Air Quality Index (AQI):** " + aqi + "\n" +
                "- **City:** " + city + "\n\n" +
                "**Tip:** Provide a weather-conscious, action-oriented suggestion for the city, considering current and upcoming conditions.";
    }
}
