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

val LocalThemeFamily = staticCompositionLocalOf { ThemeFamily.Candy }

private val LightCandyColors = SweetColors(
    cream = Cream, paper = Paper, ink = Ink, line = Line,
    pink = Pink, pinkDeep = PinkDeep, purple = Purple, purpleDeep = PurpleDeep,
    mint = Mint, mintDeep = MintDeep, lemon = Lemon, lemonDeep = LemonDeep,
    choc = Choc, chocDeep = ChocDeep, muted = Color(0xFF8A7867),
    navInactive = Color(0xFFB7A493), navActiveBg = Color(0xFFFFE6EF),
    weekendBg = Color(0xFFFFF1DC), holidayBg = Color(0xFFE3F7EE),
    streakBg = Color(0xFFFFF1DC), isDark = false,
)

private val DarkCandyColors = SweetColors(
    cream = DarkCream, paper = DarkPaper, ink = DarkInk, line = DarkLine,
    pink = Color(0xFFFF8CBE), pinkDeep = Pink, purple = Color(0xFFB79BEB),
    purpleDeep = Color(0xFFD4C2FA), mint = Color(0xFF6FE0B4), mintDeep = Color(0xFF4FD9A0),
    lemon = Color(0xFFFFDB70), lemonDeep = Color(0xFFFFC93C),
    choc = Color(0xFFD8B08A), chocDeep = Color(0xFFEFD3AE),
    muted = DarkMuted, navInactive = DarkNavInactive, navActiveBg = DarkNavActiveBg,
    weekendBg = Color(0xFF3A2A22), holidayBg = Color(0xFF1E3A32),
    streakBg = Color(0xFF3A2A46), isDark = true,
)

private val LightSpaceColors = SweetColors(
    cream = SpaceCream, paper = SpacePaper, ink = SpaceInk, line = SpaceLine,
    pink = SpacePink, pinkDeep = SpacePinkDeep, purple = SpacePurple, purpleDeep = SpacePurpleDeep,
    mint = SpaceMint, mintDeep = SpaceMintDeep, lemon = SpaceLemon, lemonDeep = SpaceLemonDeep,
    choc = SpaceChoc, chocDeep = SpaceChocDeep, muted = SpaceMuted,
    navInactive = SpaceNavInactive, navActiveBg = SpaceNavActiveBg,
    weekendBg = Color(0xFFFFF1D6), holidayBg = Color(0xFFDFF7F5),
    streakBg = Color(0xFFFFF1D6), isDark = false,
)

private val DarkSpaceColors = SweetColors(
    cream = SpaceDarkCream, paper = SpaceDarkPaper, ink = SpaceDarkInk, line = SpaceDarkLine,
    pink = SpaceDarkPink, pinkDeep = SpaceDarkPinkDeep, purple = SpaceDarkPurple,
    purpleDeep = SpaceDarkPurpleDeep, mint = SpaceDarkMint, mintDeep = SpaceDarkMintDeep,
    lemon = SpaceDarkLemon, lemonDeep = SpaceDarkLemonDeep,
    choc = SpaceDarkChoc, chocDeep = SpaceDarkChocDeep, muted = SpaceDarkMuted,
    navInactive = SpaceDarkNavInactive, navActiveBg = SpaceDarkNavActiveBg,
    weekendBg = Color(0xFF2A2F72), holidayBg = Color(0xFF173A3A),
    streakBg = Color(0xFF2A2F72), isDark = true,
)

private val LightCandyScheme = lightColorScheme(
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

private val DarkCandyScheme = darkColorScheme(
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

private val LightSpaceScheme = lightColorScheme(
    primary = SpacePink,
    onPrimary = Color.White,
    primaryContainer = SpacePinkDeep,
    secondary = SpacePurple,
    onSecondary = Color.White,
    tertiary = SpaceMint,
    background = SpaceCream,
    onBackground = SpaceInk,
    surface = SpacePaper,
    onSurface = SpaceInk,
    surfaceVariant = SpaceLine,
    onSurfaceVariant = SpaceInk,
    outline = SpaceLine,
)

private val DarkSpaceScheme = darkColorScheme(
    primary = SpaceDarkPink,
    onPrimary = Color(0xFF0B0E24),
    primaryContainer = SpaceDarkPinkDeep,
    secondary = SpaceDarkPurple,
    onSecondary = Color(0xFF0B0E24),
    tertiary = SpaceDarkMint,
    background = SpaceDarkCream,
    onBackground = SpaceDarkInk,
    surface = SpaceDarkPaper,
    onSurface = SpaceDarkInk,
    surfaceVariant = SpaceDarkLine,
    onSurfaceVariant = SpaceDarkMuted,
    outline = SpaceDarkLine,
)

@Composable
fun CalendarTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeFamily: ThemeFamily = ThemeFamily.Candy,
    content: @Composable () -> Unit
) {
    val sweetColors = when (themeFamily) {
        ThemeFamily.Candy -> if (darkTheme) DarkCandyColors else LightCandyColors
        ThemeFamily.Space -> if (darkTheme) DarkSpaceColors else LightSpaceColors
    }
    val colorScheme = when (themeFamily) {
        ThemeFamily.Candy -> if (darkTheme) DarkCandyScheme else LightCandyScheme
        ThemeFamily.Space -> if (darkTheme) DarkSpaceScheme else LightSpaceScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = {
            CompositionLocalProvider(
                LocalSweetColors provides sweetColors,
                LocalThemeFamily provides themeFamily,
                content = content
            )
        }
    )
}

object SweetTheme {
    val colors: SweetColors
        @Composable get() = LocalSweetColors.current

    val family: ThemeFamily
        @Composable get() = LocalThemeFamily.current

    val isSpace: Boolean
        @Composable get() = family == ThemeFamily.Space
}
