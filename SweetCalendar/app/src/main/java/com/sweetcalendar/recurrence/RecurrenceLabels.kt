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
    val dayLabel = when (freq) {
        RecurrenceFreq.WEEKLY -> RecurrenceWeekdays.label(recurrenceWeekdaySet(), weekStartsOn)
        RecurrenceFreq.MONTHLY -> RecurrenceMonthDays.label(recurrenceMonthDayRule(), parsedDate())
        RecurrenceFreq.NONE -> return null
    }
    val pluralUnit = when (freq) {
        RecurrenceFreq.WEEKLY -> "weeks"
        RecurrenceFreq.MONTHLY -> "months"
        RecurrenceFreq.NONE -> return null
    }
    val total = recurrenceCount
    val cadence = when (freq) {
        RecurrenceFreq.WEEKLY -> "each week"
        RecurrenceFreq.MONTHLY -> "each month"
        RecurrenceFreq.NONE -> return null
    }
    return when {
        total == null -> "Repeats $cadence on $dayLabel."
        total == 1 -> "Repeats once on $dayLabel."
        else -> {
            val following = recurrenceFollowingFromStored(freq, total, recurrenceWeekdaySet())
            "Repeats for $following more $pluralUnit on $dayLabel."
        }
    }
}
