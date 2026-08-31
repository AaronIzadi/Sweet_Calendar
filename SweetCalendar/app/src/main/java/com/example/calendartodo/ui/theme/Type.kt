package com.example.calendartodo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.calendartodo.R

val PixelFont = FontFamily(Font(R.font.press_start_2p))

val AppTypography = Typography(
    headlineMedium = TextStyle(fontFamily = PixelFont, fontSize = 20.sp, lineHeight = 26.sp),
    headlineSmall = TextStyle(fontFamily = PixelFont, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontFamily = PixelFont, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = PixelFont, fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = PixelFont, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = PixelFont, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = PixelFont, fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = PixelFont, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = PixelFont, fontSize = 11.sp, lineHeight = 14.sp)
)
