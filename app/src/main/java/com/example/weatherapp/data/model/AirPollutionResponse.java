package com.example.weatherapp.data.model;

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

        @SerializedName("components")
        private Components components;

        @SerializedName("dt")
        private long timestamp;

        public Main getMain() {
            return main;
        }

        public Components getComponents() {
            return components;
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

        public static class Components {
            @SerializedName("co")
            private double co; // Carbon Monoxide

            @SerializedName("no")
            private double no; // Nitric Oxide

            @SerializedName("no2")
            private double no2; // Nitrogen Dioxide

            @SerializedName("o3")
            private double o3; // Ozone

            @SerializedName("so2")
            private double so2; // Sulfur Dioxide

            @SerializedName("pm2_5")
            private double pm2_5; // Particulate Matter 2.5

            @SerializedName("pm10")
            private double pm10; // Particulate Matter 10

            @SerializedName("nh3")
            private double nh3; // Ammonia

            public double getCo() {
                return co;
            }

            public double getNo() {
                return no;
            }

            public double getNo2() {
                return no2;
            }

            public double getO3() {
                return o3;
            }

            public double getSo2() {
                return so2;
            }

            public double getPm2_5() {
                return pm2_5;
            }

            public double getPm10() {
                return pm10;
            }

            public double getNh3() {
                return nh3;
            }
        }
    }
}
