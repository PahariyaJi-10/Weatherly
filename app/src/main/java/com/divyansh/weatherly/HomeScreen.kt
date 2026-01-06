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
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder


/* 🌦 WEATHER ICON HELPER (SAFE) */
fun getWeatherIcon(condition: String): String {
    return when {
        condition.contains("clear", true) -> "☀️"
        condition.contains("rain", true) -> "🌧"
        condition.contains("cloud", true) -> "☁️"
        condition.contains("snow", true) -> "❄️"
        condition.contains("storm", true) -> "⛈"
        else -> "🌦"
    }
}

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherState by weatherViewModel.weatherState.collectAsState()

    var query by remember { mutableStateOf("") }
// ⭐ FAVOURITES (SAFE LOCAL STORAGE)
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("weatherly_prefs", Context.MODE_PRIVATE)

    var favouriteCities by remember {
        mutableStateOf(
            prefs.getStringSet("favourite_cities", emptySet())!!.toMutableSet()
        )
    }

    fun saveFavourites() {
        prefs.edit().putStringSet("favourite_cities", favouriteCities).apply()
    }

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
// ⭐ FAVOURITES LIST
            if (favouriteCities.isNotEmpty()) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "⭐ Favourites",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                favouriteCities.forEach { city ->
                    Text(
                        text = city,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = city
                                weatherViewModel.fetchWeather(city)
                            }
                            .padding(vertical = 6.dp),
                        color = OceanAccent
                    )
                }
            }

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

                    /* 🧠 SMART WEATHER MESSAGE (SAFE) */
                    val condition = weather.weather.firstOrNull()?.description?.lowercase() ?: ""


                    val weatherMessage = when (condition) {
                        "clear" -> "☀️ Perfect day for a walk"
                        "rain", "drizzle", "thunderstorm" -> "🌧 Carry an umbrella"
                        "snow" -> "❄️ It’s cold outside, stay warm"
                        "clouds" -> "☁️ A calm and cozy day"
                        else -> "🌈 Have a great day!"
                    }
// 👕 CLOTHING SUGGESTION (SAFE & SIMPLE)
                    val clothingSuggestion = when {
                        weather.main.temp <= 10 ->
                            "🧥 Wear a jacket, it's cold!"

                        weather.weather[0].description.contains("rain", true) ->
                            "👟 Waterproof shoes recommended!"

                        weather.main.temp >= 30 ->
                            "👕 Light cotton clothes recommended!"

                        else ->
                            "🙂 Comfortable clothing is fine!"
                    }

                    Column {

                        /* 🌦 MAIN WEATHER CARD */
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

                        Spacer(modifier = Modifier.height(8.dp))

                        /* 🧠 WEATHER MESSAGE */
                        Text(
                            text = weatherMessage,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = clothingSuggestion,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        /* 💨 WIND + 💧 HUMIDITY */
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
                            IconButton(
                                onClick = {
                                    if (query.isNotBlank()) {
                                        if (favouriteCities.contains(query)) {
                                            favouriteCities.remove(query)
                                        } else {
                                            favouriteCities.add(query)
                                        }
                                        favouriteCities = favouriteCities.toMutableSet()
                                        saveFavourites()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (favouriteCities.contains(query))
                                            Icons.Default.Favorite
                                        else
                                            Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favourite city",
                                    tint = OceanAccent
                                )
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

                        /* ⏰ HOURLY FORECAST */
                        Text("Hourly Forecast", fontSize = 16.sp)

                        LazyRow {
                            items(hourly) { item ->

                                val icon = getWeatherIcon(item.condition)

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

                                        Text(icon, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(item.time, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "${item.temp}°C",
                                            color = OceanAccent,
                                            fontSize = 14.sp
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
