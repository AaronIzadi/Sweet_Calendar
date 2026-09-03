package com.example.calendartodo.export

/** Metadata returned after a successful PDF export. */
data class PdfExportResult(
    val taskCount: Int,
    val pageCount: Int,
    val scope: PdfExportScope
)
