package com.sweetcalendar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

/**
 * Weight for secondary copy (dates, meta, subtitles) on pastel backgrounds.
 * Paired with [SweetColors.muted] for WCAG AA on cream/paper (see WebAIM contrast checker).
 */
val SecondaryTextWeight: FontWeight = FontWeight.ExtraBold

/** Secondary label / meta text color for the active theme. */
@Composable
fun SweetTheme.secondaryTextColor(): Color = colors.muted

fun TextStyle.asSecondaryText(
    fontSize: TextUnit = this.fontSize,
    lineHeight: TextUnit = this.lineHeight,
): TextStyle = copy(
    fontFamily = BodyFont,
    fontWeight = SecondaryTextWeight,
    fontSize = fontSize,
    lineHeight = lineHeight,
)
