package com.divyansh.weatherly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.divyansh.weatherly.ui.theme.OceanAccent

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherState by weatherViewModel.weatherState.collectAsState()

    var query by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            /* 🔍 SEARCH + 🌙 */
            Row(verticalAlignment = Alignment.CenterVertically) {

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search city") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { weatherViewModel.fetchWeather(query) }
                    )
                )

                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode)
                            Icons.Default.LightMode
                        else
                            Icons.Default.DarkMode,
                        contentDescription = "Toggle theme"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /* 🌦 WEATHER RESULT */
            when (val state = weatherState) {

                WeatherUiState.Idle -> {
                    Text("Search a city to see weather")
                }

                WeatherUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is WeatherUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is WeatherUiState.Success -> {
                    val weather = state.weather
                    val hourly = state.hourly
                    weather.weather[0].description.lowercase()

                    // 🌦 WEATHER-BASED MESSAGE (NEW – SAFE)
                    // 🌤 WEATHER-BASED MESSAGE (SAFE FIX)
                    val weatherMessage = when {
                        weather.weather[0].description.contains("clear", true) ->
                            "☀️ Perfect day for a walk"

                        weather.weather[0].description.contains("rain", true) ||
                                weather.weather[0].description.contains("drizzle", true) ||
                                weather.weather[0].description.contains("thunder", true) ->
                            "🌧 Carry an umbrella"

                        weather.weather[0].description.contains("snow", true) ->
                            "❄️ It’s cold outside, stay warm"

                        weather.weather[0].description.contains("cloud", true) ->
                            "☁️ A calm and cozy day"

                        else ->
                            "🌈 Have a great day!"
                    }


                    Column {

                        // 🌦 MAIN WEATHER CARD
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(weather.name, fontSize = 20.sp)
                                Text(
                                    "${weather.main.temp.toInt()}°C",
                                    fontSize = 32.sp,
                                    color = OceanAccent
                                )
                                Text(weather.weather[0].description)
                            }
                        }

                        // 🧠 SMART MESSAGE (NEW)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = weatherMessage,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 💨💧 WIND + HUMIDITY
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Card(modifier = Modifier.weight(1f)) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("💨 Wind")
                                    Text(
                                        "${weather.wind.speed} m/s",
                                        color = OceanAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Card(modifier = Modifier.weight(1f)) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("💧 Humidity")
                                    Text(
                                        "${weather.main.humidity}%",
                                        color = OceanAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ⏰ HOURLY FORECAST (UNCHANGED)
                        Text("Hourly Forecast", fontSize = 16.sp)

                        LazyRow {
                            items(hourly) { item ->
                                Card(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(90.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(item.time)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("${item.temp}°C", color = OceanAccent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
