package com.example.calendartodo.export

import com.example.calendartodo.data.local.TaskEntity

internal object ExportTaskColumns {

    val HEADERS = listOf(
        "Date",
        "Title",
        "Notes",
        "Category",
        "Priority",
        "Status",
        "Reminder",
        "Repeat weekly",
        "Created at",
        "Deleted at"
    )

    fun rowValues(task: TaskEntity, request: PdfExportRequest): List<String> = listOf(
        ExportFormatting.formatTaskDate(task.jalaliDate, request.calendarSystem),
        task.title,
        task.notes,
        task.category,
        task.priority,
        ExportFormatting.taskStatus(task),
        task.reminderTime.orEmpty(),
        if (task.repeatWeekly) "Yes" else "No",
        ExportFormatting.formatCreatedAt(task.createdAt),
        task.deletedAt?.let(ExportFormatting::formatCreatedAt).orEmpty()
    )
}
