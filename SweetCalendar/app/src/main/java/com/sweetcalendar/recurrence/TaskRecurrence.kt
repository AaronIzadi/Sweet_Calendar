package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import java.util.UUID

fun TaskEntity.parsedDate(): JalaliDate = JalaliDate.parseIso(jalaliDate)

fun TaskEntity.isVirtualOccurrence(): Boolean = id < 0

/** Stable list key for materialized and projected occurrences. */
fun TaskEntity.occurrenceKey(): String = "${seriesId ?: "t$id"}:$jalaliDate"

fun virtualOccurrenceTaskId(anchorTaskId: Long, date: JalaliDate): Long {
    val dayKey = date.year * 10_000 + date.month * 100 + date.day
    val raw = anchorTaskId * 1_000_000L + dayKey
    return if (raw == 0L) -1L else -raw
}

/** Prefer the row stored for this calendar day; otherwise keep the occurrence passed in. */
fun List<TaskEntity>.resolveOccurrence(task: TaskEntity): TaskEntity {
    val date = task.parsedDate()
    val seriesId = task.seriesId
    if (seriesId != null) {
        find { it.seriesId == seriesId && it.parsedDate() == date && it.id > 0 }?.let { return it }
    }
    if (task.id > 0) {
        find { it.id == task.id && it.parsedDate() == date }?.let { return it }
    }
    return task
}

fun TaskEntity.sameOccurrenceAs(other: TaskEntity): Boolean {
    if (parsedDate() != other.parsedDate()) return false
    if (id > 0 && id == other.id) return true
    if (seriesId != null && seriesId == other.seriesId) return true
    return false
}

fun TaskEntity.recurrenceFrequency(): RecurrenceFreq =
    RecurrenceFreq.fromStored(recurrenceFreq).let { stored ->
        if (stored != RecurrenceFreq.NONE) stored
        else RecurrenceFreq.fromLegacyRepeatWeekly(repeatWeekly)
    }

fun TaskEntity.isInRecurrenceSeries(): Boolean =
    seriesId != null && recurrenceFrequency() != RecurrenceFreq.NONE

fun TaskEntity.withRecurrence(
    freq: RecurrenceFreq,
    seriesId: String? = this.seriesId,
    interval: Int = recurrenceInterval.coerceAtLeast(1),
    count: Int? = this.recurrenceCount,
    weekdays: Set<Int> = recurrenceWeekdaySet(),
): TaskEntity {
    val id = if (freq == RecurrenceFreq.NONE) null else (seriesId ?: UUID.randomUUID().toString())
    return copy(
        seriesId = id,
        recurrenceFreq = freq.name,
        recurrenceInterval = interval,
        recurrenceCount = if (freq == RecurrenceFreq.NONE) null else count,
        recurrenceWeekdays = if (freq == RecurrenceFreq.NONE) {
            ""
        } else {
            RecurrenceWeekdays.encode(weekdays)
        },
        repeatWeekly = freq == RecurrenceFreq.WEEKLY,
        isRecurrenceException = if (freq == RecurrenceFreq.NONE) isRecurrenceException else false,
    )
}

fun TaskEntity.applyFormFields(
    title: String,
    notes: String,
    jalaliDate: String,
    reminderTime: String?,
    category: String,
    priority: String,
    freq: RecurrenceFreq,
    recurrenceInterval: Int = 1,
    recurrenceWeekdays: Set<Int> = emptySet(),
): TaskEntity {
    val anchor = JalaliDate.parseIso(jalaliDate)
    val weekdayStr = if (freq == RecurrenceFreq.NONE) {
        ""
    } else {
        RecurrenceWeekdays.encode(
            recurrenceWeekdays.ifEmpty { setOf(anchor.weekdayIndex()) }
        )
    }
    val base = copy(
        title = title,
        notes = notes,
        jalaliDate = jalaliDate,
        reminderTime = reminderTime,
        category = category,
        priority = priority,
        repeatWeekly = freq == RecurrenceFreq.WEEKLY,
        recurrenceFreq = freq.name,
        recurrenceInterval = if (freq == RecurrenceFreq.NONE) 1 else recurrenceInterval.coerceAtLeast(1),
        recurrenceCount = null,
        recurrenceWeekdays = weekdayStr,
    )
    return when {
        freq == RecurrenceFreq.NONE -> base.copy(
            seriesId = null,
            recurrenceInterval = 1,
            recurrenceCount = null,
            seriesUntil = null,
            isRecurrenceException = false,
        )
        seriesId == null -> base.withRecurrence(
            freq,
            interval = recurrenceInterval.coerceAtLeast(1),
            count = null,
            weekdays = recurrenceWeekdays.ifEmpty { setOf(anchor.weekdayIndex()) },
        )
        else -> base
    }
}

fun TaskEntity.canSpawnNextOccurrence(anchor: JalaliDate = parsedDate()): Boolean {
    if (isRecurrenceException) return false
    val freq = recurrenceFrequency()
    if (freq == RecurrenceFreq.NONE) return false
    val next = RecurrenceEngine.nextOccurrence(
        from = parsedDate(),
        anchor = anchor,
        freq = freq,
        interval = recurrenceInterval,
        weekdays = recurrenceWeekdaySet(),
    )
    val until = seriesUntil?.let { JalaliDate.parseIso(it) }
    if (until != null && next > until) return false
    return true
}
