package com.sweetcalendar.profile

import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.virtualOccurrenceTaskId
import com.sweetcalendar.ui.components.TaskCategory
import com.sweetcalendar.ui.components.TaskPriority

const val USER_BIRTHDAY_SERIES_ID = "user-birthday"

fun TaskEntity.isBirthdayTask(): Boolean = seriesId == USER_BIRTHDAY_SERIES_ID

fun birthdayDisplayName(userName: String): String =
    userName.trim().ifBlank { "Friend" }

fun birthdayTaskFor(
    date: JalaliDate,
    birthDate: JalaliDate?,
    userName: String = "Friend",
): TaskEntity? {
    if (birthDate == null) return null
    if (date.month != birthDate.month || date.day != birthDate.day) return null
    val name = birthdayDisplayName(userName)
    return TaskEntity(
        id = virtualOccurrenceTaskId(-42L, date),
        title = "$name's Birthday",
        notes = "Happy Birthday!",
        jalaliDate = date.formatIso(),
        reminderTime = "00:00",
        category = TaskCategory.Personal.label,
        priority = TaskPriority.High.label,
        seriesId = USER_BIRTHDAY_SERIES_ID,
        createdAt = 0L,
    )
}

fun formatUserBirthDateLabel(date: JalaliDate, calendarSystem: CalendarSystem): String {
    return when (calendarSystem) {
        CalendarSystem.PERSIAN -> {
            val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]
            "$month ${date.day}, ${date.year}"
        }
        CalendarSystem.GREGORIAN -> {
            val g = GregorianDate.fromJalali(date)
            val month = GregorianDate.MONTH_NAMES_EN[g.month - 1]
            "$month ${g.day}, ${g.year}"
        }
    }
}
