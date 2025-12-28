package com.divyansh.weatherly

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.divyansh.weatherly.ui.theme.*

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    var city by remember { mutableStateOf("") }

    val weatherState = weatherViewModel.weatherState.collectAsState().value
    val hourly = weatherViewModel.hourlyForecast.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /* 🌙 DARK MODE TOGGLE */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkMode)
                        Icons.Default.LightMode
                    else
                        Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }

        /* 🔍 SEARCH BAR */
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search city") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                IconButton(
                    onClick = { weatherViewModel.fetchWeather(city) }
                ) {
                    Icon(Icons.Default.Search, null)
                }
            },
            shape = RoundedCornerShape(14.dp)
        )

        when (weatherState) {

            is WeatherUiState.Idle -> {
                Text("Search for a city 🌍")
            }

            is WeatherUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = OceanAccent,
                        strokeWidth = 3.dp
                    )
                }
            }

            is WeatherUiState.Error -> {
                Text(
                    text = weatherState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                val data = weatherState.data

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        /* 🌦 WEATHER CARD */
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = getWeatherIcon(data.weather[0].main),
                                    contentDescription = data.weather[0].main,
                                    tint = OceanAccent,
                                    modifier = Modifier.size(72.dp)
                                )


                                Text(
                                    data.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "${data.main.temp.toInt()}°C",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    data.weather[0].description,
                                    color = MaterialTheme.colorScheme
                                        .onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }

                        /* ⏱ HOURLY FORECAST */
                        if (hourly.isNotEmpty()) {
                            Text(
                                "Hourly Forecast",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                        color = OceanAccent
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(hourly) { item ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment =
                                                Alignment.CenterHorizontally
                                        ) {
                                            Text(item.dt_txt.takeLast(8))
                                            Text(
                                                "${item.main.temp.toInt()}°C",
                                                fontWeight = FontWeight.Bold
                                            )
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
}
@Composable
fun getWeatherIcon(condition: String): ImageVector {
    return when (condition.lowercase()) {
        "clear" -> Icons.Default.WbSunny
        "clouds" -> Icons.Default.Cloud
        "rain" -> Icons.Default.Umbrella
        "drizzle" -> Icons.Default.Grain
        "thunderstorm" -> Icons.Default.FlashOn
        "snow" -> Icons.Default.AcUnit
        "mist", "fog", "haze", "smoke" -> Icons.Default.BlurOn
        else -> Icons.Default.Cloud
    }
}
