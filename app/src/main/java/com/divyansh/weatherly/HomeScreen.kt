package com.divyansh.weatherly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    onToggleTheme: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val state by weatherViewModel.weatherState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        /* 🔍 SEARCH + 🌙 TOGGLE */
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search city") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        weatherViewModel.fetchWeather(query)
                    }
                )
            )


            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector =
                        if (isDarkMode) Icons.Default.LightMode
                        else Icons.Default.DarkMode,
                    contentDescription = "Toggle theme"
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        when (state) {

            WeatherUiState.Loading -> {
                CircularProgressIndicator()
            }

            is WeatherUiState.Error -> {
                Text(
                    (state as WeatherUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is WeatherUiState.Success -> {
                val data = (state as WeatherUiState.Success)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(data.data.name, fontSize = 20.sp)

                            IconButton(
                                onClick = {
                                    weatherViewModel.toggleFavorite(
                                        data.data.name
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (data.isFavorite)
                                            Icons.Default.Favorite
                                        else
                                            Icons.Default.FavoriteBorder,
                                    tint = OceanAccent,
                                    contentDescription = null
                                )
                            }
                        }

                        Text(
                            "${data.data.main.temp.toInt()}°C",
                            fontSize = 34.sp,
                            color = OceanAccent
                        )

                        Text(data.data.weather[0].description)
                    }
                }
            }

            WeatherUiState.Idle -> {
                Text("Search a city to see weather")
            }
        }
    }
}

