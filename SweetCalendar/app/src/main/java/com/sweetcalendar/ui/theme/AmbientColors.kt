package com.sweetcalendar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Fixed washes between light and dark theme backgrounds — do not flip with dark mode.
 */
object AmbientColors {
    val CandyWelcome = Color(0xFF8A7D96)
    val CandyWelcomeAltA = Color(0xFF8B858F)
    val CandyWelcomeAltB = Color(0xFF8A8090)
    val CandyPermission = Color(0xFF8A7D96)
    val CandyHolidayHero = Color(0xFF7A8F8A)
    val CandyJarFill = Color(0xFFBC8FB0)
    val CandyProfileCard = Color(0xFFAD6FA6)

    val SpaceWelcome = Color(0xFF7278A0)
    val SpacePermission = Color(0xFF7278A0)
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

@Composable
fun ambientPermissionBackground(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpacePermission else AmbientColors.CandyPermission

@Composable
fun ambientHolidayHeroBackground(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceHolidayHero else AmbientColors.CandyHolidayHero

@Composable
fun ambientJarFillColor(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceJarFill else AmbientColors.CandyJarFill

@Composable
fun ambientProfileCardColor(): Color =
    if (SweetTheme.isSpace) AmbientColors.SpaceProfileCard else AmbientColors.CandyProfileCard
