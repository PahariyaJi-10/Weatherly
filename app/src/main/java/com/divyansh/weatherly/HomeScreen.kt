package com.divyansh.weatherly

import androidx.compose.foundation.layout.*
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
    val state = weatherViewModel.weatherState.collectAsState().value

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
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = {
                    weatherViewModel.fetchWeather(city)
                }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OceanAccent,
                unfocusedBorderColor = OceanSecondary
            )
        )

        when (state) {

            is WeatherUiState.Idle -> {
                Text(
                    text = "Search for a city 🌍",
                    color = OceanPrimary
                )
            }

            is WeatherUiState.Loading -> {
                CircularProgressIndicator(
                    color = OceanAccent,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is WeatherUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                val data = state.data

                // 🌦 Main Weather Card
                MainWeatherCard(
                    city = data.name,
                    temp = "${data.main.temp.toInt()}°C",
                    desc = data.weather[0].description
                )

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

/* ---------------- COMPONENTS ---------------- */

@Composable
fun MainWeatherCard(
    city: String,
    temp: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = OceanCard)
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

            Text(city, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(temp, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
            Text(
                desc,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
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
            Icon(icon, contentDescription = null, tint = OceanAccent)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
