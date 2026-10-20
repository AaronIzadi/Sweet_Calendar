package com.sweetcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlinx.coroutines.launch

@Composable
fun Modifier.wobbleOnTap(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
): Modifier {
    val reducedMotion = rememberReducedMotion()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val wobbleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    return graphicsLayer {
        rotationZ = rotation.value
        scaleX = scale.value
        scaleY = scale.value
    }.clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = {
            if (!reducedMotion) {
                scope.launch {
                    scale.snapTo(0.92f)
                    rotation.snapTo(-10f)
                    rotation.animateTo(8f, wobbleSpring)
                    scale.animateTo(1.06f, wobbleSpring)
                    rotation.animateTo(0f, wobbleSpring)
                    scale.animateTo(1f, wobbleSpring)
                }
            }
            onClick?.invoke()
        },
    )
}
