package com.example.weatherapp.data.social;

public class PostModel {
    private String postId;
    private String userId;
    private String userName;
    private String locationName;
    private String weather;
    private String text;
    private com.google.firebase.Timestamp timestamp;

    public PostModel() {}

    public PostModel(String postId, String userId, String userName, String locationName, String weather, String text, com.google.firebase.Timestamp timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.locationName = locationName;
        this.weather = weather;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
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