package com.sweetcalendar.ui.habit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.components.PixelIcon
import com.sweetcalendar.ui.components.TaskHeartIcon
import com.sweetcalendar.ui.components.TaskPenIcon
import com.sweetcalendar.ui.theme.SweetTheme

enum class HabitCategory(val label: String) {
    Personal("Personal"),
    School("School"),
    Health("Health"),
    ;

    companion object {
        fun fromString(value: String): HabitCategory = entries.find {
            it.label.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true)
        } ?: Personal
    }
}

@Composable
fun HabitCategory.accentColor(): Color {
    val colors = SweetTheme.colors
    return when (this) {
        HabitCategory.Personal -> colors.mintDeep
        HabitCategory.School -> colors.purpleDeep
        HabitCategory.Health -> colors.pinkDeep
    }
}

@Composable
fun HabitCategoryIcon(
    category: HabitCategory,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
) {
    when (category) {
        HabitCategory.Personal -> TaskHeartIcon(
            modifier = modifier,
            size = size,
            color = SweetTheme.colors.mintDeep,
        )
        // Same pen glyph as to-do School.
        HabitCategory.School -> TaskPenIcon(
            modifier = modifier,
            size = size,
            color = SweetTheme.colors.purpleDeep,
        )
        HabitCategory.Health -> HabitSoccerBallIcon(
            modifier = modifier,
            size = size,
            color = SweetTheme.colors.pinkDeep,
        )
    }
}

/**
 * Soccer ball in the same pixel style as other category markers.
 * `1` = pink seams/patches, `w` = white panels.
 */
@Composable
fun HabitSoccerBallIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    color: Color? = null,
) {
    val colors = SweetTheme.colors
    val seam = color ?: colors.pinkDeep
    val panel = if (colors.isDark) colors.paper else Color.White
    PixelIcon(
        rows = listOf(
            "...11111...",
            "..1wwwwww1..",
            ".1ww111ww1.",
            ".1w11111w1.",
            "1ww11111ww1",
            "1w1111111w1",
            "1ww11111ww1",
            ".11w11w11.",
            ".1w1ww1w1.",
            "..1wwwwww1..",
            "...11111...",
        ),
        palette = mapOf(
            '1' to seam,
            'w' to panel,
        ),
        size = size,
        modifier = modifier,
    )
}
