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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import com.sweetcalendar.ui.theme.SweetMotion

private val fadeSpec = tween<Float>(SweetMotion.ShortMillis, easing = FastOutSlowInEasing)
private val sheetSlideSpec = tween<IntOffset>(SweetMotion.MediumMillis, easing = FastOutSlowInEasing)
private val popupScaleSpec = tween<Float>(SweetMotion.ShortMillis, easing = FastOutSlowInEasing)
private val popupOrigin = TransformOrigin.Center

private fun centerPopupTransform(popOut: Boolean = false): ContentTransform {
    val transform = (
        scaleIn(popupScaleSpec, initialScale = 0.9f, transformOrigin = popupOrigin) + fadeIn(fadeSpec)
    ) togetherWith (
        fadeOut(fadeSpec) + scaleOut(popupScaleSpec, targetScale = 0.94f, transformOrigin = popupOrigin)
    )
    return if (popOut) transform.apply { targetContentZIndex = -1f } else transform
}

/** Center scale/fade for overlays, tabs, and stacked screens (no horizontal slide). */
fun sweetScreenTransition(
    fromDepth: Int,
    toDepth: Int,
    tabDirection: Int = 0,
): ContentTransform {
    return when {
        toDepth > fromDepth -> centerPopupTransform(popOut = false)
        toDepth < fromDepth -> centerPopupTransform(popOut = true)
        fromDepth == 0 && toDepth == 0 && tabDirection != 0 -> centerPopupTransform(popOut = false)
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

fun sweetDialogEnter(reducedMotion: Boolean): EnterTransition =
    if (reducedMotion) {
        fadeIn(fadeSpec)
    } else {
        scaleIn(popupScaleSpec, initialScale = 0.92f, transformOrigin = popupOrigin) + fadeIn(fadeSpec)
    }

fun sweetDialogExit(reducedMotion: Boolean): ExitTransition =
    if (reducedMotion) {
        fadeOut(fadeSpec)
    } else {
        scaleOut(popupScaleSpec, targetScale = 0.94f, transformOrigin = popupOrigin) + fadeOut(fadeSpec)
    }
