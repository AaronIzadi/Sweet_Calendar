package com.example.calendartodo.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.export.ExportCacheResult
import com.example.calendartodo.export.ExportFormat
import com.example.calendartodo.export.PdfExportRequest
import com.example.calendartodo.export.PdfExportScope
import com.example.calendartodo.export.TaskExportRunner
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BackupExportDialog(
    userName: String,
    calendarSystem: CalendarSystem,
    exportRunner: TaskExportRunner,
    onDismiss: () -> Unit
) {
    val colors = SweetTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedFormat by remember { mutableStateOf<ExportFormat?>(null) }
    var isWorking by remember { mutableStateOf(false) }

    fun runExport(action: (ExportCacheResult) -> Unit) {
        val format = selectedFormat ?: return
        if (isWorking) return
        isWorking = true
        scope.launch {
            try {
                val request = PdfExportRequest(
                    scope = PdfExportScope.ALL_INCLUDING_DELETED,
                    calendarSystem = calendarSystem,
                    userName = userName
                )
                val result = withContext(Dispatchers.IO) {
                    exportRunner.exportToCache(format, request)
                }
                withContext(Dispatchers.Main) {
                    action(result)
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isWorking = false
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            top = mockupDp(16),
                            bottom = mockupDp(8)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✕ CLOSE",
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                            lineHeight = mockupSp(14f)
                        ),
                        color = colors.purpleDeep,
                        modifier = Modifier.clickable(enabled = !isWorking, onClick = onDismiss)
                    )
                }

                Text(
                    "Backup & export",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(8)),
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier.padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        bottom = mockupDp(24)
                    )
                ) {
                    ExportFormat.entries.forEach { format ->
                        BackupFormatOption(
                            label = format.label,
                            selected = selectedFormat == format,
                            enabled = !isWorking,
                            onClick = { selectedFormat = format }
                        )
                    }

                    if (selectedFormat != null) {
                        Spacer(Modifier.height(mockupDp(16)))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                mockupDp(10),
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SweetPixelButton(
                                text = if (isWorking) "..." else "DOWNLOAD",
                                onClick = {
                                    runExport { result ->
                                        val saved = exportRunner.saveToDownloads(result.file, result.format)
                                        Toast.makeText(
                                            context,
                                            if (saved) {
                                                "Saved ${result.taskCount} tasks to Downloads"
                                            } else {
                                                "Could not save file"
                                            },
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        if (saved) onDismiss()
                                    }
                                }
                            )
                            SweetPixelButton(
                                text = if (isWorking) "..." else "SHARE",
                                onClick = {
                                    runExport { result ->
                                        exportRunner.share(result.file, result.format)
                                        Toast.makeText(
                                            context,
                                            "Sharing ${result.taskCount} tasks",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BackupFormatOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(mockupDp(10)))
            .background(if (selected) colors.purple.copy(alpha = 0.15f) else colors.paper.copy(alpha = 0f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = mockupDp(12), horizontal = mockupDp(10)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
                lineHeight = mockupSp(18f)
            ),
            color = if (selected) colors.purpleDeep else colors.ink
        )
    }
}
