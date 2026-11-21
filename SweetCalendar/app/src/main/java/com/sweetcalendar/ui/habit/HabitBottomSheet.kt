package com.sweetcalendar.ui.habit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.addtask.ActivitySheetKind
import com.sweetcalendar.ui.addtask.SweetTimePickerDialog
import com.sweetcalendar.ui.addtask.TaskPriorityChip
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.ClosePixelIcon
import com.sweetcalendar.ui.components.ScrollColumnWithBottomAction
import com.sweetcalendar.ui.components.SheetFooterBar
import com.sweetcalendar.ui.components.SweetBigSaveButton
import com.sweetcalendar.ui.components.SweetTimedSnackbar
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.components.showKeyboardOnFocus
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.formChipFillColor
import com.sweetcalendar.ui.theme.formChipShadowColor
import com.sweetcalendar.ui.theme.formChipShadowLayer
import com.sweetcalendar.ui.theme.formChipTextColor
import com.sweetcalendar.ui.theme.formPlaceholderColor
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.rememberReducedMotion
import com.sweetcalendar.ui.theme.themeAddTaskButtonText

data class HabitFormData(
    val name: String,
    /** Weekday index → reminder time ("HH:mm") or null for no reminder. */
    val dayReminders: Map<Int, String?>,
    val category: HabitCategory = HabitCategory.Personal,
    val priority: TaskPriority = TaskPriority.Medium,
)

