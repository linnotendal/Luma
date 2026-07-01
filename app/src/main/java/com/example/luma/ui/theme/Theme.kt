package com.example.luma.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LumaColorScheme = lightColorScheme(
    primary = Sage400,
    onPrimary = Sand100,
    primaryContainer = Sage100,
    onPrimaryContainer = Sage600,
    secondary = Sage400,
    secondaryContainer = Sage100,
    onSecondaryContainer = Sage600,
    background = Sand100,
    onBackground = TextPrimary,
    surface = Sand200,
    onSurface = TextPrimary,
    surfaceVariant = Sand300,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun LumaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LumaColorScheme,
        typography = LumaTypography,
        content = content
    )
}