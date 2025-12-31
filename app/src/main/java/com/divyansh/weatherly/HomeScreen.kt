package com.divyansh.weatherly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.divyansh.weatherly.ui.theme.OceanAccent

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()
    val favorites by weatherViewModel.favorites.collectAsState()

    var query by remember { mutableStateOf("") }
    var showFavorites by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* 🔍 SEARCH + 🌙 THEME + ❤️ FAVORITES */
        Row(verticalAlignment = Alignment.CenterVertically) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search city") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, null)
                },
                trailingIcon = {
                    IconButton(onClick = { weatherViewModel.fetchWeather(query) }) {
                        Icon(Icons.Default.Search, null)
                    }
                }
            )

            IconButton(onClick = onToggleTheme) {
                Icon(
                    if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }

            IconButton(onClick = { showFavorites = !showFavorites }) {
                Icon(Icons.Default.Favorite, null)
            }
        }

        Spacer(Modifier.height(20.dp))

        /* ❤️ FAVORITES LIST */
        if (showFavorites) {
            if (favorites.isEmpty()) {
                Text("No favorites yet")
            } else {
                LazyColumn {
                    items(favorites.toList()) { city ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    query = city
                                    showFavorites = false
                                    weatherViewModel.fetchWeather(city)
                                }
                        ) {
                            Text(
                                text = city,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
            return
        }

        /* 🌦 WEATHER RESULT */
        when (weatherState) {

            WeatherUiState.Idle -> {
                Text("Search a city to see weather")
            }

            WeatherUiState.Loading -> {
                CircularProgressIndicator()
            }

            is WeatherUiState.Error -> {
                Text(
                    text = (weatherState as WeatherUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                val data = (weatherState as WeatherUiState.Success).data
                val isFav = favorites.contains(data.name)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(data.name, fontSize = 20.sp)

                            IconButton(
                                onClick = {
                                    weatherViewModel.toggleFavorite(data.name)
                                }
                            ) {
                                Icon(
                                    if (isFav)
                                        Icons.Default.Favorite
                                    else
                                        Icons.Default.FavoriteBorder,
                                    null,
                                    tint = OceanAccent
                                )
                            }
                        }

                        Text(
                            "${data.main.temp.toInt()}°C",
                            fontSize = 32.sp,
                            color = OceanAccent
                        )

                        Text(data.weather[0].description)
                    }
                }
            }
        }
    }
}
