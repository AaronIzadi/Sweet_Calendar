package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate

object RecurrenceWeekdays {

    fun encode(days: Set<Int>): String =
        days.sorted().joinToString(",")

    fun parse(stored: String?, fallbackAnchor: JalaliDate): Set<Int> {
        if (stored.isNullOrBlank()) return setOf(fallbackAnchor.weekdayIndex())
        return stored
            .split(',')
            .mapNotNull { it.trim().toIntOrNull()?.coerceIn(0, 6) }
            .toSet()
            .ifEmpty { setOf(fallbackAnchor.weekdayIndex()) }
    }

    fun label(days: Set<Int>, weekStartsOn: Int = 0): String {
        val order = (0..6).map { (weekStartsOn + it) % 7 }
        return order
            .filter { it in days }
            .map { JalaliDate.WEEKDAY_NAMES_EN_SHORT[it] }
            .joinToString(", ")
    }
}

fun TaskEntity.recurrenceWeekdaySet(): Set<Int> =
    RecurrenceWeekdays.parse(recurrenceWeekdays, parsedDate())