@Composable
fun HabitBottomSheet(
    existing: HabitWithDays? = null,
    weekStartsOn: Int = 0,
    seedWeekday: Int = JalaliDate.today().weekdayIndex(),
    onDismiss: () -> Unit,
    onSwitchKind: ((ActivitySheetKind) -> Unit)? = null,
    onConfirm: (HabitFormData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val reducedMotion = rememberReducedMotion()
    val isEdit = existing != null
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var name by remember(existing) { mutableStateOf(existing?.name.orEmpty()) }
    var category by remember(existing) {
        mutableStateOf(HabitCategory.fromString(existing?.habit?.category.orEmpty()))
    }
    var priority by remember(existing) {
        mutableStateOf(TaskPriority.fromString(existing?.habit?.priority.orEmpty()))
    }
    var dayReminders by remember(existing, seedWeekday) {
        mutableStateOf(
            existing?.days?.associate { it.weekdayIndex to it.reminderTime }
                ?: mapOf(seedWeekday to "08:00")
        )
    }
    var showTitleError by remember { mutableStateOf(false) }
    var showDaysError by remember { mutableStateOf(false) }
    var errorSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var timePickerWeekday by remember { mutableStateOf<Int?>(null) }
    var titleFocused by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    fun submit() {
        if (name.isBlank()) {
            showTitleError = true
            errorSnackbarMessage = "Please enter a title"
            return
        }
        if (dayReminders.isEmpty()) {
            showDaysError = true
            errorSnackbarMessage = "Pick at least one day"
            return
        }
        onConfirm(
            HabitFormData(
                name = name.trim(),
                dayReminders = dayReminders,
                category = category,
                priority = priority,
            )
        )
    }

    BackHandler {
        if (titleFocused) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        } else {
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(colors.cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            AnimatedContent(
                targetState = isEdit,
                transitionSpec = {
                    fadeIn(SweetMotion.shortTween()) togetherWith fadeOut(SweetMotion.shortTween())
                },
                label = "habitSheetTitle",
                modifier = Modifier
                    .fillMaxWidth()
                    .then(sheetDialogTitlePaddingModifier()),
            ) { editing ->
                Text(
                    if (editing) "Edit habit" else "New habit",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f),
                    ),
                    color = colors.ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (!isEdit && onSwitchKind != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mockupDp(18), vertical = mockupDp(8)),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                ) {
                    HabitKindChip(
                        label = "Task",
                        selected = false,
                        onClick = { onSwitchKind(ActivitySheetKind.TASK) },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = mockupDp(0)),
                    )
                    HabitKindChip(
                        label = "To Do",
                        selected = false,
                        onClick = { onSwitchKind(ActivitySheetKind.TODO) },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = mockupDp(0)),
                    )
                    HabitKindChip(
                        label = "Habit",
                        selected = true,
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = mockupDp(0)),
                    )
                }
            }

            ScrollColumnWithBottomAction(
                scrollState = scrollState,
                bottomAction = {
                    SweetBigSaveButton(
                        text = if (isEdit) "SAVE CHANGES" else themeAddTaskButtonText(isEdit = false),
                        onClick = { submit() },
                    )
                },
            ) {
                HabitFieldLabel("TITLE")
                HabitSweetField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) showTitleError = false
                    },
                    placeholder = "e.g. Morning stretch",
                    singleLine = true,
                    isError = showTitleError,
                    onFocused = { titleFocused = it },
                )
                AnimatedVisibility(
                    visible = showTitleError,
                    enter = fadeIn(SweetMotion.shortTween()) + expandVertically(SweetMotion.shortTween()),
                    exit = fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween()),
                ) {
                    Text(
                        "Please enter a title",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(12f),
                        ),
                        color = colors.pinkText,
                        modifier = Modifier.padding(top = mockupDp(4)),
                    )
                }

                HabitFieldLabel("CATEGORY")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                ) {
                    HabitCategory.entries.forEach { cat ->
                        HabitCategorySwatch(
                            category = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0)),
                        )
                    }
                }

                HabitFieldLabel("PRIORITY")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                ) {
                    TaskPriority.entries.forEach { p ->
                        TaskPriorityChip(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0)),
                        )
                    }
                }

                HabitFieldLabel("DAYS · EVERY WEEK")
                HabitWeekdayRow(
                    selected = dayReminders.keys,
                    weekStartsOn = weekStartsOn,
                    onToggle = { dayIndex ->
                        showDaysError = false
                        dayReminders = if (dayIndex in dayReminders) {
                            dayReminders - dayIndex
                        } else {
                            dayReminders + (dayIndex to "08:00")
                        }
                    },
                )
                AnimatedVisibility(
                    visible = showDaysError,
                    enter = fadeIn(SweetMotion.shortTween()) + expandVertically(SweetMotion.shortTween()),
                    exit = fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween()),
                ) {
                    Text(
                        "Pick at least one day",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(12f),
                        ),
                        color = colors.pinkText,
                        modifier = Modifier.padding(top = mockupDp(4)),
                    )
                }

                AnimatedVisibility(
                    visible = dayReminders.isNotEmpty(),
                    enter = if (reducedMotion) {
                        fadeIn(SweetMotion.shortTween())
                    } else {
                        fadeIn(SweetMotion.mediumTween()) + expandVertically(SweetMotion.mediumTween())
                    },
                    exit = if (reducedMotion) {
                        fadeOut(SweetMotion.shortTween())
                    } else {
                        fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween())
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = if (reducedMotion) {
                                    SweetMotion.shortTween()
                                } else {
                                    spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow,
                                    )
                                },
                            ),
                    ) {
                        HabitFieldLabel("REMINDER")
                        val ordered = (0..6).map { (weekStartsOn + it) % 7 }
                            .filter { it in dayReminders }
                        ordered.forEach { weekday ->
                            key(weekday) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = if (reducedMotion) {
                                        fadeIn(SweetMotion.shortTween())
                                    } else {
                                        fadeIn(SweetMotion.mediumTween()) +
                                            expandVertically(SweetMotion.mediumTween())
                                    },
                                    exit = fadeOut(SweetMotion.shortTween()) +
                                        shrinkVertically(SweetMotion.shortTween()),
                                ) {
                                    HabitDayReminderRow(
                                        weekdayIndex = weekday,
                                        reminderTime = dayReminders[weekday],
                                        onToggleReminder = { enabled ->
                                            dayReminders = dayReminders +
                                                (weekday to if (enabled) "08:00" else null)
                                        },
                                        onPickTime = { timePickerWeekday = weekday },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SheetFooterBar(
                onClose = onDismiss,
                closeLabel = "DISCARD",
            )
        }

        SweetTimedSnackbar(
            message = errorSnackbarMessage,
            onDismiss = { errorSnackbarMessage = null },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = mockupDp(MockupDimens.SNACKBAR_TOP)),
        )
    }

    val pickerWeekday = timePickerWeekday
    if (pickerWeekday != null) {
        SweetTimePickerDialog(
            reminderTime = dayReminders[pickerWeekday] ?: "08:00",
            onDismiss = { timePickerWeekday = null },
            onConfirm = { picked ->
                dayReminders = dayReminders + (pickerWeekday to picked)
                timePickerWeekday = null
            },
        )
    }
}

@Composable
private fun HabitFieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = BodyFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            letterSpacing = mockupSp(0.3f),
            lineHeight = mockupSp(14f),
        ),
        color = SweetTheme.colors.muted,
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6)),
    )
}

