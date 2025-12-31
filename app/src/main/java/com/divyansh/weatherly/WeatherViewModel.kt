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

    private val _favorites =
        MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val apiKey = "6d1f33003b2afd1a9d717b682d6fd36b" // keep placeholder

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

                val weather = RetrofitInstance.weatherApi
                    .getWeatherByLatLon(
                        lat = loc.lat,
                        lon = loc.lon,
                        apiKey = apiKey
                    )

                _weatherState.value = WeatherUiState.Success(weather)

            } catch (e: Exception) {
                _weatherState.value =
                    WeatherUiState.Error("Unable to fetch weather")
            }
        }
    }

    fun toggleFavorite(city: String) {
        _favorites.value =
            if (_favorites.value.contains(city))
                _favorites.value - city
            else
                _favorites.value + city
    }
}
