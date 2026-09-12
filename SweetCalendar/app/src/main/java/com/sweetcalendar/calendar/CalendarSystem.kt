package com.sweetcalendar.calendar

enum class CalendarSystem(val label: String) {
    PERSIAN("Persian"),
    GREGORIAN("Gregorian");

    companion object {
        fun fromPref(value: String): CalendarSystem =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: PERSIAN
    }
}
