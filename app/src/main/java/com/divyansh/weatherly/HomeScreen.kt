package com.divyansh.weatherly

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

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search city") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                IconButton(onClick = {
                    weatherViewModel.fetchWeather(city)
                }) {
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
                CircularProgressIndicator()
            }

            is WeatherUiState.Error -> {
                Text(
                    weatherState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                val data = weatherState.data

                Text(
                    "${data.name}  ${data.main.temp.toInt()}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                if (hourly.isNotEmpty()) {
                    Text("Hourly Forecast")

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(hourly) {
                            Card {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(it.dt_txt.takeLast(8))
                                    Text("${it.main.temp.toInt()}°C")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
