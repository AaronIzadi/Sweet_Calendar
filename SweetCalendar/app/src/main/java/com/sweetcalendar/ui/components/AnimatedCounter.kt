package com.sweetcalendar.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.sweetcalendar.ui.motion.rememberCountUp
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.rememberReducedMotion

@Composable
fun CountUpPercentText(
    target: Int,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    suffix: String = "%",
) {
    val reducedMotion = rememberReducedMotion()
    val value = if (reducedMotion) {
        target.coerceIn(0, 100)
    } else {
        rememberCountUp(target.coerceIn(0, 100), durationMillis = SweetMotion.LongMillis + 120)
    }
    Text(
        text = "$value$suffix",
        style = style,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun CountUpIntText(
    target: Int,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
) {
    val reducedMotion = rememberReducedMotion()
    val value = if (reducedMotion) {
        target.coerceAtLeast(0)
    } else {
        rememberCountUp(target.coerceAtLeast(0), durationMillis = SweetMotion.LongMillis)
    }
    Text(
        text = "$prefix$value$suffix",
        style = style,
        color = color,
        modifier = modifier,
    )
}