@Composable
private fun HabitPaperSurface(
    shadowDepth: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(cornerRadius)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowDepth)
                .formChipShadowLayer(colors.shadow, cornerRadius)
        )
        Box(
            modifier = Modifier
                .clip(shape)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .border(mockupDp(1), colors.fieldOutline, shape)
                .background(colors.paper)
        ) {
            content()
        }
    }
}

@Composable
private fun HabitSweetField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    isError: Boolean,
    onFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val fieldTextStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f),
    )
    HabitPaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = radius,
        modifier = modifier.then(
            if (isError) {
                Modifier.border(mockupDp(1), colors.pinkText, RoundedCornerShape(radius))
            } else {
                Modifier
            }
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (singleLine) Modifier.fillMaxHeight() else Modifier)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
        ) {
            if (leading != null) {
                Box(
                    modifier = Modifier.size(mockupDp(MockupDimens.MINI_FIELD_ICON)),
                    contentAlignment = Alignment.Center,
                ) { leading() }
                Spacer(Modifier.size(mockupDp(8)))
            }
            if (readOnly) {
                Text(
                    value.ifEmpty { placeholder },
                    style = fieldTextStyle,
                    color = if (value.isEmpty()) formPlaceholderColor() else colors.ink,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = fieldTextStyle.copy(color = colors.ink),
                    cursorBrush = SolidColor(colors.pinkText),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .onFocusEvent { onFocused(it.isFocused) }
                        .showKeyboardOnFocus()
                        .defaultMinSize(minHeight = mockupDp(20)),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxWidth()) {
                            if (value.isEmpty()) {
                                Text(
                                    placeholder,
                                    style = fieldTextStyle,
                                    color = formPlaceholderColor(),
                                )
                            }
                            inner()
                        }
                    },
                )
            }
            trailing?.invoke()
        }
    }
}

@Composable
private fun HabitWeekdayRow(
    selected: Set<Int>,
    weekStartsOn: Int,
    onToggle: (Int) -> Unit,
) {
    val ordered = (0..6).map { (weekStartsOn + it) % 7 }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(mockupDp(6)),
    ) {
        ordered.forEach { dayIndex ->
            HabitKindChip(
                label = JalaliDate.WEEKDAY_NAMES_EN_SHORT[dayIndex],
                selected = dayIndex in selected,
                onClick = { onToggle(dayIndex) },
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = mockupDp(0)),
            )
        }
    }
}

@Composable
private fun HabitDayReminderRow(
    weekdayIndex: Int,
    reminderTime: String?,
    onToggleReminder: (Boolean) -> Unit,
    onPickTime: () -> Unit,
) {
    val colors = SweetTheme.colors
    val enabled = reminderTime != null
    val reducedMotion = rememberReducedMotion()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = mockupDp(8))
            .animateContentSize(
                animationSpec = if (reducedMotion) {
                    SweetMotion.shortTween()
                } else {
                    spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    )
                },
            ),
    ) {
        Text(
            JalaliDate.WEEKDAY_NAMES_EN[weekdayIndex],
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(13f),
            ),
            color = colors.ink,
            modifier = Modifier.padding(bottom = mockupDp(6)),
        )
        HabitSweetField(
            value = if (enabled) formatTime12h(reminderTime!!) else "Add a reminder",
            onValueChange = {},
            placeholder = "Add a reminder",
            singleLine = true,
            isError = false,
            readOnly = true,
            onFocused = {},
            onClick = {
                if (enabled) onPickTime() else onToggleReminder(true)
            },
            leading = {
                ClockMiniIcon(
                    size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                    color = colors.purpleDeep,
                )
            },
            trailing = if (enabled) {
                {
                    ClosePixelIcon(
                        size = mockupDp(MockupDimens.SHEET_HEADER_ICON),
                        color = colors.muted,
                        modifier = Modifier.clickable { onToggleReminder(false) },
                    )
                }
            } else {
                null
            },
        )
    }
}

@Composable
private fun HabitKindChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    y = if (selected) mockupDp(3) else mockupDp(MockupDimens.FORM_FIELD_SHADOW)
                )
                .formChipShadowLayer(formChipShadowColor(selected), radius)
        )
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F),
            ),
            color = if (selected) formChipTextColor(selected) else SweetTheme.colors.muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .clickable(onClick = onClick)
                .background(formChipFillColor(selected))
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6)),
        )
    }
}
