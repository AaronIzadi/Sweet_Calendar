package com.sweetcalendar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sweetcalendar.ui.theme.SweetTheme

/** Marker colors for scheduled **tasks** (Personal pink, Work purple, Home mint). */
@Composable
fun taskCategoryMarkerColor(category: TaskCategory): Color = when (category) {
    TaskCategory.Personal -> taskCategoryHeartColor()
    TaskCategory.Home -> taskCategoryLeafColor()
    TaskCategory.Work -> taskCategoryGemColor()
    TaskCategory.School -> taskCategoryBookSpineColor()
}

/** Marker colors for **to-dos** — same glyphs, different palette accents. */
@Composable
fun todoCategoryMarkerColor(category: TaskCategory): Color {
    val colors = SweetTheme.colors
    return when (category) {
        TaskCategory.Personal -> colors.purpleDeep
        TaskCategory.Work -> colors.mintDeep
        TaskCategory.School -> colors.pinkDeep
        TaskCategory.Home -> colors.mint
    }
}

@Composable
fun categoryMarkerColor(category: TaskCategory, forTodo: Boolean): Color =
    if (forTodo) todoCategoryMarkerColor(category) else taskCategoryMarkerColor(category)
