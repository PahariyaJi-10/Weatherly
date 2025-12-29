package com.divyansh.weatherly

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // 🌍 WEATHER BY LATITUDE & LONGITUDE (RECOMMENDED)
    @GET("data/2.5/weather")
    suspend fun getWeatherByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // ⏱ HOURLY FORECAST (OPTIONAL BUT GOOD)
    @GET("data/2.5/forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}
