package com.sweetcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.theme.BubblegumPink
import com.sweetcalendar.ui.theme.LemonYellow
import com.sweetcalendar.ui.theme.MintGreen
import com.sweetcalendar.ui.theme.SprinklesBlue
import com.sweetcalendar.ui.theme.SprinklesRed
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class FallingCandy(
    val id: Long,
    val xBias: Float,
    val tint: Color,
)

private data class ConfettiParticle(
    val angle: Float,
    val distance: Float,
    val color: Color,
    val size: Float,
)

@Composable
fun JarCandyDropOverlay(
    completed: Int,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    var lastCompleted by remember { mutableIntStateOf(completed) }
    val active = remember { mutableStateListOf<FallingCandy>() }
    var dropId by remember { mutableLongStateOf(0L) }

    LaunchedEffect(completed) {
        if (reducedMotion) {
            lastCompleted = completed
            return@LaunchedEffect
        }
        if (completed > lastCompleted) {
            val delta = (completed - lastCompleted).coerceAtMost(3)
            repeat(delta) {
                val palette = listOf(MintGreen, BubblegumPink, LemonYellow, SprinklesBlue)
                active.add(
                    FallingCandy(
                        id = dropId++,
                        xBias = Random.nextFloat() * 0.75f + 0.1f,
                        tint = palette[Random.nextInt(palette.size)],
                    )
                )
            }
        }
        lastCompleted = completed
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val trackWidth = maxWidth
        active.forEach { candy ->
            key(candy.id) {
                FallingCandyChip(
                    xBias = candy.xBias,
                    trackWidth = trackWidth,
                    tint = candy.tint,
                    onFinished = { active.remove(candy) },
                )
            }
        }
    }
}

@Composable
private fun FallingCandyChip(
    xBias: Float,
    trackWidth: Dp,
    tint: Color,
    onFinished: () -> Unit,
) {
    val density = LocalDensity.current
    val dropSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )
    val offsetY = remember { Animatable(-48f) }
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, dropSpring)
        offsetY.animateTo(0f, dropSpring)
        delay(120)
        onFinished()
    }

    val xPx = with(density) { (trackWidth * xBias).toPx() }

    Box(Modifier.fillMaxWidth()) {
        CheckCandyIcon(
            size = mockupDp(14),
            bgColor = tint,
            modifier = Modifier
                .offset { IntOffset(xPx.toInt(), offsetY.value.toInt()) }
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
        )
    }
}

@Composable
fun CelebrationBurstBackdrop(modifier: Modifier = Modifier) {
    val reducedMotion = rememberReducedMotion()
    val colors = SweetTheme.colors
    val particles = remember {
        List(18) {
            ConfettiParticle(
                angle = Random.nextFloat() * 360f,
                distance = Random.nextFloat() * 0.45f + 0.25f,
                color = listOf(
                    colors.pink,
                    colors.lemon,
                    colors.mint,
                    SprinklesRed,
                    SprinklesBlue,
                    colors.purple,
                ).random(),
                size = Random.nextFloat() * 6f + 4f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    val sparkleScale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        if (reducedMotion) {
            progress.snapTo(1f)
            sparkleScale.snapTo(1f)
            return@LaunchedEffect
        }
        sparkleScale.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        sparkleScale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
        progress.animateTo(1f, tween(SweetMotion.MediumMillis + 80))
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.28f
            val maxR = size.minDimension * 0.55f
            particles.forEach { p ->
                val rad = Math.toRadians(p.angle.toDouble())
                val r = maxR * p.distance * progress.value
                val x = cx + cos(rad).toFloat() * r
                val y = cy + sin(rad).toFloat() * r
                drawRect(
                    color = p.color.copy(alpha = 1f - progress.value * 0.35f),
                    topLeft = Offset(x - p.size / 2, y - p.size / 2),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size),
                )
            }
        }
        ThemeSparkleIcon(
            size = mockupDp(20),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = mockupDp(8))
                .graphicsLayer {
                    scaleX = sparkleScale.value
                    scaleY = sparkleScale.value
                    alpha = 0.85f
                },
        )
    }
}

@Composable
fun CelebrationJarHero(modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0.7f) }
    val reducedMotion = rememberReducedMotion()

    LaunchedEffect(Unit) {
        if (reducedMotion) {
            scale.snapTo(1f)
            return@LaunchedEffect
        }
        scale.animateTo(1.12f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
    }

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        },
        contentAlignment = Alignment.Center,
    ) {
        ThemeStatsJarIcon(size = mockupDp(52))
        CheckCandyIcon(
            size = mockupDp(16),
            bgColor = MintGreen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = mockupDp(4), y = (-4).dp),
        )
    }
}
