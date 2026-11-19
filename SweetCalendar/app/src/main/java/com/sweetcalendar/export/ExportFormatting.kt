package com.sweetcalendar.export

import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportFormatting {

    fun formatTaskDate(iso: String, calendarSystem: CalendarSystem): String {
        val jalali = JalaliDate.parseIso(iso)
        return when (calendarSystem) {
            CalendarSystem.PERSIAN -> jalali.formatIso().replace('/', '-')
            CalendarSystem.GREGORIAN -> {
                val g = GregorianDate.fromJalali(jalali)
                "%04d-%02d-%02d".format(g.year, g.month, g.day)
            }
        }
    }

    fun formatGeneratedAt(millis: Long): String {
        val formatter = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.ENGLISH)
        return formatter.format(Date(millis))
    }

    fun formatCreatedAt(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        return formatter.format(Date(millis))
    }

    fun taskStatus(task: TaskEntity): String = when {
        task.deletedAt != null -> "Deleted"
        task.isDone -> "Done"
        else -> "Pending"
    }

    fun habitStatus(habit: HabitWithDays): String =
        if (habit.isEnabled) "Enabled" else "Paused"

    fun scopeLabel(scope: PdfExportScope): String = when (scope) {
        PdfExportScope.ALL_ACTIVE -> "All active tasks, to-dos & habits"
        PdfExportScope.COMPLETED -> "Completed tasks, to-dos & habits"
        PdfExportScope.PENDING -> "Pending tasks, to-dos & habits"
        PdfExportScope.ALL_INCLUDING_DELETED -> "All tasks, to-dos & habits (including trash)"
    }

    fun itemCountLabel(taskCount: Int, todoCount: Int, habitCount: Int): String {
        val taskPart = "$taskCount task${if (taskCount == 1) "" else "s"}"
        val todoPart = "$todoCount to-do${if (todoCount == 1) "" else "s"}"
        val habitPart = "$habitCount habit${if (habitCount == 1) "" else "s"}"
        return "$taskPart · $todoPart · $habitPart"
    }
}
