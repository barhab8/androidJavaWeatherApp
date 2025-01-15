package com.example.weatherapp.data.repository;

import com.example.weatherapp.data.api.ApiClient;
import com.example.weatherapp.data.api.ApiService;
import com.example.weatherapp.data.model.AirPollutionResponse;
import com.example.weatherapp.data.model.ForecastResponse;
import com.example.weatherapp.data.model.GeocodingResponse;
import com.example.weatherapp.data.model.WeatherResponse;

import java.util.List;

import retrofit2.Call;

public class WeatherRepository {
    private static final String API_KEY = "79f8998e3f1ace6865e358a5e7916085";
    private static final String GEO_API_KEY = "79f8998e3f1ace6865e358a5e7916085";


    private final ApiService apiService;

    public WeatherRepository() {
        apiService = ApiClient.getApiService();
    }
    // weather
    public Call<WeatherResponse> fetchWeatherByCity(String city, String units) {
        return apiService.getWeatherByCity(city, API_KEY, units);
    }
    public Call<WeatherResponse> fetchWeatherByCoordinates(double lat, double lon, String units) {
        return apiService.getWeatherByCoordinates(lat, lon, API_KEY, units);
    }
    // forcast
    public Call<ForecastResponse> fetch5DayForecast(String city, String units) {
        return apiService.get5DayForecast(city, units, API_KEY);
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
