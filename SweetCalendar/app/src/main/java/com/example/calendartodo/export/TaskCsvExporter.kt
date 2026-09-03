package com.example.calendartodo.export

import com.example.calendartodo.data.local.TaskEntity
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Renders task data into a UTF-8 CSV file (with BOM for Excel compatibility).
 * Call [export] with a prepared task list; filtering is handled upstream.
 */
class TaskCsvExporter {

    fun export(
        request: PdfExportRequest,
        tasks: List<TaskEntity>,
        output: OutputStream
    ): CsvExportResult {
        val sorted = tasks.sortedWith(
            compareByDescending<TaskEntity> { it.jalaliDate }.thenBy { it.createdAt }
        )

        OutputStreamWriter(output, StandardCharsets.UTF_8).use { writer ->
            writer.write(UTF8_BOM)
            writeSummaryComments(writer, request, sorted.size)
            writer.write(CsvEncoding.line(ExportTaskColumns.HEADERS))
            writer.write(LINE_SEPARATOR)
            sorted.forEach { task ->
                writer.write(CsvEncoding.line(ExportTaskColumns.rowValues(task, request)))
                writer.write(LINE_SEPARATOR)
            }
        }

        return CsvExportResult(
            taskCount = sorted.size,
            rowCount = sorted.size + 1,
            scope = request.scope
        )
    }

    private fun writeSummaryComments(
        writer: OutputStreamWriter,
        request: PdfExportRequest,
        taskCount: Int
    ) {
        writer.write("# $APP_NAME$LINE_SEPARATOR")
        writer.write("# Export for ${request.userName}$LINE_SEPARATOR")
        writer.write("# Generated ${ExportFormatting.formatGeneratedAt(request.generatedAtMillis)}$LINE_SEPARATOR")
        writer.write("# Scope: ${ExportFormatting.scopeLabel(request.scope)}$LINE_SEPARATOR")
        writer.write("# $taskCount tasks$LINE_SEPARATOR")
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
