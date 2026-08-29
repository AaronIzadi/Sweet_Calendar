package com.example.calendartodo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CandyColorScheme = lightColorScheme(
    primary = BubblegumPink,
    onPrimary = CreamFrosting,
    primaryContainer = CottonCandyPink,
    onPrimaryContainer = ChocolateBrown,
    secondary = GrapePurple,
    onSecondary = CreamFrosting,
    secondaryContainer = LemonYellow,
    onSecondaryContainer = ChocolateBrown,
    tertiary = MintGreen,
    onTertiary = ChocolateBrown,
    tertiaryContainer = SkyBlue,
    onTertiaryContainer = ChocolateBrown,
    background = CreamFrosting,
    onBackground = ChocolateBrown,
    surface = CreamFrosting,
    onSurface = ChocolateBrown,
    surfaceVariant = CottonCandyPink,
    onSurfaceVariant = ChocolateBrown,
    error = CherryRed,
    onError = CreamFrosting,
    outline = PixelBorder
)

@Composable
fun CalendarTodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CandyColorScheme,
        typography = AppTypography,
        content = content
    )
}
