package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.lightColorScheme

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF03101C),
    primaryContainer = Color(0xFF063348),
    onPrimaryContainer = CyberCyan,
    secondary = CyberMagenta,
    onSecondary = Color(0xFF280013),
    secondaryContainer = Color(0xFF4D0027),
    onSecondaryContainer = Color(0xFFFFB2D1),
    tertiary = CyberGreen,
    onTertiary = Color(0xFF00220B),
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCyan.copy(alpha = 0.5f),
    outlineVariant = CyberBorder
)

private val CyberLightColorScheme = lightColorScheme(
    primary = LightCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = LightMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = LightGreen,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF94A3B8),
    outlineVariant = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) CyberDarkColorScheme else CyberLightColorScheme,
        typography = Typography,
        content = content
    )
}
