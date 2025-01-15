package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;

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

    // Inner classes for nested JSON objects
    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        @SerializedName("deg")
        private int degree;

        public double getSpeed() { return speed; }
        public int getDegree() { return degree; }
    }

    public String getCity() { return city; }
    public String getVisibilit() { return visibility; }
    public Main getMain() { return main; }
    public Weather[] getWeather() { return weather; }
    public Wind getWind() { return wind; }
}
