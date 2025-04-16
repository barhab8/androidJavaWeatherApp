package com.example.weatherapp.data.weather.model;

import com.google.gson.annotations.SerializedName;

public class ReverseGeocodingResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    @SerializedName("country")
    private String country;

    @SerializedName("state")
    private String state;

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }
}
