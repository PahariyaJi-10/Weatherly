package com.divyansh.weatherly.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WeatherlyColorScheme = lightColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    tertiary = OceanAccent,
    background = OceanBackground,
    surface = OceanCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = OceanPrimary,
    onSurface = OceanPrimary
)

@Composable
fun WeatherlyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WeatherlyColorScheme,
        typography = Typography(),
        content = content
    )
}
