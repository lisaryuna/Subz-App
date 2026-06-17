package com.example.subz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OceanBreezeColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryLightBlue,
    background = BackgroundLight,
    surface = Color.White,
    onBackground = TextDarkNavy,
    onSurface = TextDarkNavy,
    error = AccentCoral,
    onError = Color.White
)

@Composable
fun SubzTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OceanBreezeColorScheme,
        typography = Typography,
        content = content
    )
}