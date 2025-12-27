package com.divyansh.weatherly

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: ForecastMain
)

data class ForecastMain(
    val temp: Double
)
