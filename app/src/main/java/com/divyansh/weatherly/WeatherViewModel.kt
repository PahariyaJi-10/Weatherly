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

    private val apiKey = "6d1f33003b2afd1a9d717b682d6fd36b"

    fun fetchWeather(city: String) {
        if (city.isBlank()) return

        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                val locations = RetrofitInstance.geoCodingApi
                    .getLocation(city, apiKey = apiKey)

                if (locations.isEmpty()) {
                    _weatherState.value =
                        WeatherUiState.Error("Location not found")
                    return@launch
                }

                val loc = locations.first()

                val weather =
                    RetrofitInstance.weatherApi.getWeatherByLatLon(
                        lat = loc.lat,
                        lon = loc.lon,
                        apiKey = apiKey
                    )

                val forecast =
                    RetrofitInstance.weatherApi.getHourlyForecast(
                        lat = loc.lat,
                        lon = loc.lon,
                        apiKey = apiKey
                    )

                // ✅ SAFE FOR ALL ANDROID VERSIONS
                val hourly = forecast.list.take(6).map {
                    HourlyForecastItem(
                        time = it.dt_txt.substring(11, 16), // HH:mm
                        temp = it.main.temp.toInt(),
                        condition = it.weather[0].main.lowercase()
                    )
                }

                _weatherState.value =
                    WeatherUiState.Success(
                        weather = weather,
                        hourly = hourly
                    )

            } catch (e: Exception) {
                _weatherState.value =
                    WeatherUiState.Error("Unable to fetch weather")
            }
        }
    }
}
