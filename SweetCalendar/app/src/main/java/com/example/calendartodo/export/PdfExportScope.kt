package com.example.calendartodo.export

/** Which tasks to include in a data export. */
enum class PdfExportScope {
    /** All non-deleted tasks. */
    ALL_ACTIVE,

    /** Non-deleted tasks marked done. */
    COMPLETED,

    /** Non-deleted tasks not yet done. */
    PENDING,

    /** Every task row, including items in the trash. */
    ALL_INCLUDING_DELETED
}
