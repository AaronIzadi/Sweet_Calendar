package com.sweetcalendar.export

/** Metadata returned after a successful PDF export. */
data class PdfExportResult(
    val taskCount: Int,
    val todoCount: Int,
    val habitCount: Int,
    val pageCount: Int,
    val scope: PdfExportScope
)
