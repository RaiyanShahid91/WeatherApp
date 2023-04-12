package com.coding.weather.utils;

import com.coding.weather.model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<Example> getWeatherByCity(
            @Query("q")
            String city,
            @Query("appid")
            String apiKey);

    @GET("weather")
    Call<Example> getWeatherByZip(
            @Query("zip")
            String city,
            @Query("appid")
            String apiKey);
    @GET("weather")
    Call<Example> getWeatherByLatLong(
            @Query("lat")
            String latitude,
            @Query("lon")
            String longitude,
            @Query("appid")
            String apiKey);

}
