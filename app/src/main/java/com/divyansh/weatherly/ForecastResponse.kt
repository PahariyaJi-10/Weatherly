package com.divyansh.weatherly

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: ForecastMain,
    val weather: List<ForecastWeather> // ✅ THIS WAS MISSING
)

data class ForecastMain(
    val temp: Double
)

data class ForecastWeather(
    val main: String,
    val description: String
)

