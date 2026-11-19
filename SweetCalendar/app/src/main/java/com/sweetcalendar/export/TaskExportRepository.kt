package com.sweetcalendar.export

import android.content.Context
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.local.HabitDao
import com.sweetcalendar.data.local.TaskDao
import com.sweetcalendar.data.local.TaskEntity
import java.io.File
import java.io.OutputStream

/**
 * Loads tasks and habits from the database and writes them to PDF, Excel, or CSV exporters.
 */
class TaskExportRepository(
    context: Context,
    private val taskDao: TaskDao,
    private val habitDao: HabitDao,
) {
    private val pdfExporter = TaskPdfExporter(context.applicationContext)
    private val excelExporter = TaskExcelExporter()
    private val csvExporter = TaskCsvExporter()

    suspend fun exportToPdf(request: PdfExportRequest, output: OutputStream): PdfExportResult {
        val payload = loadExportPayload(request.scope)
        return pdfExporter.export(request, payload.tasks, payload.habits, payload.completionCounts, output)
    }

    suspend fun exportToPdfFile(request: PdfExportRequest, destination: File): PdfExportResult {
        destination.parentFile?.mkdirs()
        return destination.outputStream().use { stream ->
            exportToPdf(request, stream)
        }
    }

    suspend fun exportToExcel(request: PdfExportRequest, output: OutputStream): ExcelExportResult {
        val payload = loadExportPayload(request.scope)
        return excelExporter.export(request, payload.tasks, payload.habits, payload.completionCounts, output)
    }

    suspend fun exportToExcelFile(request: PdfExportRequest, destination: File): ExcelExportResult {
        destination.parentFile?.mkdirs()
        return destination.outputStream().use { stream ->
            exportToExcel(request, stream)
        }
    }

    suspend fun exportToCsv(request: PdfExportRequest, output: OutputStream): CsvExportResult {
        val payload = loadExportPayload(request.scope)
        return csvExporter.export(request, payload.tasks, payload.habits, payload.completionCounts, output)
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
    ): ExportCounts {
        destination.parentFile?.mkdirs()
        return when (format) {
            ExportFormat.PDF -> {
                val result = exportToPdfFile(request, destination)
                ExportCounts(result.taskCount, result.todoCount, result.habitCount)
            }
            ExportFormat.EXCEL -> {
                val result = exportToExcelFile(request, destination)
                ExportCounts(result.taskCount, result.todoCount, result.habitCount)
            }
            ExportFormat.CSV -> {
                val result = exportToCsvFile(request, destination)
                ExportCounts(result.taskCount, result.todoCount, result.habitCount)
            }
        }
    }

    private data class ExportPayload(
        val tasks: List<TaskEntity>,
        val habits: List<HabitWithDays>,
        val completionCounts: Map<Long, Int>,
    )

    private suspend fun loadExportPayload(scope: PdfExportScope): ExportPayload {
        val tasks = loadTasks(scope)
        val habits = loadHabits(scope)
        val completions = habitDao.getAllCompletions()
            .groupingBy { it.habitId }
            .eachCount()
        return ExportPayload(tasks, habits, completions)
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

    private suspend fun loadHabits(@Suppress("UNUSED_PARAMETER") scope: PdfExportScope): List<HabitWithDays> {
        val habits = habitDao.getAll()
        val days = habitDao.getAllDays().groupBy { it.habitId }
        return habits.map { habit ->
            HabitWithDays(
                habit = habit,
                days = days[habit.id].orEmpty().sortedBy { it.weekdayIndex },
            )
        }.sortedBy { it.name.lowercase() }
    }
}
