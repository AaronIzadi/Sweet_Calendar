package com.sweetcalendar.ui.addtask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sweetcalendar.ui.theme.SweetMotion
import kotlinx.coroutines.delay

@Composable
fun AnimatedFormListItem(
    reducedMotion: Boolean,
    onRemoved: () -> Unit,
    content: @Composable (requestRemove: () -> Unit) -> Unit,
) {
    var visible by remember { mutableStateOf(true) }
    val enterTransition = if (reducedMotion) {
        fadeIn(SweetMotion.shortTween())
    } else {
        fadeIn(SweetMotion.mediumTween()) +
            expandVertically(SweetMotion.mediumTween()) +
            scaleIn(
                initialScale = 0.96f,
                animationSpec = SweetMotion.mediumTween(),
            )
    }
    val exitTransition = if (reducedMotion) {
        fadeOut(SweetMotion.shortTween())
    } else {
        fadeOut(SweetMotion.shortTween()) +
            shrinkVertically(SweetMotion.shortTween()) +
            scaleOut(
                targetScale = 0.96f,
                animationSpec = SweetMotion.shortTween(),
            )
    }
    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition,
    ) {
        content { visible = false }
    }
    LaunchedEffect(visible) {
        if (!visible) {
            delay(if (reducedMotion) 0L else SweetMotion.ShortMillis.toLong())
            onRemoved()
        }
    }
}
