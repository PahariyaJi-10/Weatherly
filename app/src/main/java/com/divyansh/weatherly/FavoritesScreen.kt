package com.divyansh.weatherly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoritesScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    onCitySelected: (String) -> Unit
) {
    val favorites by weatherViewModel.favorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Favorites",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites added yet")
            }
        } else {
            LazyColumn {
                items(favorites.toList()) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                onCitySelected(city)
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
    }
}
