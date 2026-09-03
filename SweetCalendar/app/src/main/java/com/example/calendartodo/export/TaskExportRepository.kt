package com.example.calendartodo.export

import android.content.Context
import com.example.calendartodo.data.local.TaskDao
import com.example.calendartodo.data.local.TaskEntity
import java.io.File
import java.io.OutputStream

/**
 * Loads tasks from the database and writes them to PDF, Excel, or CSV exporters.
 */
class TaskExportRepository(
    context: Context,
    private val taskDao: TaskDao
) {
    private val pdfExporter = TaskPdfExporter(context.applicationContext)
    private val excelExporter = TaskExcelExporter()
    private val csvExporter = TaskCsvExporter()

    suspend fun exportToPdf(request: PdfExportRequest, output: OutputStream): PdfExportResult {
        val tasks = loadTasks(request.scope)
        return pdfExporter.export(request, tasks, output)
    }

    suspend fun exportToPdfFile(request: PdfExportRequest, destination: File): PdfExportResult {
        destination.parentFile?.mkdirs()
        return destination.outputStream().use { stream ->
            exportToPdf(request, stream)
        }
    }

    suspend fun exportToExcel(request: PdfExportRequest, output: OutputStream): ExcelExportResult {
        val tasks = loadTasks(request.scope)
        return excelExporter.export(request, tasks, output)
    }

    suspend fun exportToExcelFile(request: PdfExportRequest, destination: File): ExcelExportResult {
        destination.parentFile?.mkdirs()
        return destination.outputStream().use { stream ->
            exportToExcel(request, stream)
        }
    }

    suspend fun exportToCsv(request: PdfExportRequest, output: OutputStream): CsvExportResult {
        val tasks = loadTasks(request.scope)
        return csvExporter.export(request, tasks, output)
    }

    suspend fun exportToCsvFile(request: PdfExportRequest, destination: File): CsvExportResult {
        destination.parentFile?.mkdirs()
        return destination.outputStream().use { stream ->
            exportToCsv(request, stream)
        }
    }

    /** Routes to the PDF, Excel, or CSV file generator and writes [destination]. */
    suspend fun exportToFile(
        format: ExportFormat,
        request: PdfExportRequest,
        destination: File
    ): Int {
        destination.parentFile?.mkdirs()
        return when (format) {
            ExportFormat.PDF -> exportToPdfFile(request, destination).taskCount
            ExportFormat.EXCEL -> exportToExcelFile(request, destination).taskCount
            ExportFormat.CSV -> exportToCsvFile(request, destination).taskCount
        }
    }

    private suspend fun loadTasks(scope: PdfExportScope): List<TaskEntity> {
        val rows = when (scope) {
            PdfExportScope.ALL_INCLUDING_DELETED -> taskDao.getAllIncludingDeleted()
            else -> taskDao.getAllActive()
        }
        return when (scope) {
            PdfExportScope.ALL_ACTIVE,
            PdfExportScope.ALL_INCLUDING_DELETED -> rows
            PdfExportScope.COMPLETED -> rows.filter { it.isDone }
            PdfExportScope.PENDING -> rows.filter { !it.isDone }
        }
    }
}
