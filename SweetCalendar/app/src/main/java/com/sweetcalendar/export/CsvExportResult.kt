package com.sweetcalendar.export

/** Metadata returned after a successful CSV export. */
data class CsvExportResult(
    val taskCount: Int,
    val todoCount: Int,
    val habitCount: Int,
    val rowCount: Int,
    val scope: PdfExportScope
)
