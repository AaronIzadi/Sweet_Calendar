package com.example.calendartodo.export

import com.example.calendartodo.calendar.CalendarSystem

/** Configuration for a single data export run (PDF or Excel). */
data class PdfExportRequest(
    val scope: PdfExportScope = PdfExportScope.ALL_ACTIVE,
    val calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    val userName: String = "Friend",
    val generatedAtMillis: Long = System.currentTimeMillis()
)
