package com.divyansh.weatherly

import androidx.compose.foundation.layout.*
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
    onToggleTheme: () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()
    var query by remember { mutableStateOf("") }

    // ✅ THIS FIXES WHITE BACKGROUND
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            /* 🔍 SEARCH BAR + 🌙 TOGGLE */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Search city",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            weatherViewModel.fetchWeather(query)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OceanAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
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
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = data.name,
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
