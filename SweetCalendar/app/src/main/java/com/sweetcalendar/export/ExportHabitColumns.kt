package com.sweetcalendar.export

import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.weekDayLabels
import com.sweetcalendar.jalali.JalaliDate

internal object ExportHabitColumns {

    val HEADERS = listOf(
        "Title",
        "Category",
        "Priority",
        "Status",
        "Days",
        "Reminders",
        "Enabled from",
        "Disabled at",
        "Completions",
        "Created at",
    )

    fun rowValues(
        habit: HabitWithDays,
        completionCount: Int,
        request: PdfExportRequest,
    ): List<String> = listOf(
        habit.name,
        habit.habit.category,
        habit.habit.priority,
        ExportFormatting.habitStatus(habit),
        habit.weekDayLabels(),
        reminderSummary(habit),
        ExportFormatting.formatTaskDate(habit.habit.enabledFromJalali, request.calendarSystem),
        habit.habit.disabledAtJalali
            ?.let { ExportFormatting.formatTaskDate(it, request.calendarSystem) }
            .orEmpty(),
        completionCount.toString(),
        ExportFormatting.formatCreatedAt(habit.habit.createdAt),
    )

    private fun reminderSummary(habit: HabitWithDays): String {
        if (habit.days.isEmpty()) return ""
        return habit.days
            .sortedBy { it.weekdayIndex }
            .joinToString(" · ") { day ->
                val label = JalaliDate.WEEKDAY_NAMES_EN_SHORT[day.weekdayIndex]
                val time = day.reminderTime ?: "—"
                "$label $time"
            }
    }
}
