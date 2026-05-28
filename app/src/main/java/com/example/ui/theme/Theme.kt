package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Blue Meadow Light Color Scheme
private val BlueLightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    background = BlueBackground,
    surface = BlueSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF0F52BA),
    onBackground = Color(0xFF1C2D42),
    onSurface = Color(0xFF1C2D42)
)

// Blue Meadow Dark Color Scheme
private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF64B5F6),
    tertiary = Color(0xFF1565C0),
    background = Color(0xFF101B2B),
    surface = Color(0xFF1B2A3E),
    onPrimary = Color(0xFF0D47A1),
    onSecondary = Color(0xFF0D47A1),
    onTertiary = Color.White,
    onBackground = Color(0xFFE3F2FD),
    onSurface = Color(0xFFE3F2FD)
)

// Forest Moss & Cappuccino Light Color Scheme
private val ForestLightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    secondary = ForestSecondary,
    tertiary = ForestTertiary,
    background = ForestBackground,
    surface = ForestSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = NaturalTextPrimary,
    onSurface = NaturalTextPrimary
)

// Forest Moss & Cappuccino Dark Color Scheme
private val ForestDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8FDCB4),
    secondary = Color(0xFFA2C773),
    tertiary = CappuccinoTone,
    background = Color(0xFF121B15),
    surface = Color(0xFF1B291F),
    onPrimary = Color(0xFF0C241B),
    onSecondary = Color(0xFF223611),
    onTertiary = Color(0xFF1A120B),
    onBackground = Color(0xFFE8F1EB),
    onSurface = Color(0xFFE8F1EB)
)

@Composable
fun MyLittlePlantTheme(
    isGreenTheme: Boolean = true, // Toggle between Forest & Blue Theme
    darkTheme: Boolean = false,   // Dark mode support
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        isGreenTheme -> {
            if (darkTheme) ForestDarkColorScheme else ForestLightColorScheme
        }
        else -> {
            if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
