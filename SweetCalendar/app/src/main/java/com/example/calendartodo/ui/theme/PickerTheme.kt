package com.example.calendartodo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SelectedOnAccentCandyDark = Color(0xFF1B1424)
private val SelectedOnAccentSpaceDark = Color(0xFF0B0E24)

/** Text on purple/mint accent fills — white in light, dark ink in dark mode. */
@Composable
fun selectedOnAccentTextColor(): Color {
    val colors = SweetTheme.colors
    if (!colors.isDark) return Color.White
    return if (SweetTheme.isSpace) SelectedOnAccentSpaceDark else SelectedOnAccentCandyDark
}
