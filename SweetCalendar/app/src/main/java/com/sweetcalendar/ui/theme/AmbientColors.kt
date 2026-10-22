package com.sweetcalendar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Fixed washes between light and dark theme backgrounds — do not flip with dark mode.
 */
object AmbientColors {
    // Pastel washes — mockup welcome gradient (#FFE1EE → #F3E3FB → #E3F7EE), not mid-tone gray
    val CandyWelcome = Color(0xFFFFE8F2)
    val CandyWelcomeAltA = Color(0xFFF3E8FB)
    val CandyWelcomeAltB = Color(0xFFE5F7EE)
    val CandyHolidayHero = Color(0xFF7A8F8A)
    val CandyJarFill = Color(0xFFBC8FB0)
    val CandyProfileCard = Color(0xFFAD6FA6)

    val SpaceWelcome = Color(0xFFEAF0FF)
    val SpaceHolidayHero = Color(0xFF6B7A8F)
    val SpaceJarFill = Color(0xFFA88FBC)
    val SpaceProfileCard = Color(0xFF9578C8)
}

@Composable
fun ambientWelcomeColor(slideIndex: Int = 0): Color {
    if (SweetTheme.isSpace) return AmbientColors.SpaceWelcome
    return when (slideIndex) {
        1 -> AmbientColors.CandyWelcomeAltA
        2 -> AmbientColors.CandyWelcomeAltB
        else -> AmbientColors.CandyWelcome
    }
}

/** Same pastel wash as welcome slide 0 (notifications prompt follows intro look). */
@Composable
fun ambientPermissionBackground(): Color = ambientWelcomeColor(slideIndex = 0)

/** Readable on fixed pastel washes — does not use light [SweetColors.ink] in dark app theme. */
@Composable
fun ambientPastelInk(): Color = if (SweetTheme.isSpace) SpaceInk else Ink

@Composable
fun ambientPastelMuted(): Color = if (SweetTheme.isSpace) SpaceChocDeep else ChocDeep

@Composable
fun ambientPastelLink(): Color = if (SweetTheme.isSpace) SpacePurpleDeep else PurpleDeep

@Composable
fun ambientPastelAccentPink(): Color = if (SweetTheme.isSpace) SpacePinkText else PinkText

@Composable
fun ambientHolidayHeroBackground(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceHolidayHero else AmbientColors.CandyHolidayHero

@Composable
fun ambientJarFillColor(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceJarFill else AmbientColors.CandyJarFill

@Composable
fun ambientProfileCardColor(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceProfileCard else AmbientColors.CandyProfileCard
