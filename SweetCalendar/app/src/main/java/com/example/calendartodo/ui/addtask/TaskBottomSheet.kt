package com.example.calendartodo.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.components.TaskPriority
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.components.formatDisplayShort
import com.example.calendartodo.ui.components.formatTime12h
import com.example.calendartodo.ui.theme.SweetTheme

data class TaskFormData(
    val title: String,
    val notes: String,
    val jalaliDate: JalaliDate,
    val reminderTime: String?,
    val category: String,
    val priority: String,
    val repeatWeekly: Boolean
)

@Composable
fun TaskBottomSheet(
    date: JalaliDate,
    existingTask: TaskEntity?,
    onDismiss: () -> Unit,
    onConfirm: (TaskFormData) -> Unit
) {
    val colors = SweetTheme.colors
    val isEdit = existingTask != null
    var title by remember(existingTask) { mutableStateOf(existingTask?.title.orEmpty()) }
    var notes by remember(existingTask) { mutableStateOf(existingTask?.notes.orEmpty()) }
    var category by remember(existingTask) {
        mutableStateOf(TaskCategory.fromString(existingTask?.category.orEmpty()))
    }
    var priority by remember(existingTask) {
        mutableStateOf(TaskPriority.fromString(existingTask?.priority.orEmpty()))
    }
    var repeatWeekly by remember(existingTask) {
        mutableStateOf(existingTask?.repeatWeekly ?: false)
    }
    var reminderEnabled by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime != null)
    }
    var reminderTime by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime ?: "14:30")
    }
    var selectedDate by remember(existingTask, date) { mutableStateOf(date) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTitleError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.cream)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "✕ CLOSE",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.purpleDeep,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
                Text(
                    if (isEdit) "SAVE ✓" else "SAVE ✓",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.pinkDeep,
                    modifier = Modifier.clickable {
                        if (title.isBlank()) {
                            showTitleError = true
                            return@clickable
                        }
                        onConfirm(
                            TaskFormData(
                                title = title.trim(),
                                notes = notes.trim(),
                                jalaliDate = selectedDate,
                                reminderTime = reminderTime.takeIf { reminderEnabled },
                                category = category.label,
                                priority = priority.label,
                                repeatWeekly = repeatWeekly
                            )
                        )
                    }
                )
            }

            Text(
                if (isEdit) "Edit task" else "New task",
                style = MaterialTheme.typography.titleMedium,
                color = colors.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                FieldLabel("TITLE")
                SweetField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showTitleError = false
                    },
                    placeholder = "e.g. Call mom about Nowruz trip",
                    singleLine = true,
                    isError = showTitleError
                )
                if (showTitleError) {
                    Text(
                        "Please enter a title",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.pinkDeep,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                FieldLabel("DATE & TIME")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SweetField(
                        value = selectedDate.formatDisplayShort(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                        leading = { PeppermintCandyIcon(size = 14.dp) }
                    )
                    SweetField(
                        value = if (reminderEnabled) formatTime12h(reminderTime) else "No time",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (reminderEnabled) {
                                    showTimePicker = true
                                } else {
                                    reminderEnabled = true
                                    showTimePicker = true
                                }
                            },
                        leading = { ChocolateIcon(size = 14.dp) },
                        trailing = if (reminderEnabled) {
                            {
                                Text(
                                    "✕",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.muted,
                                    modifier = Modifier.clickable {
                                        reminderEnabled = false
                                    }
                                )
                            }
                        } else null
                    )
                }

                FieldLabel("CATEGORY")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TaskCategory.entries.forEach { cat ->
                        CategorySwatch(
                            category = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FieldLabel("PRIORITY")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TaskPriority.entries.forEach { p ->
                        PriorityChip(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FieldLabel("REPEAT")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.paper)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Repeat weekly", style = MaterialTheme.typography.bodyMedium, color = colors.ink)
                    SweetSwitch(checked = repeatWeekly, onCheckedChange = { repeatWeekly = it })
                }

                FieldLabel("NOTES")
                SweetField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Add a note (optional)",
                    singleLine = false,
                    minLines = 3
                )

                Spacer(Modifier.height(22.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.pink)
                        .clickable {
                            if (title.isBlank()) {
                                showTitleError = true
                                return@clickable
                            }
                            onConfirm(
                                TaskFormData(
                                    title = title.trim(),
                                    notes = notes.trim(),
                                    jalaliDate = selectedDate,
                                    reminderTime = reminderTime.takeIf { reminderEnabled },
                                    category = category.label,
                                    priority = priority.label,
                                    repeatWeekly = repeatWeekly
                                )
                            )
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isEdit) "SAVE CHANGES" else "ADD TO JAR",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showDatePicker) {
        JalaliDatePickerDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { picked ->
                selectedDate = picked
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            reminderTime = reminderTime,
            onDismiss = { showTimePicker = false },
            onConfirm = { time ->
                reminderTime = time
                reminderEnabled = true
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    reminderTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val colors = SweetTheme.colors
    val initialParts = reminderTime.split(":")
    val hour = initialParts.getOrNull(0)?.toIntOrNull() ?: 14
    val minute = initialParts.getOrNull(1)?.toIntOrNull() ?: 30
    val timeState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm("%02d:%02d".format(timeState.hour, timeState.minute))
            }) { Text("OK", color = colors.ink) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.ink)
            }
        },
        title = { Text("Set time", color = colors.ink) },
        text = { TimePicker(state = timeState) }
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = SweetTheme.colors.muted,
        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun SweetField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    readOnly: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val colors = SweetTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.paper)
            .then(if (isError) Modifier.border(1.dp, colors.pinkDeep, RoundedCornerShape(12.dp)) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
    ) {
        leading?.invoke()
        if (leading != null) Spacer(Modifier.size(8.dp))
        if (readOnly) {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = colors.ink, modifier = Modifier.weight(1f))
        } else {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = minLines,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.ink),
                cursorBrush = SolidColor(colors.pink),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = colors.muted)
                    }
                    inner()
                }
            )
        }
        trailing?.invoke()
    }
}

@Composable
private fun CategorySwatch(
    category: TaskCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.paper)
            .then(
                if (selected) Modifier.border(2.dp, colors.ink, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val dotColor = when (category) {
            TaskCategory.Personal -> colors.pinkDeep
            TaskCategory.Home -> colors.mintDeep
            TaskCategory.Work -> colors.purpleDeep
        }
        Box(Modifier.size(18.dp).clip(RoundedCornerShape(6.dp)).background(dotColor))
        Text(
            category.label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) colors.ink else colors.muted,
            fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.85f
        )
    }
}

@Composable
private fun PriorityChip(
    priority: TaskPriority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) colors.lemon else colors.paper)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (priority == TaskPriority.Medium) {
            Text("✦", style = MaterialTheme.typography.bodySmall, color = colors.lemonDeep)
        }
        Text(
            priority.label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) colors.chocDeep else colors.muted
        )
    }
}

@Composable
private fun SweetSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .size(width = 36.dp, height = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (checked) colors.mintDeep else colors.line)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
