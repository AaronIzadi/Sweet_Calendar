package com.sweetcalendar.data

import com.sweetcalendar.data.local.HabitCompletionEntity
import com.sweetcalendar.data.local.HabitDayEntity
import com.sweetcalendar.data.local.HabitEntity
import com.sweetcalendar.jalali.JalaliDate

/** Habit plus its weekday schedules. */
data class HabitWithDays(
    val habit: HabitEntity,
    val days: List<HabitDayEntity>,
) {
    val id: Long get() = habit.id
    val name: String get() = habit.name
    val isEnabled: Boolean get() = habit.isEnabled

    fun dayForWeekday(weekdayIndex: Int): HabitDayEntity? =
        days.firstOrNull { it.weekdayIndex == weekdayIndex }

    fun reminderOn(date: JalaliDate): String? =
        dayForWeekday(date.weekdayIndex())?.reminderTime
}

/** A habit occurrence projected onto a specific calendar day. */
data class HabitOccurrence(
    val habit: HabitWithDays,
    val date: JalaliDate,
    val reminderTime: String?,
    val isDone: Boolean = false,
) {
    val id: Long get() = habit.id
    val name: String get() = habit.name
    val isEnabled: Boolean get() = habit.isEnabled
    val key: String get() = "${habit.id}:${date.formatIso()}"
}

fun HabitWithDays.appearsOn(date: JalaliDate): Boolean {
    if (days.none { it.weekdayIndex == date.weekdayIndex() }) return false
    val start = JalaliDate.parseIso(habit.enabledFromJalali)
    if (date < start) return false
    if (habit.isEnabled) return true
    val endIso = habit.disabledAtJalali ?: return false
    val end = JalaliDate.parseIso(endIso)
    return date <= end
}

fun HabitWithDays.occurrenceOn(
    date: JalaliDate,
    completedHabitIds: Set<Long> = emptySet(),
): HabitOccurrence? {
    if (!appearsOn(date)) return null
    return HabitOccurrence(
        habit = this,
        date = date,
        reminderTime = reminderOn(date),
        isDone = id in completedHabitIds,
    )
}

fun List<HabitWithDays>.habitsOnDate(
    date: JalaliDate,
    completedHabitIds: Set<Long> = emptySet(),
): List<HabitOccurrence> =
    mapNotNull { it.occurrenceOn(date, completedHabitIds) }
        .sortedWith(
            compareBy(
                { it.isDone },
                { it.reminderTime.orEmpty() },
                { it.name.lowercase() },
            ),
        )

fun List<HabitWithDays>.habitsInWeek(weekDays: List<JalaliDate>): List<HabitWithDays> {
    val weekWeekdays = weekDays.map { it.weekdayIndex() }.toSet()
    return filter { habit ->
        habit.days.any { day -> day.weekdayIndex in weekWeekdays } &&
            weekDays.any { habit.appearsOn(it) }
    }.sortedBy { it.name.lowercase() }
}

fun List<HabitWithDays>.hasHabitOn(date: JalaliDate): Boolean =
    any { it.appearsOn(date) }

fun List<HabitCompletionEntity>.habitIdsCompletedOn(date: JalaliDate): Set<Long> {
    val iso = date.formatIso()
    return filter { it.jalaliDate == iso }.map { it.habitId }.toSet()
}

fun List<HabitCompletionEntity>.completionPairs(): Set<Pair<Long, String>> =
    map { it.habitId to it.jalaliDate }.toSet()

/**
 * True when every scheduled day in [rangeDays] on or before [through]
 * has a completion for this habit. Habits with no due days yet are not complete.
 */
fun HabitWithDays.isCompletedThrough(
    rangeDays: List<JalaliDate>,
    completedPairs: Set<Pair<Long, String>>,
    through: JalaliDate = JalaliDate.today(),
): Boolean {
    val due = rangeDays.filter { appearsOn(it) && it <= through }
    if (due.isEmpty()) return false
    return due.all { (id to it.formatIso()) in completedPairs }
}

/** Short weekday labels for habit cards (no reminder count). */
fun HabitWithDays.weekDayLabels(): String {
    if (days.isEmpty()) return "No days"
    return days
        .sortedBy { it.weekdayIndex }
        .joinToString(" · ") { JalaliDate.WEEKDAY_NAMES_EN_SHORT[it.weekdayIndex] }
}
