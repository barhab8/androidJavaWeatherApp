package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AirPollutionResponse {

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("list")
    private List<AirQuality> airQualityList;

    public Coord getCoord() {
        return coord;
    }

    public List<AirQuality> getAirQualityList() {
        return airQualityList;
    }

    public static class Coord {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lon")
        private double lon;

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }

    public static class AirQuality {
        @SerializedName("main")
        private Main main;

        @SerializedName("dt")
        private long timestamp;

        public Main getMain() {
            return main;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public static class Main {
            @SerializedName("aqi")
            private int aqi; // Air Quality Index

            public int getAqi() {
                return aqi;
            }
        }
    }
}
