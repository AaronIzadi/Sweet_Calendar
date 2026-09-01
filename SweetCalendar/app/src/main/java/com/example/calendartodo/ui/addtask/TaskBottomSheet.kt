package com.example.calendartodo.ui.addtask

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CalMiniIcon
import com.example.calendartodo.ui.components.ClockMiniIcon
import com.example.calendartodo.ui.components.SparkleIcon
import com.example.calendartodo.ui.components.SweetBigSaveButton
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.components.TaskGemIcon
import com.example.calendartodo.ui.components.TaskHeartIcon
import com.example.calendartodo.ui.components.TaskLeafIcon
import com.example.calendartodo.ui.components.TaskPriority
import com.example.calendartodo.ui.components.formatDisplayShort
import com.example.calendartodo.ui.components.formatTime12h
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val FieldLabelColor = Color(0xFF9A8878)
private val PlaceholderColor = Color(0xFFB7A493)
private val MetaMutedColor = Color(0xFF9A8878)

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
    onConfirm: (TaskFormData) -> Unit,
    modifier: Modifier = Modifier
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

    fun submit() {
        if (title.isBlank()) {
            showTitleError = true
            return
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

    BackHandler(onBack = onDismiss)

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(colors.cream)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(16),
                        bottom = mockupDp(8)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
                Text(
                    "SAVE ✓",
                    style = TextStyle(
                        fontFamily = PixelFont,
                        fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                        lineHeight = mockupSp(14f)
                    ),
                    color = colors.pinkDeep,
                    modifier = Modifier.clickable(onClick = { submit() })
                )
            }

            Text(
                if (isEdit) "Edit task" else "New task",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                    lineHeight = mockupSp(22f)
                ),
                color = colors.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(8),
                        bottom = mockupDp(24)
                    )
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
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(11f)
                        ),
                        color = colors.pinkDeep,
                        modifier = Modifier.padding(top = mockupDp(4))
                    )
                }

                FieldLabel("DATE & TIME")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                ) {
                    SweetField(
                        value = selectedDate.formatDisplayShort(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                        leading = {
                            CalMiniIcon(
                                size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                color = colors.purpleDeep
                            )
                        }
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
                        leading = {
                            ClockMiniIcon(
                                size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                color = colors.purpleDeep
                            )
                        },
                        trailing = if (reminderEnabled) {
                            {
                                Text(
                                    "✕",
                                    style = TextStyle(
                                        fontFamily = BodyFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = mockupSp(11f)
                                    ),
                                    color = colors.muted,
                                    modifier = Modifier.clickable { reminderEnabled = false }
                                )
                            }
                        } else null
                    )
                }

                FieldLabel("CATEGORY")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                ) {
                    TaskCategory.entries.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0))
                        ) {
                            CategorySwatch(
                                category = cat,
                                selected = category == cat,
                                onClick = { category = cat },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                FieldLabel("PRIORITY")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                ) {
                    TaskPriority.entries.forEach { p ->
                        PriorityChip(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0))
                        )
                    }
                }

                FieldLabel("REPEAT")
                PaperSurface(
                    shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
                    cornerRadius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Repeat weekly",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.SWITCH_LABEL_F),
                                lineHeight = mockupSp(17f)
                            ),
                            color = colors.ink
                        )
                        SweetSwitch(checked = repeatWeekly, onCheckedChange = { repeatWeekly = it })
                    }
                }

                FieldLabel("NOTES")
                SweetField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Add a note (optional)",
                    singleLine = false,
                    minLines = 3
                )

                Spacer(Modifier.height(mockupDp(22)))
                SweetBigSaveButton(
                    text = if (isEdit) "SAVE CHANGES" else "ADD TO JAR",
                    onClick = { submit() }
                )
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

