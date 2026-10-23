package com.sweetcalendar.recurrence

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.mockupSp

/** Matches None / Weekly / Monthly repeat chips in the task form. */
@Composable
fun recurrenceIntervalInlineTextStyle(): TextStyle = TextStyle(
    fontFamily = BodyFont,
    fontWeight = FontWeight.Bold,
    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F),
)

fun RecurrenceFreq.userLabel(): String = when (this) {
    RecurrenceFreq.NONE -> "None"
    RecurrenceFreq.WEEKLY -> "Weekly"
    RecurrenceFreq.MONTHLY -> "Monthly"
}

fun TaskEntity.repeatSummary(weekStartsOn: Int = 0): String? {
    val freq = recurrenceFrequency()
    if (freq == RecurrenceFreq.NONE) return null
    val days = recurrenceWeekdaySet()
    val dayLabel = RecurrenceWeekdays.label(days, weekStartsOn)
    val step = recurrenceInterval.coerceAtLeast(1)
    val unit = when (freq) {
        RecurrenceFreq.WEEKLY -> if (step == 1) "each week" else "$step weeks"
        RecurrenceFreq.MONTHLY -> if (step == 1) "each month" else "$step months"
        RecurrenceFreq.NONE -> return null
    }
    return "Repeat for $unit on $dayLabel."
}
