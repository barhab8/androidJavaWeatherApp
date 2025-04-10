package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<ForecastItem> forecastList;

    @SerializedName("city")
    private City city;

    public List<ForecastItem> getForecastList() {
        return forecastList;
    }

    public City getCity() {
        return city;
    }

    // Nested classes for forecast items and city details
    public static class ForecastItem {

        @SerializedName("dt_txt")
        private String dateTime;

        @SerializedName("dt")
        private String DT;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("wind")
        private Wind wind;

        public String getDateTime() {
            return dateTime;
        }
        public String getDT() {
            return DT;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public Wind getWind() {
            return wind;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        public double getTemp() {
            return temp;
        }
    }

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

        public double getSpeed() {
            return speed;
        }
    }

    public static class City {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
}
