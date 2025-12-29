package com.divyansh.weatherly.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    tertiary = OceanAccent,
    background = OceanDarkBackground,
    surface = OceanDarkCard,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    tertiary = OceanAccent,
    background = OceanBackground,
    surface = OceanCard,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun WeatherlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    val context = LocalContext.current

    // ✅ Safe system bar handling
    if (!view.isInEditMode && context is Activity) {
        WindowCompat.getInsetsController(context.window, view)
            .isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
