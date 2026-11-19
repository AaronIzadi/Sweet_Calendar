package com.sweetcalendar.export

import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.TaskEntity
import org.dhatim.fastexcel.Workbook
import java.io.OutputStream

/**
 * Renders tasks, to-dos, and habits into an .xlsx workbook via [Workbook].
 */
class TaskExcelExporter {

    fun export(
        request: PdfExportRequest,
        tasks: List<TaskEntity>,
        habits: List<HabitWithDays>,
        completionCounts: Map<Long, Int>,
        output: OutputStream
    ): ExcelExportResult {
        val scheduled = tasks.filter { !it.isToDo() }.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )
        val todos = tasks.filter { it.isToDo() }.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )
        val sortedHabits = habits.sortedBy { it.name.lowercase() }

        val workbook = Workbook(output, APP_NAME, VERSION)
        writeTaskKindSheet(workbook, TASKS_SHEET, request, scheduled, countLabel = "tasks")
        writeTaskKindSheet(workbook, TODOS_SHEET, request, todos, countLabel = "to-dos")
        writeHabitsSheet(workbook, request, sortedHabits, completionCounts)
        workbook.finish()

        return ExcelExportResult(
            taskCount = scheduled.size,
            todoCount = todos.size,
            habitCount = sortedHabits.size,
            rowCount = (SUMMARY_ROW_COUNT + 1) * 3 + scheduled.size + todos.size + sortedHabits.size,
            scope = request.scope
        )
    }

    private fun writeTaskKindSheet(
        workbook: Workbook,
        sheetName: String,
        request: PdfExportRequest,
        items: List<TaskEntity>,
        countLabel: String,
    ) {
        val sheet = workbook.newWorksheet(sheetName)
        writeSummaryRows(sheet, request, "${items.size} $countLabel")
        val headerRow = SUMMARY_ROW_COUNT
        ExportTaskColumns.HEADERS.forEachIndexed { column, title ->
            sheet.value(headerRow, column, title)
            sheet.style(headerRow, column).bold().set()
        }
        items.forEachIndexed { index, task ->
            ExportTaskColumns.rowValues(task, request).forEachIndexed { column, value ->
                sheet.value(headerRow + 1 + index, column, value)
            }
        }
        if (items.isNotEmpty()) {
            sheet.setAutoFilter(headerRow, 0, headerRow + items.size, ExportTaskColumns.HEADERS.size - 1)
        }
    }

    private fun writeHabitsSheet(
        workbook: Workbook,
        request: PdfExportRequest,
        habits: List<HabitWithDays>,
        completionCounts: Map<Long, Int>,
    ) {
        val sheet = workbook.newWorksheet(HABITS_SHEET)
        writeSummaryRows(sheet, request, "${habits.size} habits")
        val headerRow = SUMMARY_ROW_COUNT
        ExportHabitColumns.HEADERS.forEachIndexed { column, title ->
            sheet.value(headerRow, column, title)
            sheet.style(headerRow, column).bold().set()
        }
        habits.forEachIndexed { index, habit ->
            ExportHabitColumns.rowValues(
                habit = habit,
                completionCount = completionCounts[habit.id] ?: 0,
                request = request,
            ).forEachIndexed { column, value ->
                sheet.value(headerRow + 1 + index, column, value)
            }
        }
        if (habits.isNotEmpty()) {
            sheet.setAutoFilter(headerRow, 0, headerRow + habits.size, ExportHabitColumns.HEADERS.size - 1)
        }
    }

    private fun writeSummaryRows(
        sheet: org.dhatim.fastexcel.Worksheet,
        request: PdfExportRequest,
        countLine: String,
    ) {
        sheet.value(0, 0, APP_NAME)
        sheet.style(0, 0).bold().set()
        sheet.value(1, 0, "Export for ${request.userName}")
        sheet.value(2, 0, "Generated ${ExportFormatting.formatGeneratedAt(request.generatedAtMillis)}")
        sheet.value(3, 0, "Scope: ${ExportFormatting.scopeLabel(request.scope)}")
        sheet.value(4, 0, countLine)
    }

    companion object {
        private const val APP_NAME = "Sweet Calendar"
        private const val VERSION = "1.0"
        private const val TASKS_SHEET = "Tasks"
        private const val TODOS_SHEET = "To-dos"
        private const val HABITS_SHEET = "Habits"
        private const val SUMMARY_ROW_COUNT = 6
    }
}
