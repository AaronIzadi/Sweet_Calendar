package com.sweetcalendar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sweetcalendar.ui.navigation.sweetDialogEnter
import com.sweetcalendar.ui.navigation.sweetDialogExit
import com.sweetcalendar.ui.theme.rememberReducedMotion

@Composable
fun SweetDialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    DialogCenterMotion()
    val reducedMotion = rememberReducedMotion()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = sweetDialogEnter(reducedMotion),
        exit = sweetDialogExit(reducedMotion),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
