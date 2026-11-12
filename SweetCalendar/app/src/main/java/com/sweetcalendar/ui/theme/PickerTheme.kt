package com.sweetcalendar.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

private val SelectedOnAccentCandyDark = Color(0xFF1B1424)
private val SelectedOnAccentSpaceDark = Color(0xFF0B0E24)

/** Selected priority / category / period chips (light & dark themes). */
private val FormChipFillLight = Color(0xFFFFF0B0)
private val FormChipFillDark = Color(0xFFFFF0B8)
private val FormChipShadowLight = Color(0xFFFFD966)
private val FormChipShadowDark = Color(0xFFFFD966)
private val FormChipTextSelected = Color.Black

/** Text on purple/mint accent fills — white in light, dark ink in dark mode. */
@Composable
fun selectedOnAccentTextColor(): Color {
    val colors = SweetTheme.colors
    if (!colors.isDark) return Color.White
    return if (SweetTheme.isSpace) SelectedOnAccentSpaceDark else SelectedOnAccentCandyDark
}

@Composable
fun formChipFillColor(selected: Boolean): Color {
    if (!selected) return SweetTheme.colors.paper
    val colors = SweetTheme.colors
    return if (colors.isDark) FormChipFillDark else FormChipFillLight
}

@Composable
fun formChipShadowColor(selected: Boolean): Color {
    if (!selected) return SweetTheme.colors.shadow
    return if (SweetTheme.colors.isDark) FormChipShadowDark else FormChipShadowLight
}

@Composable
fun formChipTextColor(selected: Boolean): Color =
    if (selected) FormChipTextSelected else SweetTheme.colors.muted

@Composable
fun formPlaceholderColor(): Color {
    val colors = SweetTheme.colors
    val base = if (colors.isDark) colors.navInactive else colors.muted
    return base.copy(alpha = if (colors.isDark) 0.55f else 0.45f)
}

/** Drop-shadow layer under form chips — shaped fill so corners match the button face. */
fun Modifier.formChipShadowLayer(color: Color, cornerRadius: Dp): Modifier =
    background(color, RoundedCornerShape(cornerRadius))
