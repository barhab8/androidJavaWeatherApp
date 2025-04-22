package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import static com.example.weatherapp.data.weather.model.WeatherCommonModels.*;

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

    public static class ForecastItem {

        @SerializedName("dt_txt")
        private String dateTime;

        @SerializedName("dt")
        private String dt;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("wind")
        private Wind wind;

        public String getDateTime() {
            return dateTime;
        }

        public String getDt() {
            return dt;
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

    public static class City {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
}
