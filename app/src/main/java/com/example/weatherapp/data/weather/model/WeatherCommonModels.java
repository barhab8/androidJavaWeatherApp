package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;

public class WeatherCommonModels {

    public static class Weather {
        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        @SerializedName("deg")
        private int degree = -1; // Default if not present (like in ForecastResponse)

        public double getSpeed() {
            return speed;
        }

        public int getDegree() {
            return degree;
        }
    }
}
