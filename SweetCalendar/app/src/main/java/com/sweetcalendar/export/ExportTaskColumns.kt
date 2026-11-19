package com.sweetcalendar.export

import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.recurrence.repeatSummary

internal object ExportTaskColumns {

    val HEADERS = listOf(
        "Type",
        "Date",
        "Title",
        "Notes",
        "Category",
        "Priority",
        "Status",
        "Reminder",
        "Repeat",
        "Created at",
        "Deleted at"
    )

    fun typeLabel(task: TaskEntity): String =
        if (task.isToDo()) "To-do" else "Task"

    fun rowValues(task: TaskEntity, request: PdfExportRequest): List<String> = listOf(
        typeLabel(task),
        ExportFormatting.formatTaskDate(task.jalaliDate, request.calendarSystem),
        task.title,
        task.notes,
        task.category,
        task.priority,
        ExportFormatting.taskStatus(task),
        task.reminderTime.orEmpty(),
        task.repeatSummary() ?: "No",
        ExportFormatting.formatCreatedAt(task.createdAt),
        task.deletedAt?.let(ExportFormatting::formatCreatedAt).orEmpty()
    )
}
