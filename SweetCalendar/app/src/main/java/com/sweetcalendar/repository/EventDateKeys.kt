package com.sweetcalendar.repository

import com.sweetcalendar.data.local.EventCacheEntity
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate

fun jalaliEventStorageKey(date: JalaliDate): String =
    "%04d-%02d-%02d".format(date.year, date.month, date.day)

fun gregorianEventStorageKey(date: GregorianDate): String =
    "%04d-%02d-%02d".format(date.year, date.month, date.day)

fun normalizeCachedJalaliDateKey(raw: String): String {
    val parsed = JalaliDate.parseIso(raw)
    return jalaliEventStorageKey(parsed)
}

fun EventCacheEntity.matchesCalendarSource(calendarSystem: com.sweetcalendar.calendar.CalendarSystem): Boolean =
    when (calendarSystem) {
        com.sweetcalendar.calendar.CalendarSystem.PERSIAN ->
            source == EventCacheEntity.SOURCE_TIME_IR
        com.sweetcalendar.calendar.CalendarSystem.GREGORIAN ->
            source == EventCacheEntity.SOURCE_NAGER
    }
