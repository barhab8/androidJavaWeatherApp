package com.example.weatherapp.data.weather.api;

import com.example.weatherapp.data.weather.model.AirPollutionResponse;
import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.example.weatherapp.data.weather.model.GeocodingResponse;
import com.example.weatherapp.data.weather.model.ReverseGeocodingResponse;
import com.example.weatherapp.data.weather.model.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherByCity(@Query("q") String city, @Query("appid") String apiKey, @Query("units") String units);

    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherByCoordinates(@Query("lat") double lat, @Query("lon") double lon,
                                                  @Query("appid") String apiKey, @Query("units") String units);

    // 5-day forecast API
    @GET("data/2.5/forecast")
    Call<ForecastResponse> get5DayForecast(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
    @GET("data/2.5/forecast")
    Call<ForecastResponse> get5DayForecastByCoordinates(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
    // air pollution
    @GET("data/2.5/air_pollution")
    Call<AirPollutionResponse> getAirPollutionDataByCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey
    );

    //geocodingAPI

    @GET("geo/1.0/direct")
    Call<List<GeocodingResponse>> getCoordinatesByCity(
            @Query("q") String cityName,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );

    @GET("geo/1.0/reverse")
    Call<List<ReverseGeocodingResponse>> getCityByCoordinates(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey
    );


}
