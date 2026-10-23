package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate

/**
 * Materialized rows plus projected recurring occurrences (weekly / monthly) for calendar lists.
 */
fun List<TaskEntity>.tasksOnDate(date: JalaliDate): List<TaskEntity> {
    if (isEmpty()) return emptyList()

    val materialized = filter { it.parsedDate() == date }
    val virtual = mutableListOf<TaskEntity>()
    val recurring = filter { it.recurrenceFrequency() != RecurrenceFreq.NONE && !it.isRecurrenceException }
    val groups = recurring.groupBy { it.seriesId ?: "solo-${it.id}" }

    for ((_, members) in groups) {
        val root = members.minByOrNull { it.parsedDate() } ?: continue
        val seriesId = root.seriesId
        if (materialized.any { it.parsedDate() == date && it.seriesId == seriesId && seriesId != null }) {
            continue
        }
        if (seriesId == null && materialized.any { it.id == root.id && it.parsedDate() == date }) {
            continue
        }

        val ruleMembers = members.filter { !it.isRecurrenceException }
        if (ruleMembers.isEmpty()) continue

        val anchorRoot = ruleMembers.minBy { it.parsedDate() }
        val anchor = anchorRoot.parsedDate()
        val freq = anchorRoot.recurrenceFrequency()
        val interval = anchorRoot.recurrenceInterval.coerceAtLeast(1)
        val seriesEnd = ruleMembers
            .mapNotNull { it.seriesUntil?.let(JalaliDate::parseIso) }
            .maxOrNull()

        if (!occursOnDate(anchor, date, freq, interval, seriesEnd)) continue

        val maxCount = anchorRoot.recurrenceCount
        if (maxCount != null) {
            val index = RecurrenceEngine.occurrenceIndex(anchor, date, freq, interval) ?: continue
            if (index > maxCount) continue
        }

        virtual.add(
            anchorRoot.copy(
                id = virtualOccurrenceTaskId(anchorRoot.id, date),
                jalaliDate = date.formatIso(),
                isDone = false,
                completedAt = null,
            )
        )
    }

    return (materialized + virtual).distinctBy { it.occurrenceKey() }
}

fun List<TaskEntity>.tasksByDateInRange(
    start: JalaliDate,
    end: JalaliDate,
): Map<String, List<TaskEntity>> {
    if (start > end) return emptyMap()
    val map = linkedMapOf<String, List<TaskEntity>>()
    var day = start
    while (day <= end) {
        val onDay = tasksOnDate(day)
        if (onDay.isNotEmpty()) {
            map[day.formatIso()] = onDay
        }
        day = day.plusDays(1)
    }
    return map
}

private fun occursOnDate(
    anchor: JalaliDate,
    target: JalaliDate,
    freq: RecurrenceFreq,
    interval: Int,
    seriesEnd: JalaliDate?,
): Boolean {
    if (target < anchor) return false
    if (seriesEnd != null && target > seriesEnd) return false
    return RecurrenceEngine.isOccurrenceInSeries(anchor, target, freq, interval)
}
