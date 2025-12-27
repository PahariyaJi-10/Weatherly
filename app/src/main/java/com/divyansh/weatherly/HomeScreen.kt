package com.divyansh.weatherly

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.divyansh.weatherly.ui.theme.*

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    var city by remember { mutableStateOf("") }

    val weatherState = weatherViewModel.weatherState.collectAsState().value
    val hourly = weatherViewModel.hourlyForecast.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 🔍 Search Bar
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search city") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                IconButton(
                    onClick = { weatherViewModel.fetchWeather(city) },
                    modifier = Modifier.size(48.dp)
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
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // 🌦 Main Weather Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(26.dp),
                            elevation = CardDefaults.cardElevation(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = OceanCard
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = null,
                                    tint = OceanAccent,
                                    modifier = Modifier.size(72.dp)
                                )

                                Text(
                                    text = data.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${data.main.temp.toInt()}°C",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = data.weather[0].description,
                                    color = MaterialTheme.colorScheme
                                        .onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // ⏱ Hourly Forecast
                        if (hourly.isNotEmpty()) {

                            Text(
                                text = "Hourly Forecast",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(hourly) { item ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = OceanCard
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
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

                        // 📊 Info Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCard(
                                title = "Humidity",
                                value = "${data.main.humidity}%",
                                icon = Icons.Default.WaterDrop,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                title = "Wind",
                                value = "${data.wind.speed} km/h",
                                icon = Icons.Default.Air,
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                title = "Feels Like",
                                value = "${data.main.temp.toInt()}°C",
                                icon = Icons.Default.Cloud,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = OceanCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = OceanAccent)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
