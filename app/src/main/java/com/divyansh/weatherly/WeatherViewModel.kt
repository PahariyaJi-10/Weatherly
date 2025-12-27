package com.divyansh.weatherly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherState =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    private val _hourlyForecast =
        MutableStateFlow<List<ForecastItem>>(emptyList())
    val hourlyForecast: StateFlow<List<ForecastItem>> = _hourlyForecast

    // ⚠️ PUT YOUR REAL KEY HERE (do not commit real key)
    private val apiKey ="6d1f33003b2afd1a9d717b682d6fd36b"

    fun fetchWeather(city: String) {
        if (city.isBlank()) return

        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                // 🌦 Current weather
                val weatherResponse =
                    RetrofitInstance.weatherApi.getWeather(city, apiKey)

                _weatherState.value =
                    WeatherUiState.Success(weatherResponse)

                // ⏱ Hourly forecast (safe)
                try {
                    val forecastResponse =
                        RetrofitInstance.forecastApi
                            .getHourlyForecast(city, apiKey)

                    _hourlyForecast.value =
                        forecastResponse.list.take(6)

                } catch (e: Exception) {
                    _hourlyForecast.value = emptyList()
                }

            } catch (e: Exception) {
                _weatherState.value =
                    WeatherUiState.Error("City not found or API error")
            }
        }
    }
}
