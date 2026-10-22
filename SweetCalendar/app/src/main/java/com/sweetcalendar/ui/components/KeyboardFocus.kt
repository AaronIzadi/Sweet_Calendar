package com.sweetcalendar.ui.components

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

/** Requests the soft keyboard when a text field gains focus. */
fun Modifier.showKeyboardOnFocus(): Modifier = composed {
    val keyboard = LocalSoftwareKeyboardController.current
    onFocusEvent { state ->
        if (state.isFocused) {
            keyboard?.show()
        }
    }
}

/** Focuses a field when the screen appears and opens the IME (e.g. search). */
@Composable
fun RequestKeyboardOnEnter(focusRequester: FocusRequester) {
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(focusRequester) {
        withFrameNanos { }
        focusRequester.requestFocus()
        keyboard?.show()
    }
}

fun Modifier.searchFieldFocus(focusRequester: FocusRequester): Modifier =
    focusRequester(focusRequester).showKeyboardOnFocus()

/** Dialog windows do not inherit the activity [android:windowSoftInputMode]; resize for IME. */
@Composable
fun DialogKeyboardInsets() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
        @Suppress("DEPRECATION")
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onDispose { }
    }
}
