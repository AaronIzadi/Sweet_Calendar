package com.sweetcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.theme.BubblegumPink
import com.sweetcalendar.ui.theme.LemonYellow
import com.sweetcalendar.ui.theme.SprinklesBlue
import com.sweetcalendar.ui.theme.SprinklesGreen
import com.sweetcalendar.ui.theme.SprinklesRed
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class StarSeed(val xFrac: Float, val yFrac: Float, val phase: Float, val size: Float)

@Composable
fun SpaceTwinkleBackground(modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val reducedMotion = rememberReducedMotion()
    val starColors = listOf(colors.pink, colors.purple, colors.mint, colors.lemon, colors.purpleDeep)
    val stars = remember {
        buildList {
            val rows = 28
            val cols = 14
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    if ((row + col) % 3 == 0) continue
                    val xFrac = (col + if (row % 2 == 0) 0f else 0.5f) / cols
                    val yFrac = row / rows.toFloat()
                    add(
                        StarSeed(
                            xFrac = xFrac.coerceIn(0.02f, 0.98f),
                            yFrac = yFrac.coerceIn(0.02f, 0.98f),
                            phase = (row * 0.7f + col * 1.3f) % (2f * PI.toFloat()),
                            size = if ((row + col) % 5 == 0) 3.5f else 2.8f,
                        )
                    )
                }
            }
        }
    }

    val twinkle = if (reducedMotion) {
        0.5f
    } else {
        val transition = rememberInfiniteTransition(label = "spaceTwinkle")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(4200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "twinklePhase",
        ).value
    }

    var shooting by remember { mutableStateOf(false) }
    val shotProgress = remember { Animatable(-1f) }

    LaunchedEffect(reducedMotion) {
        if (reducedMotion) return@LaunchedEffect
        while (true) {
            delay(Random.nextLong(8_000, 15_000))
            shooting = true
            shotProgress.snapTo(0f)
            shotProgress.animateTo(1f, tween(900, easing = LinearEasing))
            shooting = false
            shotProgress.snapTo(-1f)
        }
    }

    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            stars.forEachIndexed { index, star ->
                val pulse = 0.35f + 0.5f * ((sin(star.phase + twinkle * 2f * PI.toFloat()) + 1f) / 2f)
                val color = starColors[index % starColors.size]
                    .copy(alpha = pulse.coerceIn(0.2f, 0.95f))
                val cell = star.size
                val cx = star.xFrac * size.width
                val cy = star.yFrac * size.height
                drawRect(color, Offset(cx - cell / 2, cy - cell), androidx.compose.ui.geometry.Size(cell, cell))
                drawRect(color, Offset(cx - cell, cy - cell / 2), androidx.compose.ui.geometry.Size(cell, cell))
                drawRect(color, Offset(cx - cell / 2, cy), androidx.compose.ui.geometry.Size(cell, cell))
                drawRect(color, Offset(cx, cy - cell / 2), androidx.compose.ui.geometry.Size(cell, cell))
                drawRect(color, Offset(cx - cell / 2, cy - cell / 2), androidx.compose.ui.geometry.Size(cell, cell))
            }

            if (shooting && shotProgress.value >= 0f) {
                val t = shotProgress.value
                val startX = size.width * 0.15f
                val startY = size.height * 0.12f
                val endX = size.width * 0.85f
                val endY = size.height * 0.38f
                val x = startX + (endX - startX) * t
                val y = startY + (endY - startY) * t
                val tail = 48f * (1f - t * 0.3f)
                val angleDeg = kotlin.math.atan2(
                    (endY - startY).toDouble(),
                    (endX - startX).toDouble(),
                ).toFloat() * (180f / PI.toFloat())
                rotate(angleDeg, pivot = Offset(x, y)) {
                    drawLine(
                        color = colors.lemon.copy(alpha = 0.15f * (1f - t)),
                        start = Offset(x - tail, y),
                        end = Offset(x, y),
                        strokeWidth = 3f,
                    )
                    drawRect(
                        colors.cream.copy(alpha = 0.9f * (1f - t * 0.5f)),
                        topLeft = Offset(x - 4f, y - 4f),
                        size = androidx.compose.ui.geometry.Size(8f, 8f),
                    )
                }
            }
        }
    }
}

@Composable
fun CandyFloatingSprinklesBackground(modifier: Modifier = Modifier) {
    val reducedMotion = rememberReducedMotion()
    val sprinkleColors = listOf(SprinklesRed, SprinklesBlue, SprinklesGreen, LemonYellow, BubblegumPink)
    val drift = if (reducedMotion) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "candyDrift")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(7000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "sprinkleDrift",
        ).value
    }

    Canvas(modifier) {
        val step = 28.dp.toPx()
        var row = 0
        var y = 0f
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else step / 2
            var col = 0
            while (x < size.width) {
                val color = sprinkleColors[(row + col) % sprinkleColors.size]
                if ((row + col) % 3 != 0) {
                    val bob = if (reducedMotion) 0f else sin((row + col) * 0.8f + drift * 2f * PI.toFloat()) * 5f
                    drawRect(color.copy(alpha = 0.85f), Offset(x, y + bob), androidx.compose.ui.geometry.Size(4f, 4f))
                }
                x += step
                col++
            }
            y += step
            row++
        }
    }
}
