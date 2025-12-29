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

    // ⚠️ DO NOT COMMIT REAL KEY
    private val apiKey = "6d1f33003b2afd1a9d717b682d6fd36b"

    fun fetchWeather(query: String) {
        if (query.isBlank()) return

        _weatherState.value = WeatherUiState.Loading

        viewModelScope.launch {
            try {
                /* 1️⃣ GEOCODING — convert any place to lat/lon */
                val locations = RetrofitInstance.geoCodingApi
                    .getLocation(
                        city = query,
                        apiKey = apiKey
                    )

                if (locations.isEmpty()) {
                    _weatherState.value =
                        WeatherUiState.Error("Location not found. Try a nearby city.")
                    return@launch
                }

                val location = locations.first()
                val lat = location.lat
                val lon = location.lon

                /* 2️⃣ CURRENT WEATHER (lat/lon based) */
                val weatherResponse =
                    RetrofitInstance.weatherApi
                        .getWeatherByLatLon(
                            lat = lat,
                            lon = lon,
                            apiKey = apiKey
                        )

                _weatherState.value =
                    WeatherUiState.Success(weatherResponse)

                /* 3️⃣ HOURLY FORECAST (safe call) */
                try {
                    val forecastResponse =
                        RetrofitInstance.weatherApi
                            .getHourlyForecast(
                                lat = lat,
                                lon = lon,
                                apiKey = apiKey
                            )

                    _hourlyForecast.value =
                        forecastResponse.list.take(6)

                } catch (e: Exception) {
                    // Forecast failure should NOT break weather
                    _hourlyForecast.value = emptyList()
                }

            } catch (e: Exception) {
                _weatherState.value =
                    WeatherUiState.Error("Unable to fetch weather. Check internet.")
                _hourlyForecast.value = emptyList()
            }
        }
    }
}
