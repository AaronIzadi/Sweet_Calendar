package com.example.calendartodo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SelectedOnAccentDark = Color(0xFF1B1424)

/** Text on purple/mint accent fills — white in light, dark ink in dark mode. */
@Composable
fun selectedOnAccentTextColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) SelectedOnAccentDark else Color.White
}
