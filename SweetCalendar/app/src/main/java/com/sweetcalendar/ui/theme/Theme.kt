package com.sweetcalendar.ui.theme

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
    /** Slightly raised surface (dark mode chips, grouped rows). */
    val surfaceElevated: Color,
    val ink: Color,
    /** Borders and dividers. */
    val line: Color,
    /** Pixel-art offset shadow (face uses [paper]). */
    val shadow: Color,
    val shadowDeep: Color,
    /** Streak header, offline banner (warm panel). */
    val bannerBg: Color,
    /** Completed-task tray / soft inset panels. */
    val surfaceInset: Color,
    val pink: Color,
    val pinkDeep: Color,
    /** Pink for small text/thin icons; defaults to [pinkDeep] where that already has enough contrast. */
    val pinkText: Color,
    /** Text and icons on [pink] / [pinkDeep] button fills. */
    val onPink: Color,
    /** Form fields and other outlines that must read clearly on [cream]. */
    val fieldOutline: Color,
    /** Affirmative actions (SAVE, OK) — mint success; kept away from error-like pink-red. */
    val confirmAccent: Color,
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
        cream = Cream, paper = Paper, surfaceElevated = Paper, ink = Ink, line = Line,
        shadow = CandyShadow, shadowDeep = CandyShadowDeep,
        bannerBg = CandyBannerBgLight, surfaceInset = CandySurfaceInsetLight,
        pink = Pink, pinkDeep = PinkDeep,
        pinkText = PinkText, onPink = Ink, fieldOutline = CandyFieldOutline,
        confirmAccent = MintDeep,
        purple = Purple, purpleDeep = PurpleDeep,
        mint = Mint, mintDeep = MintDeep, lemon = Lemon, lemonDeep = LemonDeep,
        choc = Choc, chocDeep = ChocDeep, muted = Choc,
        navInactive = Color(0xFF86705E), navActiveBg = Color(0xFFFFE6EF),
        weekendBg = Color(0xFFEAD3C4), holidayBg = Color(0xFFD7EEDF),
        streakBg = Color(0xFFEAD3C4), isDark = false,
    )
}

val LocalThemeFamily = staticCompositionLocalOf { ThemeFamily.Candy }

private val LightCandyColors = SweetColors(
    cream = Cream, paper = Paper, surfaceElevated = Paper, ink = Ink, line = Line,
    shadow = CandyShadow, shadowDeep = CandyShadowDeep,
    bannerBg = CandyBannerBgLight, surfaceInset = CandySurfaceInsetLight,
    pink = Pink, pinkDeep = PinkDeep,
    pinkText = PinkText, onPink = Ink, fieldOutline = CandyFieldOutline,
    confirmAccent = MintDeep,
    purple = Purple, purpleDeep = PurpleDeep,
    mint = Mint, mintDeep = MintDeep, lemon = Lemon, lemonDeep = LemonDeep,
    choc = Choc, chocDeep = ChocDeep, muted = Choc,
    navInactive = Color(0xFF86705E), navActiveBg = Color(0xFFFFE6EF),
    weekendBg = Color(0xFFEAD3C4), holidayBg = Color(0xFFD7EEDF),
    streakBg = Color(0xFFEAD3C4), isDark = false,
)

private val DarkCandyColors = SweetColors(
    cream = DarkCream, paper = DarkPaper, surfaceElevated = DarkPaperElevated,
    ink = DarkInk, line = DarkLine,
    shadow = DarkShadow, shadowDeep = DarkShadowDeep,
    bannerBg = CandyBannerBgDark, surfaceInset = CandyBannerBgDark,
    // Accents stay candy-bright; neutrals carry depth (mockup `body.dark`).
    pink = Color(0xFFFF8CBE), pinkDeep = Pink,
    pinkText = Pink, onPink = DarkInk, fieldOutline = DarkLine,
    confirmAccent = Color(0xFF6FE8B8),
    purple = Color(0xFFB79BEB),
    purpleDeep = Color(0xFFD4C2FA), mint = Color(0xFF6FE0B4), mintDeep = Color(0xFF4FD9A0),
    lemon = Color(0xFFFFDB70), lemonDeep = Color(0xFFFFC93C),
    choc = Color(0xFFD8B08A), chocDeep = Color(0xFFEFD3AE),
    muted = DarkMuted, navInactive = DarkNavInactive, navActiveBg = DarkNavActiveBg,
    weekendBg = Color(0xFF4A312B), holidayBg = Color(0xFF1E3A32),
    streakBg = Color(0xFF3A2A46), isDark = true,
)

