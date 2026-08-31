package com.example.calendartodo.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.PixelPanel
import com.example.calendartodo.ui.components.pixelBorder
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.CherryRed
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CottonCandyPink
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen

data class TaskFormData(
    val title: String,
    val notes: String,
    val reminderTime: String?,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    dayLabel: String,
    existingTask: TaskEntity?,
    onDismiss: () -> Unit,
    onConfirm: (TaskFormData) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEdit = existingTask != null
    var title by remember(existingTask) { mutableStateOf(existingTask?.title.orEmpty()) }
    var notes by remember(existingTask) { mutableStateOf(existingTask?.notes.orEmpty()) }
    var category by remember(existingTask) { mutableStateOf(existingTask?.category.orEmpty()) }
    var reminderEnabled by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime != null)
    }
    var reminderTime by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime ?: "09:00")
    }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTitleError by remember { mutableStateOf(false) }
    val isValid = title.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CottonCandyPink
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            LollipopIcon(size = 40.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                if (isEdit) "Edit a task" else "Add a task",
                style = MaterialTheme.typography.titleMedium,
                color = ChocolateBrown
            )
            Text(
                "Fill the details below to ${if (isEdit) "update" else "add"} your sweet to-do",
                style = MaterialTheme.typography.bodySmall,
                color = ChocolateBrown.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                dayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = BubblegumPink,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PixelTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) showTitleError = false
                },
                label = "Task title",
                placeholder = "What do you need to do?",
                singleLine = true,
                isError = showTitleError
            )
            if (showTitleError) {
                Text(
                    "Please enter a title",
                    style = MaterialTheme.typography.labelSmall,
                    color = CherryRed,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            PixelTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (optional)",
                placeholder = "Add extra details…",
                singleLine = false
            )
            Spacer(Modifier.height(12.dp))
            PixelTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category (optional)",
                placeholder = "e.g. Work, Home, Study",
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            ReminderToggleRow(
                enabled = reminderEnabled,
                reminderTime = reminderTime,
                onToggle = { reminderEnabled = it },
                onPickTime = { showTimePicker = true }
            )
            Spacer(Modifier.height(20.dp))
            RowButtons(
                isValid = isValid,
                confirmLabel = if (isEdit) "Save task" else "Add task",
                onDismiss = onDismiss,
                onConfirm = {
                    if (!isValid) {
                        showTitleError = true
                        return@RowButtons
                    }
                    onConfirm(
                        TaskFormData(
                            title = title.trim(),
                            notes = notes.trim(),
                            reminderTime = reminderTime.takeIf { reminderEnabled },
                            category = category.trim()
                        )
                    )
                }
            )
        }
    }

    if (showTimePicker) {
        val initialParts = reminderTime.split(":")
        val hour = initialParts.getOrNull(0)?.toIntOrNull() ?: 9
        val minute = initialParts.getOrNull(1)?.toIntOrNull() ?: 0
        val timeState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderTime = "%02d:%02d".format(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("OK", color = ChocolateBrown) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = ChocolateBrown)
                }
            },
            title = { Text("Set reminder time", color = ChocolateBrown) },
            text = { TimePicker(state = timeState) }
        )
    }
}

@Composable
private fun ReminderToggleRow(
    enabled: Boolean,
    reminderTime: String,
    onToggle: (Boolean) -> Unit,
    onPickTime: () -> Unit
) {
    Column {
        Text("Reminder", style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PixelButton(
                onClick = { onToggle(!enabled) },
                backgroundColor = if (enabled) MintGreen else LemonYellow
            ) {
                Text(
                    if (enabled) "ON" else "OFF",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown
                )
            }
            if (enabled) {
                PixelButton(
                    onClick = onPickTime,
                    modifier = Modifier.weight(1f),
                    backgroundColor = GrapePurple
                ) {
                    Text(reminderTime, style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
                }
            }
        }
    }
}

@Composable
private fun PixelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    singleLine: Boolean,
    isError: Boolean = false
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = ChocolateBrown),
            cursorBrush = SolidColor(BubblegumPink),
            modifier = Modifier
                .fillMaxWidth()
                .pixelBorder(
                    borderWidth = 2.dp,
                    shadowOffset = 2.dp,
                    borderColor = if (isError) CherryRed else PixelBorder
                )
                .background(if (isError) CherryRed.copy(alpha = 0.08f) else CreamFrosting)
                .padding(10.dp),
            decorationBox = { inner ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.35f)
                    )
                }
                inner()
            }
        )
    }
}

@Composable
private fun RowButtons(
    isValid: Boolean,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PixelButton(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isValid) MintGreen else MintGreen.copy(alpha = 0.4f)
        ) {
            Text(confirmLabel, style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        }
        Spacer(Modifier.height(8.dp))
        PixelButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), backgroundColor = LemonYellow) {
            Text("Cancel", style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        }
    }
}
