package com.sweetcalendar.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import com.sweetcalendar.ui.theme.SweetMotion

private val slideSpec = tween<IntOffset>(320, easing = FastOutSlowInEasing)
private val fadeSpec = tween<Float>(SweetMotion.ShortMillis, easing = FastOutSlowInEasing)
private val sheetSlideSpec = tween<IntOffset>(SweetMotion.MediumMillis, easing = FastOutSlowInEasing)

/**
 * Horizontal push/pop for stacked screens and bottom-nav tab changes.
 *
 * @param tabDirection when [fromDepth] and [toDepth] are both 0, `-1` or `1` for tab slide direction; else `0`.
 */
fun sweetScreenTransition(
    fromDepth: Int,
    toDepth: Int,
    tabDirection: Int = 0,
): ContentTransform {
    return when {
        toDepth > fromDepth ->
            (slideInHorizontally(slideSpec) { it } + fadeIn(fadeSpec)) togetherWith
                (slideOutHorizontally(slideSpec) { -it / 4 } + fadeOut(fadeSpec))
        toDepth < fromDepth ->
            ((slideInHorizontally(slideSpec) { -it / 4 } + fadeIn(fadeSpec)) togetherWith
                (slideOutHorizontally(slideSpec) { it } + fadeOut(fadeSpec)))
                .apply { targetContentZIndex = -1f }
        fromDepth == 0 && toDepth == 0 && tabDirection != 0 -> {
            val dir = tabDirection
            (slideInHorizontally(slideSpec) { dir * it / 5 } + fadeIn(fadeSpec)) togetherWith
                (slideOutHorizontally(slideSpec) { -dir * it / 5 } + fadeOut(fadeSpec))
        }
        else -> fadeIn(fadeSpec) togetherWith fadeOut(fadeSpec)
    }
}

fun <T> AnimatedContentTransitionScope<T>.sweetScreenTransitionByDepth(
    depthOf: (T) -> Int,
    tabOrdinalOf: (T) -> Int = { 0 },
): ContentTransform {
    val fromDepth = depthOf(initialState)
    val toDepth = depthOf(targetState)
    val tabDirection = if (fromDepth == 0 && toDepth == 0) {
        if (tabOrdinalOf(targetState) >= tabOrdinalOf(initialState)) 1 else -1
    } else {
        0
    }
    return sweetScreenTransition(fromDepth, toDepth, tabDirection)
}

fun sweetFullScreenSheetEnter(): EnterTransition =
    slideInVertically(sheetSlideSpec) { it } + fadeIn(fadeSpec)

fun sweetFullScreenSheetExit(): ExitTransition =
    slideOutVertically(sheetSlideSpec) { it } + fadeOut(fadeSpec)

private val dialogScaleSpec = tween<Float>(SweetMotion.ShortMillis, easing = FastOutSlowInEasing)

fun sweetDialogEnter(reducedMotion: Boolean): EnterTransition =
    if (reducedMotion) {
        fadeIn(fadeSpec)
    } else {
        scaleIn(dialogScaleSpec, initialScale = 0.94f) + fadeIn(fadeSpec)
    }

fun sweetDialogExit(reducedMotion: Boolean): ExitTransition =
    if (reducedMotion) {
        fadeOut(fadeSpec)
    } else {
        scaleOut(dialogScaleSpec, targetScale = 0.96f) + fadeOut(fadeSpec)
    }
