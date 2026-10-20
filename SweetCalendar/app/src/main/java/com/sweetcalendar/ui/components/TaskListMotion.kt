package com.sweetcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.rememberReducedMotion

private val taskCardShape = RoundedCornerShape(14.dp)

private val checkPopSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium,
)

private val listItemPlacementSpring = spring<IntOffset>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)

/**
 * Lazy list item motion: slide in, shrink out, smooth gap close.
 */
fun LazyItemScope.sweetTaskItemMotion(): Modifier {
    return Modifier.animateItem(
        fadeInSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        fadeOutSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        placementSpec = listItemPlacementSpring,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteTaskCard(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = SweetTheme.colors
    val reducedMotion = rememberReducedMotion()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (!enabled) return@rememberSwipeToDismissBoxState false
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.38f },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = enabled && !reducedMotion,
        backgroundContent = {
            val showDelete = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = mockupDp(10))
                    .clip(taskCardShape)
                    .background(if (showDelete) colors.pinkDeep else colors.paper),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (showDelete) {
                    TrashIcon(
                        modifier = Modifier.padding(end = mockupDp(22)),
                        width = mockupDp(18),
                        height = mockupDp(20),
                        color = SweetTheme.colors.cream,
                    )
                }
            }
        },
        content = { content() },
    )
}

@Composable
fun AnimatedTaskTitle(
    text: AnnotatedString,
    isDone: Boolean,
    style: TextStyle,
    inkColor: androidx.compose.ui.graphics.Color,
    mutedColor: androidx.compose.ui.graphics.Color,
    maxLines: Int = 2,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    val strikeProgress by animateFloatAsState(
        targetValue = if (isDone) 1f else 0f,
        animationSpec = if (reducedMotion) {
            tween(SweetMotion.ShortMillis)
        } else {
            tween(SweetMotion.MediumMillis + 40)
        },
        label = "taskStrike",
    )
    val titleColor by animateColorAsState(
        targetValue = if (isDone) mutedColor else inkColor,
        animationSpec = SweetMotion.shortTween(),
        label = "taskTitleColor",
    )

    Text(
        text = text,
        style = style,
        color = titleColor,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.drawBehind {
            if (strikeProgress <= 0f) return@drawBehind
            val lineY = size.height * 0.52f
            drawLine(
                color = titleColor.copy(alpha = 0.9f),
                start = Offset(0f, lineY),
                end = Offset(size.width * strikeProgress, lineY),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
            )
        },
    )
}

@Composable
fun PoppingTaskCompleteIcon(
    size: Dp,
    bgColor: androidx.compose.ui.graphics.Color,
    pop: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(pop) {
        if (pop && !reducedMotion) {
            scale.snapTo(0.5f)
            scale.animateTo(1.12f, checkPopSpring)
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
        } else if (!pop) {
            scale.snapTo(1f)
        }
    }

    ThemeCompletedCheckIcon(
        size = size,
        bgColor = bgColor,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .then(
                Modifier.clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
            ),
    )
}
