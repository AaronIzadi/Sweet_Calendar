package com.example.calendartodo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Mockup phone content width in [sweet_calendar_mockups.html]. */
const val MOCKUP_REFERENCE_WIDTH_DP = 360f

/**
 * Scales dp/sp so a 360dp-wide mockup fills the same *proportion* of the screen on any device.
 * On a 360dp phone → 1×. On a 900dp emulator → 2.5×.
 */
val LocalMockupScale = staticCompositionLocalOf { 1f }

@Composable
fun ProvideMockupScale(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val scale = (configuration.screenWidthDp / MOCKUP_REFERENCE_WIDTH_DP).coerceAtLeast(1f)
    val baseDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalMockupScale provides scale,
        LocalDensity provides Density(
            density = baseDensity.density * scale,
            fontScale = baseDensity.fontScale
        )
    ) {
        content()
    }
}

/** Convert a mockup CSS pixel value to dp (scaled via [ProvideMockupScale] density). */
@Composable
fun mockupDp(px: Int): Dp = px.dp

@Composable
fun mockupDp(px: Float): Dp = px.dp

/** Convert a mockup CSS pixel value to sp (scaled via [ProvideMockupScale] density). */
@Composable
fun mockupSp(px: Int): TextUnit = px.sp

@Composable
fun mockupSp(px: Float): TextUnit = px.sp
