package com.sweetcalendar.ui.components

import android.view.Gravity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.sweetcalendar.ui.navigation.sweetDialogEnter
import com.sweetcalendar.ui.navigation.sweetDialogExit
import com.sweetcalendar.ui.theme.rememberReducedMotion

/** Disables platform dialog window slide so [SweetDialogSurface] center scale is visible. */
@Composable
fun DialogCenterMotion() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
        window?.setWindowAnimations(0)
        window?.setGravity(Gravity.CENTER)
        onDispose { }
    }
}

/**
 * Center modal in the current window (no [Dialog] window), so Android's side-slide never runs.
 */
@Composable
fun SweetModalOverlay(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val reducedMotion = rememberReducedMotion()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    BackHandler(onBack = onDismissRequest)

    Box(modifier = modifier.fillMaxSize().zIndex(200f)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismissRequest,
                ),
        )
        AnimatedVisibility(
            visible = visible,
            enter = sweetDialogEnter(reducedMotion),
            exit = sweetDialogExit(reducedMotion),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
        ) {
            content()
        }
    }
}

@Composable
fun SweetDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DialogCenterMotion()
        content()
    }
}
