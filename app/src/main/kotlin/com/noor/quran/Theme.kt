package com.noor.quran

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFF8F5E9),
    primary = Color(0xFFF8F5E9),
    secondary = Color(0xFF888888)
)

@Composable
fun NoorQuranTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
