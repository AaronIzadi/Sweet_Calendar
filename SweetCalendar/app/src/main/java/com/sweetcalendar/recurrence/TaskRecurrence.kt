package com.sweetcalendar.recurrence

import com.sweetcalendar.data.TaskScheduleKind
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import java.util.UUID

fun TaskEntity.parsedDate(): JalaliDate = JalaliDate.parseIso(jalaliDate)

fun TaskEntity.isVirtualOccurrence(): Boolean = id < 0

/** Stable list key for materialized and projected occurrences. */
fun TaskEntity.occurrenceKey(): String = when {
    isToDo() -> "todo-$id"
    else -> "${seriesId ?: "t$id"}:$jalaliDate"
}

private const val VIRTUAL_DAY_BITS = 20
private const val VIRTUAL_DAY_MASK = (1L shl VIRTUAL_DAY_BITS) - 1

/** Negative synthetic id packing anchor task id + date (reversible). */
fun virtualOccurrenceTaskId(anchorTaskId: Long, date: JalaliDate): Long {
    val safeAnchor = anchorTaskId.coerceAtLeast(1L)
    val dayKey = (date.year * 372L + (date.month - 1) * 31L + (date.day - 1)) and VIRTUAL_DAY_MASK
    return -((safeAnchor shl VIRTUAL_DAY_BITS) or dayKey)
}

/** Anchor row id encoded in a virtual occurrence id, or null if not virtual. */
fun anchorTaskIdFromVirtual(virtualId: Long): Long? {
    if (virtualId >= 0) return null
    val anchor = (-virtualId) shr VIRTUAL_DAY_BITS
    return anchor.takeIf { it > 0L }
}

/** Prefer the row stored for this calendar day; otherwise keep the occurrence passed in. */
fun List<TaskEntity>.resolveOccurrence(task: TaskEntity): TaskEntity {
    val date = task.parsedDate()
    val seriesId = task.seriesId
    if (seriesId != null) {
        find { it.seriesId == seriesId && it.parsedDate() == date && it.id > 0 }?.let { return it }
    }
    if (task.id > 0) {
        if (task.isToDo()) {
            find { it.id == task.id }?.let { return it }
        } else {
            find { it.id == task.id && it.parsedDate() == date }?.let { return it }
        }
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
    monthDay: RecurrenceMonthDay = recurrenceMonthDayRule(),
): TaskEntity {
    val anchor = parsedDate()
    val id = if (freq == RecurrenceFreq.NONE) null else (seriesId ?: UUID.randomUUID().toString())
    return copy(
        seriesId = id,
        recurrenceFreq = freq.name,
        recurrenceInterval = interval,
        recurrenceCount = if (freq == RecurrenceFreq.NONE) null else count,
        recurrenceWeekdays = recurrenceWeekdaysStorage(freq, anchor, weekdays, monthDay),
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
    recurrenceCount: Int? = null,
    recurrenceWeekdays: Set<Int> = emptySet(),
    recurrenceMonthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
    kind: TaskScheduleKind = TaskScheduleKind.fromStored(this.scheduleKind),
): TaskEntity {
    val effectiveFreq = if (kind == TaskScheduleKind.TODO) RecurrenceFreq.NONE else freq
    val storedDate = JalaliDate.parseIso(jalaliDate)
    val weekdayStr = recurrenceWeekdaysStorage(effectiveFreq, storedDate, recurrenceWeekdays, recurrenceMonthDay)
    val base = copy(
        title = title,
        notes = notes,
        jalaliDate = storedDate.formatIso(),
        reminderTime = reminderTime,
        category = category,
        priority = priority,
        repeatWeekly = effectiveFreq == RecurrenceFreq.WEEKLY,
        recurrenceFreq = effectiveFreq.name,
        recurrenceInterval = 1,
        recurrenceCount = if (effectiveFreq == RecurrenceFreq.NONE) null else recurrenceCount,
        recurrenceWeekdays = weekdayStr,
        scheduleKind = kind.name,
    )
    return when {
        effectiveFreq == RecurrenceFreq.NONE -> base.copy(
            seriesId = null,
            recurrenceInterval = 1,
            recurrenceCount = null,
            seriesUntil = null,
            isRecurrenceException = false,
        )
        seriesId == null -> base.withRecurrence(
            effectiveFreq,
            interval = 1,
            count = recurrenceCount,
            weekdays = recurrenceWeekdays.ifEmpty { setOf(storedDate.weekdayIndex()) },
            monthDay = recurrenceMonthDay,
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
        interval = 1,
        weekdays = recurrenceWeekdaySet(),
        monthDay = recurrenceMonthDayRule(),
    )
    val until = seriesUntil?.let { JalaliDate.parseIso(it) }
    if (until != null && next > until) return false
    val max = recurrenceCount
    if (max != null) {
        val seriesAnchor = anchor
        val completedIndex = RecurrenceEngine.occurrenceIndex(
            seriesAnchor,
            parsedDate(),
            freq,
            interval = 1,
            weekdays = recurrenceWeekdaySet(),
            monthDay = recurrenceMonthDayRule(),
        ) ?: return false
        if (completedIndex >= max) return false
    }
    return true
}
