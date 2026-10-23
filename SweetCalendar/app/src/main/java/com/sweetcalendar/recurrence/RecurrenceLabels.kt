package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity

fun RecurrenceFreq.userLabel(): String = when (this) {
    RecurrenceFreq.NONE -> "None"
    RecurrenceFreq.WEEKLY -> "Weekly"
    RecurrenceFreq.MONTHLY -> "Monthly"
}

fun TaskEntity.repeatSummary(): String? {
    val base = when (recurrenceFrequency()) {
        RecurrenceFreq.NONE -> return null
        RecurrenceFreq.WEEKLY -> "Repeats weekly"
        RecurrenceFreq.MONTHLY -> "Repeats monthly"
    }
    val count = recurrenceCount
    return if (count == null) base else "$base, $count times"
}
