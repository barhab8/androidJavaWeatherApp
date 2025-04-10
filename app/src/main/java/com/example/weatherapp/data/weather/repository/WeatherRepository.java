package com.example.weatherapp.data.weather.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weatherapp.data.weather.api.ApiClient;
import com.example.weatherapp.data.weather.api.ApiService;
import com.example.weatherapp.data.weather.model.AirPollutionResponse;
import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.example.weatherapp.data.weather.model.GeocodingResponse;
import com.example.weatherapp.data.weather.model.WeatherResponse;

import java.util.List;

import retrofit2.Call;
public class WeatherRepository {
    private static final String API_KEY = "513a571468dce9fd66f6df3965ea4716";
    private static final String GEO_API_KEY = "79f8998e3f1ace6865e358a5e7916085";

    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    private final ApiService apiService;
    private final Context context;

    public WeatherRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getApiService();
    }
    // Get the updated unit
    private String getUnit() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(UNIT_KEY, "metric");
    }

    public void setUnit(Context context, String newUnit) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(UNIT_KEY, newUnit).apply();
    }

    public Call<WeatherResponse> fetchWeatherByCity(String city) {
        return apiService.getWeatherByCity(city, API_KEY, getUnit());
    }

    public Call<WeatherResponse> fetchWeatherByCoordinates(double lat, double lon) {
        return apiService.getWeatherByCoordinates(lat, lon, API_KEY, getUnit());
    }

    public Call<ForecastResponse> fetch5DayForecast(String city) {
        return apiService.get5DayForecast(city, getUnit(), API_KEY);
    }
    public Call<ForecastResponse> fetch5DayForecastByCoordinates(double lat, double lon, String units) {
        return apiService.get5DayForecastByCoordinates(lat, lon, units, API_KEY);
    }
    // geocoding api
    public Call<List<GeocodingResponse>> getCoordinatesByCity(String cityName) {
        return apiService.getCoordinatesByCity(cityName, 1, GEO_API_KEY); // Limit to 1 result
    }

    // air pollution
    public Call<AirPollutionResponse> getAirPollutionData(double latitude, double longitude) {
        return apiService.getAirPollutionDataByCoordinates(latitude, longitude, API_KEY);
    }
}
