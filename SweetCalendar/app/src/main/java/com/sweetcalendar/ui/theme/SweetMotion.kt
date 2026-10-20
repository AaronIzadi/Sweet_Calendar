package com.sweetcalendar.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

object SweetMotion {
    const val ShortMillis = 220
    const val MediumMillis = 360
    const val LongMillis = 480

    fun <T> shortTween(): FiniteAnimationSpec<T> = tween(ShortMillis, easing = FastOutSlowInEasing)

    fun <T> mediumTween(): FiniteAnimationSpec<T> = tween(MediumMillis, easing = FastOutSlowInEasing)

    fun <T> longTween(): FiniteAnimationSpec<T> = tween(LongMillis, easing = FastOutSlowInEasing)
}

@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        }.getOrDefault(false)
    }
}