@Composable
private fun PaperSurface(
    shadowDepth: Dp,
    cornerRadius: Dp,
    selectedOutline: Boolean = false,
    selectedShadowDepth: Dp? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = SweetTheme.colors
    val depth = if (selectedOutline && selectedShadowDepth != null) selectedShadowDepth else shadowDepth
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depth)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    if (selectedOutline) colors.lemonDeep else colors.line
                )
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(colors.paper)
                .then(
                    if (selectedOutline) {
                        Modifier.border(mockupDp(2), colors.ink, RoundedCornerShape(cornerRadius))
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
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
        text = {
            ProvideMockupScale {
                TimePicker(state = timeState)
            }
        }
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = BodyFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            letterSpacing = mockupSp(0.3f),
            lineHeight = mockupSp(14f)
        ),
        color = FieldLabelColor,
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6))
    )
}

@Composable
private fun FormIconSlot(
    size: Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
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
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val fieldTextStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f)
    )
    PaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = radius,
        modifier = modifier.then(
            if (isError) Modifier.border(mockupDp(1), colors.pinkDeep, RoundedCornerShape(radius)) else Modifier
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
        ) {
            if (leading != null) {
                FormIconSlot(mockupDp(MockupDimens.MINI_FIELD_ICON)) { leading() }
                Spacer(Modifier.size(mockupDp(8)))
            }
            if (readOnly) {
                Text(
                    value,
                    style = fieldTextStyle,
                    color = colors.ink,
                    modifier = Modifier.weight(1f)
                )
            } else {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    minLines = minLines,
                    textStyle = fieldTextStyle.copy(color = colors.ink),
                    cursorBrush = SolidColor(colors.pink),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    decorationBox = { inner ->
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(placeholder, style = fieldTextStyle, color = PlaceholderColor)
                        }
                        inner()
                    }
                )
            }
            trailing?.invoke()
        }
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
    val iconSize = mockupDp(MockupDimens.SWATCH_ICON)
    PaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = mockupDp(MockupDimens.FORM_FIELD_RADIUS),
        selectedOutline = selected,
        selectedShadowDepth = mockupDp(3),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mockupDp(6))
        ) {
            FormIconSlot(iconSize) {
                when (category) {
                    TaskCategory.Personal -> TaskHeartIcon(size = iconSize)
                    TaskCategory.Home -> TaskLeafIcon(size = iconSize)
                    TaskCategory.Work -> TaskGemIcon(size = iconSize)
                }
            }
            Text(
                category.label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F)
                ),
                color = if (selected) colors.ink else MetaMutedColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val sparkleSize = mockupDp(MockupDimens.PRIORITY_SPARKLE_SLOT)
    val chipHeight = mockupDp(MockupDimens.PRIORITY_CHIP_MIN_H)
    val labelStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = mockupSp(MockupDimens.PRIORITY_CHIP_TEXT),
        lineHeight = mockupSp(MockupDimens.PRIORITY_CHIP_LINE.toFloat())
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(chipHeight)
            .clickable(onClick = onClick)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = mockupDp(3))
                    .clip(RoundedCornerShape(radius))
                    .background(colors.lemonDeep)
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                    .clip(RoundedCornerShape(radius))
                    .background(colors.line)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(radius))
                .background(if (selected) colors.lemon else colors.paper)
                .padding(
                    horizontal = mockupDp(MockupDimens.PRIORITY_CHIP_PAD_H),
                    vertical = mockupDp(MockupDimens.PRIORITY_CHIP_PAD_V)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (selected) Arrangement.Top else Arrangement.Center
            ) {
                if (selected) {
                    SparkleIcon(size = sparkleSize)
                    Spacer(Modifier.height(mockupDp(MockupDimens.PRIORITY_CHIP_GAP)))
                }
                Text(
                    priority.label,
                    style = labelStyle,
                    color = if (selected) colors.chocDeep else MetaMutedColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SweetSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .size(width = mockupDp(36), height = mockupDp(20))
            .clip(RoundedCornerShape(mockupDp(10)))
            .background(if (checked) colors.mintDeep else colors.line)
            .clickable { onCheckedChange(!checked) }
            .padding(mockupDp(2))
    ) {
        Box(
            modifier = Modifier
                .size(mockupDp(16))
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
