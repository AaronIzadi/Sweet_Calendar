package com.sweetcalendar.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    var animate by remember { mutableStateOf(reducedMotion) }
    LaunchedEffect(target) {
        if (!reducedMotion) animate = true
    }
    val value by animateIntAsState(
        targetValue = if (animate) target.coerceIn(0, 100) else target,
        animationSpec = tween(SweetMotion.LongMillis + 120),
        label = "countUpPct",
    )
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
    var animate by remember { mutableStateOf(reducedMotion) }
    LaunchedEffect(target) {
        if (!reducedMotion) animate = true
    }
    val value by animateIntAsState(
        targetValue = if (animate) target.coerceAtLeast(0) else target,
        animationSpec = tween(SweetMotion.LongMillis),
        label = "countUpInt",
    )
    Text(
        text = "$prefix$value$suffix",
        style = style,
        color = color,
        modifier = modifier,
    )
}
