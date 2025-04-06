package com.example.weatherapp.data.model;

public class PostModel {
    private String userName;
    private String locationName;
    private String weather;
    private String text;
    private com.google.firebase.Timestamp timestamp;

    public PostModel() {}

    public PostModel(String userName, String locationName, String weather, String text, com.google.firebase.Timestamp timestamp) {
        this.userName = userName;
        this.locationName = locationName;
        this.weather = weather;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getWeather() {
        return weather;
    }

    public String getText() {
        return text;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }
}
