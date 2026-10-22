package com.sweetcalendar.ui.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * Shared motion helpers for SweetCalendar. Keep durations and springs here so
 * the whole app feels consistent.
 */

/** Squishes the element while pressed and springs back. Pass the same
 *  [InteractionSource] you give to `clickable`. */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.94f
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Press feedback for containers with nested click targets (e.g. task cards).
 * Skips the squash when a child [clickable] already handled the press.
 */
fun Modifier.pressScaleNested(pressedScale: Float = 0.95f): Modifier = composed {
    val reducedMotion = rememberReducedMotion()
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = when {
            reducedMotion -> 1f
            pressed -> pressedScale
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressScaleNested",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.pointerInput(reducedMotion) {
        if (reducedMotion) return@pointerInput
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = true)
            pressed = true
            waitForUpOrCancellation(pass = PointerEventPass.Final)
            pressed = false
        }
    }
}

/** Scales in with a bounce the first time it enters composition. */
@Composable
fun Modifier.popIn(delayMillis: Int = 0, from: Float = 0.2f): Modifier {
    val scale = remember { Animatable(from) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) kotlinx.coroutines.delay(delayMillis.toLong())
        scale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }
    return this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        alpha = ((scale.value - from) / (1f - from)).coerceIn(0f, 1f)
    }
}

/** Gentle up/down bob. Use for decorative art only. */
@Composable
fun Modifier.floating(
    amplitude: Dp = 5.dp,
    periodMillis: Int = 2600,
    phaseMillis: Int = 0
): Modifier {
    val reducedMotion = rememberReducedMotion()
    val ampPx = with(LocalDensity.current) { amplitude.roundToPx() }
    val bobFraction = if (reducedMotion) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "floating")
        transition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(periodMillis, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(phaseMillis)
            ),
            label = "floatingOffset"
        ).value
    }
    // Lay out extra vertical room so the bob does not clip at rest bounds.
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val height = placeable.height + ampPx * 2
        layout(placeable.width, height) {
            placeable.placeRelative(0, ampPx + (bobFraction * ampPx).roundToInt())
        }
    }
}

/** Fade + rise for a list item that was added after the screen opened. */
@Composable
fun Modifier.appearOnEnter(enabled: Boolean): Modifier {
    val progress = remember { Animatable(if (enabled) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (progress.value < 1f) {
            progress.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }
    }
    return this.graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationY = (1f - p) * 28.dp.toPx()
        scaleX = 0.96f + 0.04f * p
        scaleY = 0.96f + 0.04f * p
    }
}

/**
 * Returns a function telling whether an item id is new since the screen
 * opened (true only once per id). Call inside the lazy item as
 * `remember { isNew(task.id) }`.
 */
@Composable
fun rememberNewItemTracker(currentIds: List<Long>): (Long) -> Boolean {
    val seen = remember { currentIds.toMutableSet() }
    return remember { { id: Long -> seen.add(id) } }
}

/** True one frame after first composition; drive "grow in" animations with it. */
@Composable
fun rememberEntered(): Boolean {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    return entered
}

/** Counts from 0 up to [target] the first time it is shown, then follows changes. */
@Composable
fun rememberCountUp(target: Int, durationMillis: Int = 900): Int {
    val entered = rememberEntered()
    val value by animateIntAsState(
        targetValue = if (entered) target else 0,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "countUp"
    )
    return value
}

/** Draws a strike-through line that sweeps across the text as [progress] goes 0 -> 1. */
fun Modifier.strikeThroughSweep(
    progress: () -> Float,
    layout: () -> TextLayoutResult?,
    color: Color,
    thickness: Dp = 1.5.dp
): Modifier = drawWithContent {
    drawContent()
    val p = progress()
    val l = layout()
    if (p > 0f && l != null && l.lineCount > 0) {
        val widths = List(l.lineCount) { l.getLineRight(it) - l.getLineLeft(it) }
        var remaining = widths.sum() * p
        for (i in 0 until l.lineCount) {
            if (remaining <= 0f) break
            val w = minOf(widths[i], remaining)
            val y = (l.getLineTop(i) + l.getLineBottom(i)) / 2f
            val x0 = l.getLineLeft(i)
            drawLine(
                color = color,
                start = Offset(x0, y),
                end = Offset(x0 + w, y),
                strokeWidth = thickness.toPx()
            )
            remaining -= w
        }
    }
}

private class ConfettiPiece(
    val angle: Float,
    val speed: Float,
    val sizeDp: Float,
    val colorIndex: Int
)

/** One-shot pixel confetti burst drawn on top of the element's content. */
@Composable
fun Modifier.confettiBurst(
    colors: List<Color>,
    count: Int = 36,
    originYFraction: Float = 0.28f,
    durationMillis: Int = 1500
): Modifier {
    val progress = remember { Animatable(0f) }
    val pieces = remember {
        val r = Random(11)
        List(count) {
            ConfettiPiece(
                angle = (-PI.toFloat()) + r.nextFloat() * PI.toFloat() * 1.0f,
                speed = 0.35f + r.nextFloat() * 0.65f,
                sizeDp = 4f + r.nextFloat() * 4f,
                colorIndex = r.nextInt(colors.size.coerceAtLeast(1))
            )
        }
    }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis, easing = LinearOutSlowInEasing))
    }
    return this.drawWithContent {
        drawContent()
        val t = progress.value
        if (t < 1f && colors.isNotEmpty()) {
            val origin = Offset(size.width / 2f, size.height * originYFraction)
            val reach = size.width * 0.55f
            val gravity = size.height * 0.55f
            pieces.forEach { p ->
                val x = origin.x + cos(p.angle) * p.speed * reach * t
                val y = origin.y + sin(p.angle) * p.speed * reach * t + gravity * t * t
                val a = (1f - t * t).coerceIn(0f, 1f)
                val s = p.sizeDp.dp.toPx()
                drawRect(
                    color = colors[p.colorIndex % colors.size].copy(alpha = a),
                    topLeft = Offset(x - s / 2f, y - s / 2f),
                    size = Size(s, s)
                )
            }
        }
    }
}
