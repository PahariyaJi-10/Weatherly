package com.divyansh.weatherly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.divyansh.weatherly.ui.theme.OceanAccent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // 🔥 FIX WHITE SCREEN
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        weatherViewModel.fetchWeather(query)
                    }
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

        /* 🌦 WEATHER CONTENT */
        when (weatherState) {

            WeatherUiState.Idle -> {
                Text(
                    "Search a city to see weather",
                    color = MaterialTheme.colorScheme.onBackground
                )
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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = OceanAccent,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = data.name, // ✅ CITY NAME FIXED
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${data.main.temp.toInt()}°C",
                            fontSize = 32.sp,
                            color = OceanAccent
                        )

                        Text(
                            text = data.weather[0].description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

