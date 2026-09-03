package com.example.calendartodo.export

/** Metadata returned after a successful Excel export. */
data class ExcelExportResult(
    val taskCount: Int,
    val rowCount: Int,
    val scope: PdfExportScope
)
