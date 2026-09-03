package com.example.calendartodo.export

import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object ExportFormatting {

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

    fun scopeLabel(scope: PdfExportScope): String = when (scope) {
        PdfExportScope.ALL_ACTIVE -> "All active tasks"
        PdfExportScope.COMPLETED -> "Completed tasks"
        PdfExportScope.PENDING -> "Pending tasks"
        PdfExportScope.ALL_INCLUDING_DELETED -> "All tasks (including trash)"
    }
}
