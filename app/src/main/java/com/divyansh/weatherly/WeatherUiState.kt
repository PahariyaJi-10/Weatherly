package com.divyansh.weatherly

sealed class WeatherUiState {

    object Idle : WeatherUiState()

    object Loading : WeatherUiState()

    data class Success(
        val weather: WeatherResponse,
        val hourly: List<HourlyForecastItem>
    ) : WeatherUiState()

    data class Error(
        val message: String
    ) : WeatherUiState()
}

