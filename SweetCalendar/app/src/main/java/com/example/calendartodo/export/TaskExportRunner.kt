package com.example.calendartodo.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Writes exports to cache and supports saving to Downloads or sharing via Android intents.
 */
class TaskExportRunner(
    private val context: Context,
    private val repository: TaskExportRepository
) {
    suspend fun exportToCache(format: ExportFormat, request: PdfExportRequest): ExportCacheResult {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, ExportFileNames.fileName(format))
        val taskCount = repository.exportToFile(format, request, file)
        check(file.exists() && file.length() > 0L) { "Export file was not created" }
        return ExportCacheResult(file = file, taskCount = taskCount, format = format)
    }

    fun saveToDownloads(file: File, format: ExportFormat): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                put(MediaStore.Downloads.MIME_TYPE, format.mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false
            resolver.openOutputStream(uri)?.use { output ->
                file.inputStream().use { input -> input.copyTo(output) }
            } ?: return false
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            return true
        }

        @Suppress("DEPRECATION")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) return false
        val destination = File(downloadsDir, file.name)
        file.copyTo(destination, overwrite = true)
        MediaScannerConnection.scanFile(
            context,
            arrayOf(destination.absolutePath),
            arrayOf(format.mimeType),
            null
        )
        return true
    }

    fun share(file: File, format: ExportFormat) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share export"))
    }
}

data class ExportCacheResult(
    val file: File,
    val taskCount: Int,
    val format: ExportFormat
)
