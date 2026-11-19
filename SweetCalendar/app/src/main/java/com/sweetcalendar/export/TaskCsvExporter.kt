package com.sweetcalendar.export

import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.TaskEntity
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Renders tasks, to-dos, and habits into a UTF-8 CSV file (with BOM for Excel compatibility).
 */
class TaskCsvExporter {

    fun export(
        request: PdfExportRequest,
        tasks: List<TaskEntity>,
        habits: List<HabitWithDays>,
        completionCounts: Map<Long, Int>,
        output: OutputStream
    ): CsvExportResult {
        val scheduled = tasks.filter { !it.isToDo() }.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )
        val todos = tasks.filter { it.isToDo() }.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )
        val sortedHabits = habits.sortedBy { it.name.lowercase() }

        OutputStreamWriter(output, StandardCharsets.UTF_8).use { writer ->
            writer.write(UTF8_BOM)
            writeSummaryComments(writer, request, scheduled.size, todos.size, sortedHabits.size)
            writeActivityBlock(writer, "Tasks", scheduled, request)
            writer.write(LINE_SEPARATOR)
            writeActivityBlock(writer, "To-dos", todos, request)
            writer.write(LINE_SEPARATOR)
            writer.write("# Habits$LINE_SEPARATOR")
            writer.write(CsvEncoding.line(ExportHabitColumns.HEADERS))
            writer.write(LINE_SEPARATOR)
            sortedHabits.forEach { habit ->
                writer.write(
                    CsvEncoding.line(
                        ExportHabitColumns.rowValues(
                            habit = habit,
                            completionCount = completionCounts[habit.id] ?: 0,
                            request = request,
                        )
                    )
                )
                writer.write(LINE_SEPARATOR)
            }
        }

        return CsvExportResult(
            taskCount = scheduled.size,
            todoCount = todos.size,
            habitCount = sortedHabits.size,
            rowCount = scheduled.size + todos.size + sortedHabits.size + 3,
            scope = request.scope
        )
    }

    private fun writeActivityBlock(
        writer: OutputStreamWriter,
        label: String,
        items: List<TaskEntity>,
        request: PdfExportRequest,
    ) {
        writer.write("# $label$LINE_SEPARATOR")
        writer.write(CsvEncoding.line(ExportTaskColumns.HEADERS))
        writer.write(LINE_SEPARATOR)
        items.forEach { task ->
            writer.write(CsvEncoding.line(ExportTaskColumns.rowValues(task, request)))
            writer.write(LINE_SEPARATOR)
        }
    }

    private fun writeSummaryComments(
        writer: OutputStreamWriter,
        request: PdfExportRequest,
        taskCount: Int,
        todoCount: Int,
        habitCount: Int,
    ) {
        writer.write("# $APP_NAME$LINE_SEPARATOR")
        writer.write("# Export for ${request.userName}$LINE_SEPARATOR")
        writer.write("# Generated ${ExportFormatting.formatGeneratedAt(request.generatedAtMillis)}$LINE_SEPARATOR")
        writer.write("# Scope: ${ExportFormatting.scopeLabel(request.scope)}$LINE_SEPARATOR")
        writer.write("# ${ExportFormatting.itemCountLabel(taskCount, todoCount, habitCount)}$LINE_SEPARATOR")
        writer.write(LINE_SEPARATOR)
    }

    companion object {
        private const val APP_NAME = "Sweet Calendar"
        private const val UTF8_BOM = "\uFEFF"
        private const val LINE_SEPARATOR = "\r\n"
    }
}

internal object CsvEncoding {
    fun line(values: List<String>): String = values.joinToString(",") { escape(it) }

    fun escape(value: String): String {
        if (value.none { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            return value
        }
        return "\"${value.replace("\"", "\"\"")}\""
    }
}
