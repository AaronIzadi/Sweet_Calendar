package com.sweetcalendar.recurrence

/** User-facing N: how many weeks/months after the first period. */
fun recurrenceFollowingToTotalPeriods(following: Int): Int =
    (following + 1).coerceAtLeast(1)

fun recurrenceTotalToFollowingPeriods(total: Int): Int =
    (total - 1).coerceAtLeast(0)

fun recurrenceWeekdayCount(weekdays: Set<Int>): Int = weekdays.size.coerceAtLeast(1)

/** Stored [com.sweetcalendar.data.local.TaskEntity.recurrenceCount] for weekly = weeks × selected days. */
fun recurrenceStoredCountFromFollowing(
    freq: RecurrenceFreq,
    following: Int?,
    weekdays: Set<Int>,
): Int? {
    if (following == null) return null
    val periods = recurrenceFollowingToTotalPeriods(following)
    return when (freq) {
        RecurrenceFreq.WEEKLY -> periods * recurrenceWeekdayCount(weekdays)
        RecurrenceFreq.MONTHLY -> periods
        RecurrenceFreq.NONE -> null
    }
}

fun recurrenceFollowingFromStored(
    freq: RecurrenceFreq,
    storedTotal: Int,
    weekdays: Set<Int>,
): Int {
    val periods = when (freq) {
        RecurrenceFreq.WEEKLY -> {
            val days = recurrenceWeekdayCount(weekdays)
            (storedTotal / days).coerceAtLeast(1)
        }
        RecurrenceFreq.MONTHLY -> storedTotal
        RecurrenceFreq.NONE -> 1
    }
    return recurrenceTotalToFollowingPeriods(periods)
}
