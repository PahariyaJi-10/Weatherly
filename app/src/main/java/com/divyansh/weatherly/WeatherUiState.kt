package com.divyansh.weatherly

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(
        val data: WeatherResponse,
        val isFavorite: Boolean = false
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}