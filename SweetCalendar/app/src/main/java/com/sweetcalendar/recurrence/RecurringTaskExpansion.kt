package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.profile.birthdayTaskFor
import com.sweetcalendar.profile.isBirthdayTask

/**
 * Materialized rows plus projected recurring occurrences (weekly / monthly) for calendar lists.
 */
fun List<TaskEntity>.tasksOnDate(
    date: JalaliDate,
    userBirthDate: JalaliDate? = null,
    userName: String = "Friend",
): List<TaskEntity> {
    val materialized = filter { it.parsedDate() == date && it.isDisplayedMaterializedOccurrence(date, this) }
    if (isEmpty() && userBirthDate == null) return emptyList()
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
        val interval = 1
        val seriesEnd = ruleMembers
            .mapNotNull { it.seriesUntil?.let(JalaliDate::parseIso) }
            .maxOrNull()

        val weekdays = anchorRoot.recurrenceWeekdaySet()
        val monthDay = anchorRoot.recurrenceMonthDayRule()
        if (!occursOnDate(anchor, date, freq, interval, seriesEnd, weekdays, monthDay)) continue

        val maxCount = anchorRoot.recurrenceCount
        if (maxCount != null) {
            val index = RecurrenceEngine.occurrenceIndex(
                anchor,
                date,
                freq,
                interval,
                weekdays,
                monthDay,
            ) ?: continue
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

    val combined = (materialized + virtual).distinctBy { it.occurrenceKey() }
    val birthday = birthdayTaskFor(date, userBirthDate, userName)
    if (birthday == null || combined.any { it.isBirthdayTask() }) return combined
    return combined + birthday
}

fun List<TaskEntity>.tasksByDateInRange(
    start: JalaliDate,
    end: JalaliDate,
    userBirthDate: JalaliDate? = null,
    userName: String = "Friend",
): Map<String, List<TaskEntity>> {
    if (start > end) return emptyMap()
    val map = linkedMapOf<String, List<TaskEntity>>()
    var day = start
    while (day <= end) {
        val onDay = tasksOnDate(day, userBirthDate, userName)
        if (onDay.isNotEmpty()) {
            map[day.formatIso()] = onDay
        }
        day = day.plusDays(1)
    }
    return map
}

private fun TaskEntity.isDisplayedMaterializedOccurrence(
    date: JalaliDate,
    allTasks: List<TaskEntity>,
): Boolean {
    if (isRecurrenceException) return true
    val freq = recurrenceFrequency()
    if (freq == RecurrenceFreq.NONE) return true
    val anchor = seriesAnchor(allTasks) ?: parsedDate()
    return RecurrenceEngine.isOccurrenceInSeries(
        anchor = anchor,
        date = date,
        freq = freq,
        interval = 1,
        weekdays = recurrenceWeekdaySet(),
        monthDay = recurrenceMonthDayRule(),
    )
}

private fun TaskEntity.seriesAnchor(allTasks: List<TaskEntity>): JalaliDate? {
    val sid = seriesId ?: return parsedDate()
    return allTasks
        .filter { it.seriesId == sid && !it.isRecurrenceException }
        .minByOrNull { it.parsedDate() }
        ?.parsedDate()
}

private fun occursOnDate(
    anchor: JalaliDate,
    target: JalaliDate,
    freq: RecurrenceFreq,
    interval: Int,
    seriesEnd: JalaliDate?,
    weekdays: Set<Int>,
    monthDay: RecurrenceMonthDay,
): Boolean {
    if (seriesEnd != null && target > seriesEnd) return false
    return RecurrenceEngine.isOccurrenceInSeries(anchor, target, freq, interval, weekdays, monthDay)
}
