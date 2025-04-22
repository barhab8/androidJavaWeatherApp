package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;
import static com.example.weatherapp.data.weather.model.WeatherCommonModels.*;

public class WeatherResponse {

    @SerializedName("name")
    private String city;

    @SerializedName("visibility")
    private String visibility;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("wind")
    private Wind wind;

    // Inner class for temperature and humidity
    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public String getCity() {
        return city;
    }

    public String getVisibility() {
        return visibility;
    }

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }
}
