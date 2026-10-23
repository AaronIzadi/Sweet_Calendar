package com.sweetcalendar.recurrence

enum class RecurrenceFreq {
    NONE,
    WEEKLY,
    MONTHLY,
    ;

    companion object {
        fun fromStored(value: String?): RecurrenceFreq =
            entries.find { it.name == value } ?: NONE

        fun fromLegacyRepeatWeekly(repeatWeekly: Boolean): RecurrenceFreq =
            if (repeatWeekly) WEEKLY else NONE
    }
}

enum class RecurrenceEditScope {
    THIS_INSTANCE,
    THIS_AND_FOLLOWING,
    ENTIRE_SERIES,
}
