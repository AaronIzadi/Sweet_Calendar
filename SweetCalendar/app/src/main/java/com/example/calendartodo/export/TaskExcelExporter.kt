package com.example.calendartodo.export

import com.example.calendartodo.data.local.TaskEntity
import org.dhatim.fastexcel.Workbook
import java.io.OutputStream

/**
 * Renders task data into an .xlsx workbook via [Workbook].
 * Call [export] with a prepared task list; filtering is handled upstream.
 */
class TaskExcelExporter {

    fun export(
        request: PdfExportRequest,
        tasks: List<TaskEntity>,
        output: OutputStream
    ): ExcelExportResult {
        val sorted = tasks.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )

        val workbook = Workbook(output, APP_NAME, VERSION)
        val sheet = workbook.newWorksheet(SHEET_NAME)

        writeSummaryRows(sheet, request, sorted.size)
        val headerRow = SUMMARY_ROW_COUNT
        writeHeaderRow(sheet, headerRow)

        sorted.forEachIndexed { index, task ->
            writeTaskRow(sheet, headerRow + 1 + index, task, request)
        }

        sheet.setAutoFilter(headerRow, 0, headerRow + sorted.size, COLUMN_COUNT - 1)
        workbook.finish()

        return ExcelExportResult(
            taskCount = sorted.size,
            rowCount = headerRow + 1 + sorted.size,
            scope = request.scope
        )
    }

    private fun writeSummaryRows(sheet: org.dhatim.fastexcel.Worksheet, request: PdfExportRequest, taskCount: Int) {
        sheet.value(0, 0, APP_NAME)
        sheet.style(0, 0).bold().set()
        sheet.value(1, 0, "Export for ${request.userName}")
        sheet.value(2, 0, "Generated ${ExportFormatting.formatGeneratedAt(request.generatedAtMillis)}")
        sheet.value(3, 0, "Scope: ${ExportFormatting.scopeLabel(request.scope)}")
        sheet.value(4, 0, "$taskCount tasks")
    }

    private fun writeHeaderRow(sheet: org.dhatim.fastexcel.Worksheet, row: Int) {
        ExportTaskColumns.HEADERS.forEachIndexed { column, title ->
            sheet.value(row, column, title)
            sheet.style(row, column).bold().set()
        }
    }

    private fun writeTaskRow(
        sheet: org.dhatim.fastexcel.Worksheet,
        row: Int,
        task: TaskEntity,
        request: PdfExportRequest
    ) {
        ExportTaskColumns.rowValues(task, request).forEachIndexed { column, value ->
            sheet.value(row, column, value)
        }
    }

    companion object {
        private const val APP_NAME = "Sweet Calendar"
        private const val VERSION = "1.0"
        private const val SHEET_NAME = "Tasks"
        private const val SUMMARY_ROW_COUNT = 6
        private val COLUMN_COUNT = ExportTaskColumns.HEADERS.size
    }
}
