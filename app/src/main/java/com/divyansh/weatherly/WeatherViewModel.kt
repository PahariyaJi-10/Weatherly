package com.divyansh.weatherly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    // 🌦 Current weather UI state
    private val _weatherState =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    // ⏱ Hourly forecast state
    private val _hourlyForecast =
        MutableStateFlow<List<HourlyForecast>>(emptyList())
    val hourlyForecast: StateFlow<List<HourlyForecast>> = _hourlyForecast

    // 🔐 API key (replace later with secure method)
    private val apiKey = "YOUR_API_KEY"

    fun fetchWeather(city: String) {
        if (city.isBlank()) return

        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                // 🌦 Fetch current weather
                val weatherResponse =
                    RetrofitInstance.weatherApi.getWeather(city, apiKey)

                // ⏱ Fetch hourly forecast
                val forecastResponse =
                    RetrofitInstance.forecastApi.getHourlyForecast(city, apiKey)
                _weatherState.value =
                    WeatherUiState.Success(weatherResponse)

                // Take next 6 time slots (~18 hours)
                _hourlyForecast.value =
                    forecastResponse.list.take(6)

            } catch (e: Exception) {
                _weatherState.value =
                    WeatherUiState.Error("Unable to fetch weather data")
                _hourlyForecast.value = emptyList()
            }
        }
    }
}
