package com.example.calendartodo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.calendartodo.R

val PixelFont = FontFamily(Font(R.font.press_start_2p))

val AppTypography = Typography(
    titleLarge = TextStyle(fontFamily = PixelFont, fontSize = 14.sp, lineHeight = 20.sp),
    titleMedium = TextStyle(fontFamily = PixelFont, fontSize = 11.sp, lineHeight = 16.sp),
    titleSmall = TextStyle(fontFamily = PixelFont, fontSize = 9.sp, lineHeight = 14.sp),
    bodyLarge = TextStyle(fontFamily = PixelFont, fontSize = 10.sp, lineHeight = 16.sp),
    bodyMedium = TextStyle(fontFamily = PixelFont, fontSize = 8.sp, lineHeight = 14.sp),
    bodySmall = TextStyle(fontFamily = PixelFont, fontSize = 7.sp, lineHeight = 12.sp),
    labelLarge = TextStyle(fontFamily = PixelFont, fontSize = 9.sp, lineHeight = 14.sp),
    labelMedium = TextStyle(fontFamily = PixelFont, fontSize = 8.sp, lineHeight = 12.sp),
    labelSmall = TextStyle(fontFamily = PixelFont, fontSize = 7.sp, lineHeight = 10.sp)
)
