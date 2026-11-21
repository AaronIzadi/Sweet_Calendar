package com.sweetcalendar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import kotlinx.coroutines.delay

private val SnackbarTextColor = Color(0xFFFFF6EA)

/** Oldest → newest. Rendered with the newest on top. */
data class SweetToastData(
    val id: Long,
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val onDismiss: () -> Unit,
)

@Composable
fun SweetToastStack(
    toasts: List<SweetToastData>,
    modifier: Modifier = Modifier,
) {
    // Newest first so the latest toast sits on top and older ones shift down.
    val newestFirst = remember(toasts) { toasts.sortedByDescending { it.id } }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.SNACKBAR_STACK_GAP)),
    ) {
        newestFirst.forEach { toast ->
            key(toast.id) {
                SweetSnackbar(
                    message = toast.message,
                    visible = true,
                    actionLabel = toast.actionLabel,
                    onAction = toast.onAction,
                    onDismiss = toast.onDismiss,
                )
            }
        }
    }
}

@Composable
fun SweetSnackbar(
    message: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.SNACKBAR_RADIUS))
    val enterSpring = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
    val overshootSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
    val dismissCallback = onDismiss
    val canDismiss = dismissCallback != null
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (dismissCallback != null && value == SwipeToDismissBoxValue.EndToStart) {
                dismissCallback()
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.28f },
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(enterSpring) { -it } +
            scaleIn(overshootSpring, initialScale = 0.92f) +
            fadeIn(overshootSpring),
        exit = slideOutVertically(enterSpring) { -it } +
            scaleOut(overshootSpring, targetScale = 0.94f) +
            fadeOut(overshootSpring),
        modifier = modifier
    ) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = Modifier.fillMaxWidth(),
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = canDismiss,
            backgroundContent = { Box(Modifier.fillMaxSize()) },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = mockupDp(MockupDimens.SNACKBAR_EDGE))
                    .shadow(
                        elevation = mockupDp(6),
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
                    .clip(shape)
                    .background(if (colors.isDark) colors.bannerBg else colors.chocDeep)
                    .then(
                        if (onDismiss != null) {
                            Modifier.clickable(onClick = onDismiss)
                        } else {
                            Modifier
                        }
                    )
                    .padding(
                        horizontal = mockupDp(MockupDimens.SNACKBAR_PAD_H),
                        vertical = mockupDp(MockupDimens.SNACKBAR_PAD_V)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    message,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SNACKBAR_MSG_F),
                        lineHeight = mockupSp(16f)
                    ),
                    color = SnackbarTextColor,
                    modifier = Modifier.weight(1f)
                )
                if (actionLabel != null && onAction != null) {
                    Text(
                        actionLabel,
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SNACKBAR_RECOVER),
                            letterSpacing = mockupSp(0.5f),
                            lineHeight = mockupSp(14f)
                        ),
                        color = colors.lemon,
                        modifier = Modifier
                            .clickable(onClick = onAction)
                            .padding(start = mockupDp(10))
                    )
                }
            }
        }
    }
}

@Composable
fun SweetRecoverSnackbar(
    message: String,
    visible: Boolean,
    onRecover: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String = "RECOVER",
    onDismiss: (() -> Unit)? = null,
) {
    SweetSnackbar(
        message = message,
        visible = visible,
        modifier = modifier,
        actionLabel = actionLabel,
        onAction = onRecover,
        onDismiss = onDismiss,
    )
}

/**
 * Message-only snackbar that auto-dismisses after [visibleMs].
 * Tap or swipe left also dismisses. Set [message] to show; clear via [onDismiss].
 */
@Composable
fun SweetTimedSnackbar(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    visibleMs: Long = MockupDimens.SNACKBAR_MESSAGE_VISIBLE_MS,
) {
    LaunchedEffect(message) {
        if (message != null) {
            delay(visibleMs)
            onDismiss()
        }
    }
    SweetSnackbar(
        message = message.orEmpty(),
        visible = message != null,
        modifier = modifier,
        onDismiss = onDismiss,
    )
}

