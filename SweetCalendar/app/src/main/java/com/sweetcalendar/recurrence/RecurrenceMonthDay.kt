package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate

sealed class RecurrenceMonthDay {
    data object OnThisDay : RecurrenceMonthDay()
    data object Last : RecurrenceMonthDay()
    data class Fixed(val day: Int) : RecurrenceMonthDay()
}

object RecurrenceMonthDays {

    fun encode(rule: RecurrenceMonthDay): String = when (rule) {
        RecurrenceMonthDay.OnThisDay -> ""
        RecurrenceMonthDay.Last -> "LAST"
        is RecurrenceMonthDay.Fixed -> "D${rule.day.coerceIn(1, 31)}"
    }

    fun parse(stored: String?): RecurrenceMonthDay {
        val raw = stored?.trim().orEmpty()
        return when {
            raw.isEmpty() -> RecurrenceMonthDay.OnThisDay
            raw == "LAST" -> RecurrenceMonthDay.Last
            raw.startsWith("D", ignoreCase = true) -> {
                val day = raw.drop(1).toIntOrNull()?.coerceIn(1, 31)
                if (day != null) RecurrenceMonthDay.Fixed(day) else RecurrenceMonthDay.OnThisDay
            }
            else -> RecurrenceMonthDay.OnThisDay
        }
    }

    fun label(rule: RecurrenceMonthDay, anchor: JalaliDate): String = when (rule) {
        RecurrenceMonthDay.OnThisDay -> "day ${anchor.day}"
        RecurrenceMonthDay.Last -> "the last day"
        is RecurrenceMonthDay.Fixed -> "day ${rule.day.coerceIn(1, 31)}"
    }

    /** Day of month for [month] (first day of that month is not required; any date in the month works). */
    fun dayInMonth(month: JalaliDate, anchor: JalaliDate, rule: RecurrenceMonthDay): Int = when (rule) {
        RecurrenceMonthDay.OnThisDay -> minOf(anchor.day, month.daysInMonth())
        RecurrenceMonthDay.Last -> month.daysInMonth()
        is RecurrenceMonthDay.Fixed -> minOf(rule.day.coerceIn(1, 31), month.daysInMonth())
    }
}

fun TaskEntity.recurrenceMonthDayRule(): RecurrenceMonthDay {
    if (recurrenceFrequency() != RecurrenceFreq.MONTHLY) return RecurrenceMonthDay.OnThisDay
    return RecurrenceMonthDays.parse(recurrenceWeekdays)
}

/**
 * Stored series anchor when saving a monthly task.
 * - Last / fixed day: [settingOn] (calendar pick); later months use the rule in [RecurrenceEngine].
 * - On this day: first matching day strictly after [settingOn] (may be later this month).
 */
fun monthlyTaskAnchorDate(
    settingOn: JalaliDate,
    rule: RecurrenceMonthDay,
    onThisDayAnchor: JalaliDate = settingOn,
): JalaliDate {
    return when (rule) {
        RecurrenceMonthDay.OnThisDay -> {
            val targetThisMonth = RecurrenceMonthDays.dayInMonth(settingOn, onThisDayAnchor, rule)
            val inThisMonth = JalaliDate(settingOn.year, settingOn.month, targetThisMonth)
            if (inThisMonth > settingOn) {
                inThisMonth
            } else {
                val nextMonth = settingOn.firstOfMonth().addMonths(1)
                val day = RecurrenceMonthDays.dayInMonth(nextMonth, onThisDayAnchor, rule)
                JalaliDate(nextMonth.year, nextMonth.month, day)
            }
        }
        RecurrenceMonthDay.Last, is RecurrenceMonthDay.Fixed -> settingOn
    }
}

fun recurrenceWeekdaysStorage(
    freq: RecurrenceFreq,
    anchor: JalaliDate,
    weekdays: Set<Int>,
    monthDay: RecurrenceMonthDay,
): String = when (freq) {
    RecurrenceFreq.NONE -> ""
    RecurrenceFreq.WEEKLY -> RecurrenceWeekdays.encode(
        weekdays.ifEmpty { setOf(anchor.weekdayIndex()) },
    )
    RecurrenceFreq.MONTHLY -> RecurrenceMonthDays.encode(monthDay)
}
