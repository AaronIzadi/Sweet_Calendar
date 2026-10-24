package com.sweetcalendar.recurrence

import com.sweetcalendar.jalali.JalaliDate
import java.util.concurrent.TimeUnit

object RecurrenceEngine {

    fun resolvedWeekdays(anchor: JalaliDate, weekdays: Set<Int>): Set<Int> =
        weekdays.ifEmpty { setOf(anchor.weekdayIndex()) }

    fun nextOccurrence(
        from: JalaliDate,
        anchor: JalaliDate,
        freq: RecurrenceFreq,
        interval: Int = 1,
        weekdays: Set<Int> = emptySet(),
        monthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
    ): JalaliDate {
        val step = interval.coerceAtLeast(1)
        val days = resolvedWeekdays(anchor, weekdays)
        if (freq == RecurrenceFreq.NONE) return from
        var cursor = from.plusDays(1)
        repeat(800) {
            if (isOccurrenceInSeries(anchor, cursor, freq, step, days, monthDay)) return cursor
            cursor = cursor.plusDays(1)
        }
        return when (freq) {
            RecurrenceFreq.WEEKLY -> from.plusDays(7 * step)
            RecurrenceFreq.MONTHLY -> from.addMonths(step)
            RecurrenceFreq.NONE -> from
        }
    }

    /** @param weekdays empty set uses [anchor]'s weekday only */
    fun isOccurrenceInSeries(
        anchor: JalaliDate,
        date: JalaliDate,
        freq: RecurrenceFreq,
        interval: Int = 1,
        weekdays: Set<Int> = emptySet(),
        monthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
    ): Boolean {
        val step = interval.coerceAtLeast(1)
        val days = resolvedWeekdays(anchor, weekdays)
        return when (freq) {
            RecurrenceFreq.NONE -> date == anchor
            RecurrenceFreq.WEEKLY -> isWeeklyOccurrence(anchor, date, days, step)
            RecurrenceFreq.MONTHLY -> isMonthlyDayOccurrence(anchor, date, monthDay, step)
        }
    }

    fun occurrenceIndex(
        anchor: JalaliDate,
        date: JalaliDate,
        freq: RecurrenceFreq,
        interval: Int = 1,
        weekdays: Set<Int> = emptySet(),
        monthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
    ): Int? {
        if (!isOccurrenceInSeries(anchor, date, freq, interval, weekdays, monthDay)) return null
        var index = 0
        var cursor = anchor
        var guard = 0
        while (cursor <= date && guard < 2000) {
            if (isOccurrenceInSeries(anchor, cursor, freq, interval, weekdays, monthDay)) {
                index++
                if (cursor == date) return index
            }
            cursor = cursor.plusDays(1)
            guard++
        }
        return null
    }

    private fun weekStartSaturday(date: JalaliDate): JalaliDate =
        date.minusDays(date.weekdayIndex())

    private fun daysBetween(start: JalaliDate, end: JalaliDate): Int {
        val startMs = start.toGregorianCalendar().timeInMillis
        val endMs = end.toGregorianCalendar().timeInMillis
        return TimeUnit.MILLISECONDS.toDays(endMs - startMs).toInt()
    }

    private fun isWeeklyOccurrence(
        anchor: JalaliDate,
        date: JalaliDate,
        weekdays: Set<Int>,
        interval: Int,
    ): Boolean {
        if (date < anchor) return false
        if (date.weekdayIndex() !in weekdays) return false
        val anchorWeek = weekStartSaturday(anchor)
        val dateWeek = weekStartSaturday(date)
        if (dateWeek < anchorWeek) return false
        val weeksApart = daysBetween(anchorWeek, dateWeek) / 7
        return weeksApart % interval == 0
    }

    private fun isMonthlyDayOccurrence(
        anchor: JalaliDate,
        date: JalaliDate,
        monthDay: RecurrenceMonthDay,
        interval: Int,
    ): Boolean {
        if (date < anchor) return false
        val monthsApart = (date.year - anchor.year) * 12 + (date.month - anchor.month)
        if (monthsApart < 0 || monthsApart % interval != 0) return false
        if (date.year == anchor.year && date.month == anchor.month && date < anchor) return false
        return when (monthDay) {
            RecurrenceMonthDay.OnThisDay -> {
                val targetDay = RecurrenceMonthDays.dayInMonth(date, anchor, monthDay)
                date.day == targetDay
            }
            RecurrenceMonthDay.Last, is RecurrenceMonthDay.Fixed -> {
                if (date.year == anchor.year && date.month == anchor.month) {
                    date == anchor
                } else {
                    val targetDay = RecurrenceMonthDays.dayInMonth(date, anchor, monthDay)
                    date.day == targetDay
                }
            }
        }
    }
}
