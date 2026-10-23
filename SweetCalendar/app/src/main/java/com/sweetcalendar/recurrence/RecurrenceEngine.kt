package com.sweetcalendar.recurrence

import com.sweetcalendar.jalali.JalaliDate

object RecurrenceEngine {

    fun nextOccurrence(from: JalaliDate, freq: RecurrenceFreq, interval: Int = 1): JalaliDate {
        val step = interval.coerceAtLeast(1)
        return when (freq) {
            RecurrenceFreq.WEEKLY -> from.plusDays(7 * step)
            RecurrenceFreq.MONTHLY -> from.addMonths(step)
            RecurrenceFreq.NONE -> from
        }
    }

    fun isOccurrenceInSeries(
        anchor: JalaliDate,
        date: JalaliDate,
        freq: RecurrenceFreq,
        interval: Int = 1,
    ): Boolean {
        if (freq == RecurrenceFreq.NONE) return date == anchor
        if (date < anchor) return false
        var cursor = anchor
        var guard = 0
        while (cursor < date && guard < 500) {
            cursor = nextOccurrence(cursor, freq, interval)
            guard++
        }
        return cursor == date
    }

    /** 1-based index of [date] in the series starting at [anchor], or null if not in the series. */
    fun occurrenceIndex(
        anchor: JalaliDate,
        date: JalaliDate,
        freq: RecurrenceFreq,
        interval: Int = 1,
    ): Int? {
        if (!isOccurrenceInSeries(anchor, date, freq, interval)) return null
        var cursor = anchor
        var index = 1
        var guard = 0
        while (cursor < date && guard < 500) {
            cursor = nextOccurrence(cursor, freq, interval)
            index++
            guard++
        }
        return if (cursor == date) index else null
    }
}
