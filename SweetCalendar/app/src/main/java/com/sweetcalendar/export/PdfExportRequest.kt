package com.sweetcalendar.export

import com.sweetcalendar.calendar.CalendarSystem

/** Configuration for a single data export run (PDF or Excel). */
data class PdfExportRequest(
    val scope: PdfExportScope = PdfExportScope.ALL_ACTIVE,
    val calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    val userName: String = "Friend",
    val generatedAtMillis: Long = System.currentTimeMillis()
)
