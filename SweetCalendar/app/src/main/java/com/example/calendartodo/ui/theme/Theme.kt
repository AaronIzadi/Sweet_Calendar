package com.example.calendartodo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SweetColors(
    val cream: Color,
    val paper: Color,
    val ink: Color,
    val line: Color,
    val pink: Color,
    val pinkDeep: Color,
    val purple: Color,
    val purpleDeep: Color,
    val mint: Color,
    val mintDeep: Color,
    val lemon: Color,
    val lemonDeep: Color,
    val choc: Color,
    val chocDeep: Color,
    val muted: Color,
    val navInactive: Color,
    val navActiveBg: Color,
    val weekendBg: Color,
    val holidayBg: Color,
    val streakBg: Color,
    val isDark: Boolean,
)

val LocalSweetColors = staticCompositionLocalOf {
    SweetColors(
        cream = Cream, paper = Paper, ink = Ink, line = Line,
        pink = Pink, pinkDeep = PinkDeep, purple = Purple, purpleDeep = PurpleDeep,
        mint = Mint, mintDeep = MintDeep, lemon = Lemon, lemonDeep = LemonDeep,
        choc = Choc, chocDeep = ChocDeep, muted = Color(0xFF8A7867),
        navInactive = Color(0xFFB7A493), navActiveBg = Color(0xFFFFE6EF),
        weekendBg = Color(0xFFFFF1DC), holidayBg = Color(0xFFE3F7EE),
        streakBg = Color(0xFFFFF1DC), isDark = false,
    )
}

private val LightSweetColors = SweetColors(
    cream = Cream, paper = Paper, ink = Ink, line = Line,
    pink = Pink, pinkDeep = PinkDeep, purple = Purple, purpleDeep = PurpleDeep,
    mint = Mint, mintDeep = MintDeep, lemon = Lemon, lemonDeep = LemonDeep,
    choc = Choc, chocDeep = ChocDeep, muted = Color(0xFF8A7867),
    navInactive = Color(0xFFB7A493), navActiveBg = Color(0xFFFFE6EF),
    weekendBg = Color(0xFFFFF1DC), holidayBg = Color(0xFFE3F7EE),
    streakBg = Color(0xFFFFF1DC), isDark = false,
)

private val DarkSweetColors = SweetColors(
    cream = DarkCream, paper = DarkPaper, ink = DarkInk, line = DarkLine,
    pink = Color(0xFFFF8CBE), pinkDeep = Pink, purple = Color(0xFFB79BEB),
    purpleDeep = Color(0xFFD4C2FA), mint = Color(0xFF6FE0B4), mintDeep = Color(0xFF4FD9A0),
    lemon = Color(0xFFFFDB70), lemonDeep = Color(0xFFFFC93C),
    choc = Color(0xFFD8B08A), chocDeep = Color(0xFFEFD3AE),
    muted = DarkMuted, navInactive = DarkNavInactive, navActiveBg = DarkNavActiveBg,
    weekendBg = Color(0xFF3A2A22), holidayBg = Color(0xFF1E3A32),
    streakBg = Color(0xFF3A2A46), isDark = true,
)

private val LightScheme = lightColorScheme(
    primary = Pink,
    onPrimary = Color.White,
    primaryContainer = PinkDeep,
    secondary = Purple,
    onSecondary = Color.White,
    tertiary = Mint,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Line,
    onSurfaceVariant = Ink,
    outline = Line,
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFFF8CBE),
    onPrimary = Color(0xFF1B1424),
    primaryContainer = PinkDeep,
    secondary = Color(0xFFB79BEB),
    onSecondary = Color(0xFF1B1424),
    tertiary = Color(0xFF6FE0B4),
    background = DarkCream,
    onBackground = DarkInk,
    surface = DarkPaper,
    onSurface = DarkInk,
    surfaceVariant = DarkLine,
    onSurfaceVariant = DarkMuted,
    outline = DarkLine,
)

@Composable
fun CalendarTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val sweetColors = if (darkTheme) DarkSweetColors else LightSweetColors
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        content = {
            CompositionLocalProvider(LocalSweetColors provides sweetColors, content = content)
        }
    )
}

object SweetTheme {
    val colors: SweetColors
        @Composable get() = LocalSweetColors.current
}