private val LightSpaceColors = SweetColors(
    cream = SpaceCream, paper = SpacePaper, surfaceElevated = SpacePaper,
    ink = SpaceInk, line = SpaceLine,
    shadow = SpaceShadow, shadowDeep = SpaceShadowDeep,
    bannerBg = Color(0xFFE7D5C5), surfaceInset = Color(0xFFF1E9FB),
    pink = SpacePink, pinkDeep = SpacePinkDeep,
    pinkText = SpacePinkText, onPink = SpaceInk, fieldOutline = SpaceFieldOutline,
    confirmAccent = SpaceMintDeep,
    purple = SpacePurple, purpleDeep = SpacePurpleDeep,
    mint = SpaceMint, mintDeep = SpaceMintDeep, lemon = SpaceLemon, lemonDeep = SpaceLemonDeep,
    choc = SpaceChoc, chocDeep = SpaceChocDeep, muted = SpaceChocDeep,
    navInactive = SpaceNavInactive, navActiveBg = SpaceNavActiveBg,
    weekendBg = Color(0xFFE7D5C5), holidayBg = Color(0xFFD6EEDC),
    streakBg = Color(0xFFE7D5C5), isDark = false,
)

private val DarkSpaceColors = SweetColors(
    cream = SpaceDarkCream, paper = SpaceDarkPaper, surfaceElevated = SpaceDarkPaperElevated,
    ink = SpaceDarkInk, line = SpaceDarkLine,
    shadow = SpaceDarkShadow, shadowDeep = SpaceDarkShadowDeep,
    bannerBg = Color(0xFF433B62), surfaceInset = Color(0xFF3A2C4C),
    pink = SpaceDarkPink, pinkDeep = SpaceDarkPinkDeep,
    pinkText = Color(0xFFFF9FD4), onPink = SpaceDarkInk, fieldOutline = SpaceDarkLine,
    confirmAccent = SpaceDarkMintDeep,
    purple = SpaceDarkPurple,
    purpleDeep = SpaceDarkPurpleDeep, mint = SpaceDarkMint, mintDeep = SpaceDarkMintDeep,
    lemon = SpaceDarkLemon, lemonDeep = SpaceDarkLemonDeep,
    choc = SpaceDarkChoc, chocDeep = SpaceDarkChocDeep, muted = SpaceDarkMuted,
    navInactive = SpaceDarkNavInactive, navActiveBg = SpaceDarkNavActiveBg,
    weekendBg = Color(0xFF433B62), holidayBg = Color(0xFF163A3A),
    streakBg = Color(0xFF2A2F72), isDark = true,
)

private val LightCandyScheme = lightColorScheme(
    primary = Pink,
    onPrimary = Ink,
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
    surfaceVariant = DarkPaperElevated,
    onSurfaceVariant = DarkMuted,
    outline = DarkLine,
)

private val LightSpaceScheme = lightColorScheme(
    primary = SpacePink,
    onPrimary = SpaceInk,
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
    surfaceVariant = SpaceDarkPaperElevated,
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

fun resolveSweetColors(themeFamily: ThemeFamily, darkTheme: Boolean): SweetColors =
    when (themeFamily) {
        ThemeFamily.Candy -> if (darkTheme) DarkCandyColors else LightCandyColors
        ThemeFamily.Space -> if (darkTheme) DarkSpaceColors else LightSpaceColors
    }
