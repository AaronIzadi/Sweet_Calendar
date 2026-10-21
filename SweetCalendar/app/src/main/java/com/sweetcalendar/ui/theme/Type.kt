package com.sweetcalendar.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sweetcalendar.R

val PixelFont = FontFamily(Font(R.font.press_start_2p))

private val QuicksandFont = FontFamily(
    Font(R.font.quicksand_variable, weight = FontWeight.Medium),
    Font(R.font.quicksand_variable, weight = FontWeight.SemiBold),
    Font(R.font.quicksand_variable, weight = FontWeight.Bold),
    Font(R.font.quicksand_variable, weight = FontWeight.ExtraBold),
)

// Variable fonts are unreliable below API 26 (common on older emulators).
val BodyFont: FontFamily =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) QuicksandFont else FontFamily.SansSerif

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 26.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(fontFamily = PixelFont, fontSize = 12.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = PixelFont, fontSize = 11.sp, lineHeight = 17.sp),
    labelSmall = TextStyle(fontFamily = PixelFont, fontSize = 10.sp, lineHeight = 15.sp),
)
