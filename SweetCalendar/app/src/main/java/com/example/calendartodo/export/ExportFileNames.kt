package com.example.calendartodo.export

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object ExportFileNames {
    fun fileName(format: ExportFormat): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "sweet_calendar_tasks_$timestamp.${format.extension}"
    }
}
