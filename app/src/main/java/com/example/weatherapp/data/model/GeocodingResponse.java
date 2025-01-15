package com.example.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;

public class GeocodingResponse {

    @SerializedName("name")
    private String cityName;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    @SerializedName("country")
    private String country;

    @SerializedName("state")
    private String state;

    public String getCityName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }
}
