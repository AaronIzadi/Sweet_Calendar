package com.example.calendartodo.export

/** Metadata returned after a successful CSV export. */
data class CsvExportResult(
    val taskCount: Int,
    val rowCount: Int,
    val scope: PdfExportScope
)
