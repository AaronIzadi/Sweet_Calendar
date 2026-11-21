package com.sweetcalendar.ui.addtask

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.sweetcalendar.data.TaskScheduleKind
import com.sweetcalendar.data.coercedFor
import com.sweetcalendar.data.taskCategoriesFor
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.data.scheduleKind
import com.sweetcalendar.recurrence.RecurrenceFreq
import com.sweetcalendar.recurrence.RecurrenceMonthDay
import com.sweetcalendar.recurrence.recurrenceIntervalInlineTextStyle
import com.sweetcalendar.recurrence.parsedDate
import com.sweetcalendar.recurrence.recurrenceFrequency
import com.sweetcalendar.recurrence.monthlyTaskAnchorDate
import com.sweetcalendar.recurrence.recurrenceMonthDayRule
import com.sweetcalendar.recurrence.recurrenceWeekdaySet
import com.sweetcalendar.recurrence.userLabel
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.repository.EventRepository
import com.sweetcalendar.ui.components.CalMiniIcon
import com.sweetcalendar.ui.components.NotesFormatToolbar
import com.sweetcalendar.ui.components.RichNotesText
import com.sweetcalendar.ui.components.applyNotesFormat
import com.sweetcalendar.ui.components.showKeyboardOnFocus
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.ThemeSparkleIcon
import com.sweetcalendar.ui.theme.themeAddTaskButtonText
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.components.ClosePixelIcon
import com.sweetcalendar.ui.components.ScrollColumnWithBottomAction
import com.sweetcalendar.ui.components.SheetFooterBar
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.components.SweetBigSaveButton
import com.sweetcalendar.ui.components.SweetTimedSnackbar
import com.sweetcalendar.data.local.TaskAlertEntity
import com.sweetcalendar.ui.components.TaskCategory
import com.sweetcalendar.ui.components.ThemeCategoryTaskIcon
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.formatDisplayShort
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.habit.HabitCategory
import com.sweetcalendar.ui.habit.HabitFormData
import com.sweetcalendar.ui.habit.HabitSheetFields
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.rememberReducedMotion
import com.sweetcalendar.ui.theme.formChipFillColor
import com.sweetcalendar.ui.theme.formPlaceholderColor
import com.sweetcalendar.ui.theme.formChipShadowColor
import com.sweetcalendar.ui.theme.formChipShadowLayer
import com.sweetcalendar.ui.theme.formChipTextColor
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import kotlinx.coroutines.launch

@Composable
private fun formFieldLabelColor(): Color = SweetTheme.colors.muted

@Composable
private fun formMetaMutedColor(): Color = SweetTheme.colors.muted

data class TaskFormData(
    val title: String,
    val notes: String,
    val jalaliDate: JalaliDate,
    val reminderTime: String?,
    val category: String,
    val priority: String,
    val scheduleKind: TaskScheduleKind = TaskScheduleKind.TASK,
    val subtasks: List<SubtaskFormItem> = emptyList(),
    val alerts: List<TaskAlertFormItem> = emptyList(),
    val recurrenceFreq: RecurrenceFreq,
    /** Total occurrences stored (first + N following); null = no limit. */
    val recurrenceCount: Int? = null,
    val recurrenceWeekdays: Set<Int> = emptySet(),
    val recurrenceMonthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
) {
    val repeatWeekly: Boolean get() = recurrenceFreq == RecurrenceFreq.WEEKLY
}

private const val RECURRENCE_COUNT_INFINITE_LABEL = "∞"

/** Empty / ∞ = no limit. Otherwise N = weeks/months after the first occurrence. */
internal fun parseRecurrenceFollowingInput(text: String): Int? {
    val trimmed = text.trim()
    if (trimmed.isEmpty() ||
        trimmed == RECURRENCE_COUNT_INFINITE_LABEL ||
        trimmed.equals("infinite", ignoreCase = true)
    ) {
        return null
    }
    return trimmed.toIntOrNull()?.coerceIn(0, 999)
}

/** Monthly: stored total = first month + N following months. */
internal fun parseMonthlyRecurrenceCountFromField(text: String): Int? {
    val following = parseRecurrenceFollowingInput(text) ?: return null
    return com.sweetcalendar.recurrence.recurrenceFollowingToTotalPeriods(following)
}

internal fun recurrenceCountFieldText(stored: String): String =
    if (stored.isEmpty()) RECURRENCE_COUNT_INFINITE_LABEL else stored

internal fun normalizeRecurrenceCountFieldInput(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty() ||
        trimmed == RECURRENCE_COUNT_INFINITE_LABEL ||
        trimmed.equals("infinite", ignoreCase = true)
    ) {
        return ""
    }
    return trimmed.filter { it.isDigit() }.take(3)
}

/** Stepper cycle: ∞ → 1 → 2 → … */
internal fun stepRecurrenceCountIncrement(stored: String): String {
    val following = parseRecurrenceFollowingInput(stored)
    return when (following) {
        null -> "1"
        else -> (following + 1).coerceAtMost(999).toString()
    }
}

internal fun stepRecurrenceCountDecrement(stored: String): String {
    val following = parseRecurrenceFollowingInput(stored)
    return when {
        following == null -> ""
        following <= 1 -> ""
        else -> (following - 1).toString()
    }
}

private sealed interface DatePickerTarget {
    data object Deadline : DatePickerTarget
    data class Alert(val stableKey: Long) : DatePickerTarget
    data class SubtaskDeadline(val index: Int) : DatePickerTarget
}

private sealed interface TimePickerTarget {
    data object TaskReminder : TimePickerTarget
    data class Alert(val stableKey: Long) : TimePickerTarget
    data class HabitDay(val weekdayIndex: Int) : TimePickerTarget
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskBottomSheet(
    date: JalaliDate,
    existingTask: TaskEntity?,
    existingAlerts: List<TaskAlertEntity> = emptyList(),
    existingSubtasks: List<SubtaskFormItem> = emptyList(),
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    eventRepository: EventRepository,
    weekStartsOn: Int = 0,
    initialActivityKind: ActivitySheetKind? = null,
    onDismiss: () -> Unit,
    onConfirm: (TaskFormData) -> Unit,
    onConfirmHabit: ((HabitFormData) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val isEdit = existingTask != null
    var title by remember(existingTask) { mutableStateOf(existingTask?.title.orEmpty()) }
    var notesField by remember(existingTask) {
        mutableStateOf(TextFieldValue(existingTask?.notes.orEmpty()))
    }
    var notesFormatSelection by remember(existingTask) {
        mutableStateOf<TextRange?>(null)
    }
    var activityKind by remember(existingTask, initialActivityKind) {
        mutableStateOf(
            when {
                existingTask != null ->
                    ActivitySheetKind.fromScheduleKind(existingTask.scheduleKind())
                initialActivityKind != null -> initialActivityKind
                else -> ActivitySheetKind.TASK
            }
        )
    }
    val scheduleKind = activityKind.toScheduleKind()
    val allowHabit = !isEdit && onConfirmHabit != null
    var category by remember(existingTask) {
        val kind = existingTask?.scheduleKind() ?: TaskScheduleKind.TASK
        mutableStateOf(TaskCategory.fromString(existingTask?.category.orEmpty()).coercedFor(kind))
    }
    var priority by remember(existingTask) {
        mutableStateOf(TaskPriority.fromString(existingTask?.priority.orEmpty()))
    }
    var recurrenceFreq by remember(existingTask) {
        mutableStateOf(existingTask?.recurrenceFrequency() ?: RecurrenceFreq.NONE)
    }
    var recurrenceCountText by remember(existingTask) {
        mutableStateOf(
            when {
                existingTask == null -> ""
                existingTask.recurrenceCount == null -> ""
                else -> com.sweetcalendar.recurrence.recurrenceFollowingFromStored(
                    existingTask.recurrenceFrequency(),
                    existingTask.recurrenceCount,
                    existingTask.recurrenceWeekdaySet(),
                ).toString()
            }
        )
    }
    var recurrenceWeekdays by remember(existingTask, date) {
        mutableStateOf(
            existingTask?.recurrenceWeekdaySet()
                ?: setOf(date.weekdayIndex())
        )
    }
    var recurrenceMonthDay by remember(existingTask, date) {
        mutableStateOf(
            existingTask?.recurrenceMonthDayRule() ?: RecurrenceMonthDay.OnThisDay
        )
    }
    var reminderEnabled by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime != null)
    }
    var reminderTime by remember(existingTask) {
        mutableStateOf(existingTask?.reminderTime ?: "14:30")
    }
    val initialSettingDate = remember(existingTask, date) {
        existingTask?.parsedDate() ?: date
    }
    val initialMonthDayRule = remember(existingTask) {
        existingTask?.recurrenceMonthDayRule() ?: RecurrenceMonthDay.OnThisDay
    }
    var selectedDate by remember(existingTask, date) { mutableStateOf(initialSettingDate) }
    var todoAlerts by remember(existingTask, existingAlerts) {
        mutableStateOf(existingAlerts.map { TaskAlertFormItem.fromEntity(it) })
    }
    var subtasks by remember(existingTask, existingSubtasks) {
        mutableStateOf(existingSubtasks)
    }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerTarget by remember { mutableStateOf<DatePickerTarget?>(null) }
    var timePickerTarget by remember { mutableStateOf<TimePickerTarget?>(null) }
    var timePickerSeed by remember { mutableStateOf("14:30") }
    var showTitleError by remember { mutableStateOf(false) }
    var showHabitDaysError by remember { mutableStateOf(false) }
    var errorSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var habitCategory by remember { mutableStateOf(HabitCategory.Personal) }
    var habitPriority by remember { mutableStateOf(TaskPriority.Medium) }
    var habitDayReminders by remember(date) {
        mutableStateOf<Map<Int, String?>>(mapOf(date.weekdayIndex() to "08:00"))
    }

    fun showSaveBlockedError(message: String) {
        errorSnackbarMessage = message
    }

    fun submitHabit() {
        val confirm = onConfirmHabit ?: return
        if (title.isBlank()) {
            showTitleError = true
            showSaveBlockedError("Please enter a title")
            return
        }
        if (habitDayReminders.isEmpty()) {
            showHabitDaysError = true
            showSaveBlockedError("Pick at least one day")
            return
        }
        confirm(
            HabitFormData(
                name = title.trim(),
                dayReminders = habitDayReminders,
                category = habitCategory,
                priority = habitPriority,
            )
        )
    }

    fun submit() {
        if (activityKind == ActivitySheetKind.HABIT) {
            submitHabit()
            return
        }
        if (title.isBlank()) {
            showTitleError = true
            showSaveBlockedError("Please enter a title")
            return
        }
        val effectiveRecurrence = if (scheduleKind == TaskScheduleKind.TODO) {
            RecurrenceFreq.NONE
        } else {
            recurrenceFreq
        }
        val taskDate = when {
            scheduleKind == TaskScheduleKind.TODO -> selectedDate
            effectiveRecurrence != RecurrenceFreq.MONTHLY -> selectedDate
            existingTask == null -> monthlyTaskAnchorDate(selectedDate, recurrenceMonthDay)
            existingTask.recurrenceFrequency() != RecurrenceFreq.MONTHLY ||
                selectedDate != initialSettingDate ||
                recurrenceMonthDay != initialMonthDayRule -> {
                monthlyTaskAnchorDate(selectedDate, recurrenceMonthDay)
            }
            else -> existingTask.parsedDate()
        }
        onConfirm(
            TaskFormData(
                title = title.trim(),
                notes = notesField.text.trim(),
                jalaliDate = taskDate,
                reminderTime = if (scheduleKind == TaskScheduleKind.TODO) {
                    null
                } else {
                    reminderTime.takeIf { reminderEnabled }
                },
                category = category.label,
                priority = priority.label,
                scheduleKind = scheduleKind,
                subtasks = subtasks.filter { it.title.isNotBlank() },
                alerts = if (scheduleKind == TaskScheduleKind.TODO) todoAlerts else emptyList(),
                recurrenceFreq = effectiveRecurrence,
                recurrenceCount = if (effectiveRecurrence == RecurrenceFreq.NONE) {
                    null
                } else {
                    com.sweetcalendar.recurrence.recurrenceStoredCountFromFollowing(
                        effectiveRecurrence,
                        parseRecurrenceFollowingInput(recurrenceCountText),
                        recurrenceWeekdays,
                    )
                },
                recurrenceWeekdays = if (effectiveRecurrence == RecurrenceFreq.WEEKLY) {
                    recurrenceWeekdays
                } else {
                    emptySet()
                },
                recurrenceMonthDay = if (effectiveRecurrence == RecurrenceFreq.MONTHLY) {
                    recurrenceMonthDay
                } else {
                    RecurrenceMonthDay.OnThisDay
                },
            )
        )
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var titleFieldFocused by remember { mutableStateOf(false) }
    var notesFieldFocused by remember { mutableStateOf(false) }
    val titleBringIntoView = remember { BringIntoViewRequester() }
    val notesBringIntoView = remember { BringIntoViewRequester() }
    val notesFocusRequester = remember { FocusRequester() }

    fun scrollNotesIntoView() {
        scope.launch {
            notesBringIntoView.bringIntoView()
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    fun formTextFieldModifier(
        bringIntoView: BringIntoViewRequester,
        onFocused: (Boolean) -> Unit,
        scrollToEndOnFocus: Boolean = false,
    ): Modifier = Modifier
        .bringIntoViewRequester(bringIntoView)
        .onFocusEvent { state ->
            onFocused(state.isFocused)
            if (state.isFocused) {
                scope.launch {
                    bringIntoView.bringIntoView()
                    if (scrollToEndOnFocus) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            }
        }

    LaunchedEffect(notesField.text, notesFieldFocused) {
        if (notesFieldFocused) {
            notesBringIntoView.bringIntoView()
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    BackHandler {
        if (titleFieldFocused || notesFieldFocused) {
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
            val reducedMotion = rememberReducedMotion()
            AnimatedContent(
                targetState = activityKind,
                transitionSpec = {
                    if (reducedMotion) {
                        fadeIn(SweetMotion.shortTween()) togetherWith fadeOut(SweetMotion.shortTween())
                    } else {
                        fadeIn(SweetMotion.shortTween()) togetherWith fadeOut(SweetMotion.shortTween())
                    }
                },
                label = "sheetTitleKind",
                modifier = Modifier
                    .fillMaxWidth()
                    .then(sheetDialogTitlePaddingModifier()),
            ) { kind ->
                Text(
                    when {
                        kind == ActivitySheetKind.HABIT && isEdit -> "Edit habit"
                        kind == ActivitySheetKind.HABIT -> "New habit"
                        kind == ActivitySheetKind.TODO && isEdit -> "Edit to-do"
                        kind == ActivitySheetKind.TODO -> "New to-do"
                        isEdit -> "Edit task"
                        else -> "New task"
                    },
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

            if (!isEdit) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mockupDp(18), vertical = mockupDp(8)),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                ) {
                    RepeatFreqChip(
                        label = "Task",
                        selected = activityKind == ActivitySheetKind.TASK,
                        onClick = {
                            if (activityKind != ActivitySheetKind.TASK) {
                                subtasks = subtasks.filter { it.title.isNotBlank() }
                                category = category.coercedFor(TaskScheduleKind.TASK)
                                showHabitDaysError = false
                                activityKind = ActivitySheetKind.TASK
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = mockupDp(0)),
                    )
                    RepeatFreqChip(
                        label = "To Do",
                        selected = activityKind == ActivitySheetKind.TODO,
                        onClick = {
                            if (activityKind != ActivitySheetKind.TODO) {
                                subtasks = subtasks.filter { it.title.isNotBlank() }
                                category = category.coercedFor(TaskScheduleKind.TODO)
                                recurrenceFreq = RecurrenceFreq.NONE
                                showHabitDaysError = false
                                activityKind = ActivitySheetKind.TODO
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = mockupDp(0)),
                    )
                    if (allowHabit) {
                        RepeatFreqChip(
                            label = "Habit",
                            selected = activityKind == ActivitySheetKind.HABIT,
                            onClick = {
                                if (activityKind != ActivitySheetKind.HABIT) {
                                    showTitleError = false
                                    showHabitDaysError = false
                                    activityKind = ActivitySheetKind.HABIT
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0)),
                        )
                    }
                }
            }

            ScrollColumnWithBottomAction(
                scrollState = scrollState,
                bottomAction = {
                    SweetBigSaveButton(
                        text = if (activityKind == ActivitySheetKind.HABIT) {
                            themeAddTaskButtonText(isEdit = false)
                        } else {
                            themeAddTaskButtonText(isEdit)
                        },
                        onClick = {
                            if (activityKind == ActivitySheetKind.HABIT) submitHabit()
                            else submit()
                        },
                    )
                },
            ) {
                AnimatedContent(
                    targetState = activityKind,
                    transitionSpec = {
                        if (reducedMotion) {
                            fadeIn(SweetMotion.shortTween()) togetherWith fadeOut(SweetMotion.shortTween())
                        } else if (targetState.ordinal > initialState.ordinal) {
                            fadeIn(SweetMotion.mediumTween()) +
                                slideInVertically(SweetMotion.mediumTween()) { h -> h / 10 } togetherWith
                                fadeOut(SweetMotion.shortTween()) +
                                slideOutVertically(SweetMotion.shortTween()) { h -> -h / 10 }
                        } else {
                            fadeIn(SweetMotion.mediumTween()) +
                                slideInVertically(SweetMotion.mediumTween()) { h -> -h / 10 } togetherWith
                                fadeOut(SweetMotion.shortTween()) +
                                slideOutVertically(SweetMotion.shortTween()) { h -> h / 10 }
                        }
                    },
                    label = "activityKindFields",
                    modifier = Modifier.fillMaxWidth(),
                ) { kind ->
                if (kind == ActivitySheetKind.HABIT) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                    HabitSheetFields(
                        category = habitCategory,
                        onCategoryChange = { habitCategory = it },
                        priority = habitPriority,
                        onPriorityChange = { habitPriority = it },
                        dayReminders = habitDayReminders,
                        onDayRemindersChange = {
                            habitDayReminders = it
                            if (it.isNotEmpty()) showHabitDaysError = false
                        },
                        weekStartsOn = weekStartsOn,
                        showDaysError = showHabitDaysError,
                        fieldLabel = { FieldLabel(it) },
                        kindChip = { label, selected, onClick, mod ->
                            RepeatFreqChip(
                                label = label,
                                selected = selected,
                                onClick = onClick,
                                modifier = mod,
                            )
                        },
                        titleField = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                FieldLabel("TITLE")
                                SweetField(
                                    value = title,
                                    onValueChange = {
                                        title = it
                                        if (it.isNotBlank()) showTitleError = false
                                    },
                                    placeholder = "e.g. Morning stretch",
                                    singleLine = true,
                                    isError = showTitleError,
                                    textFieldModifier = formTextFieldModifier(
                                        bringIntoView = titleBringIntoView,
                                        onFocused = { titleFieldFocused = it },
                                    ),
                                )
                                AnimatedVisibility(
                                    visible = showTitleError,
                                    enter = fadeIn(SweetMotion.shortTween()) +
                                        expandVertically(SweetMotion.shortTween()),
                                    exit = fadeOut(SweetMotion.shortTween()) +
                                        shrinkVertically(SweetMotion.shortTween()),
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
                            }
                        },
                        reminderField = { value, onClick, trailing ->
                            SweetField(
                                value = value,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = onClick),
                                leading = {
                                    ClockMiniIcon(
                                        size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                        color = colors.purpleDeep,
                                    )
                                },
                                trailing = trailing,
                            )
                        },
                        onPickReminderTime = { weekday ->
                            timePickerSeed = habitDayReminders[weekday] ?: "08:00"
                            timePickerTarget = TimePickerTarget.HabitDay(weekday)
                            showTimePicker = true
                        },
                    )
                    }
                } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                FieldLabel("TITLE")
                SweetField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showTitleError = false
                    },
                    placeholder = if (scheduleKind == TaskScheduleKind.TODO) {
                        "e.g. Biology homework"
                    } else {
                        "e.g. Do laundry"
                    },
                    singleLine = true,
                    isError = showTitleError,
                    textFieldModifier = formTextFieldModifier(
                        bringIntoView = titleBringIntoView,
                        onFocused = { titleFieldFocused = it },
                    ),
                )
                if (showTitleError) {
                    Text(
                        "Please enter a title",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(12f)
                        ),
                        color = colors.pinkText,
                        modifier = Modifier.padding(top = mockupDp(4))
                    )
                }

                if (kind == ActivitySheetKind.TODO) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                    FieldLabel("DEADLINE")
                    SweetField(
                        value = selectedDate.formatDisplayShort(calendarSystem),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                datePickerTarget = DatePickerTarget.Deadline
                                showDatePicker = true
                            },
                        leading = {
                            CalMiniIcon(
                                size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                color = colors.purpleDeep
                            )
                        }
                    )

                    FieldLabel("ALERTS")
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
                        AnimatedVisibility(
                            visible = todoAlerts.isEmpty(),
                            enter = fadeIn(SweetMotion.shortTween()) + expandVertically(SweetMotion.shortTween()),
                            exit = fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween()),
                        ) {
                            Text(
                                "No alerts yet — add one below",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = SecondaryTextWeight,
                                    fontSize = mockupSp(12f),
                                ),
                                color = formMetaMutedColor(),
                                modifier = Modifier.padding(bottom = mockupDp(8)),
                            )
                        }
                        todoAlerts.forEach { alert ->
                            key(alert.stableKey) {
                                AnimatedFormListItem(
                                    reducedMotion = reducedMotion,
                                    onRemoved = {
                                        todoAlerts = todoAlerts.filter { it.stableKey != alert.stableKey }
                                    },
                                ) { requestRemove ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = mockupDp(8)),
                                        horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        SweetField(
                                            value = alert.date.formatDisplayShort(calendarSystem),
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    datePickerTarget = DatePickerTarget.Alert(alert.stableKey)
                                                    showDatePicker = true
                                                },
                                            leading = {
                                                CalMiniIcon(
                                                    size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                                    color = colors.purpleDeep
                                                )
                                            }
                                        )
                                        SweetField(
                                            value = formatTime12h(alert.time),
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    timePickerSeed = alert.time
                                                    timePickerTarget = TimePickerTarget.Alert(alert.stableKey)
                                                    showTimePicker = true
                                                },
                                            leading = {
                                                ClockMiniIcon(
                                                    size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                                    color = colors.purpleDeep
                                                )
                                            }
                                        )
                                        ClosePixelIcon(
                                            size = mockupDp(MockupDimens.SHEET_HEADER_ICON),
                                            color = colors.muted,
                                            modifier = Modifier.clickable(onClick = requestRemove),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    RepeatFreqChip(
                        label = "+ Add alert",
                        selected = false,
                        onClick = {
                            todoAlerts = todoAlerts + TaskAlertFormItem(
                                date = JalaliDate.today(),
                                time = "09:00",
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                    FieldLabel("DATE & TIME")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                    ) {
                        SweetField(
                            value = selectedDate.formatDisplayShort(calendarSystem),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    datePickerTarget = DatePickerTarget.Deadline
                                    showDatePicker = true
                                },
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
                                    timePickerSeed = reminderTime
                                    timePickerTarget = TimePickerTarget.TaskReminder
                                    showTimePicker = true
                                },
                            leading = {
                                ClockMiniIcon(
                                    size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                                    color = colors.purpleDeep
                                )
                            },
                            trailing = if (reminderEnabled) {
                                {
                                    ClosePixelIcon(
                                        size = mockupDp(MockupDimens.SHEET_HEADER_ICON),
                                        color = colors.muted,
                                        modifier = Modifier.clickable { reminderEnabled = false }
                                    )
                                }
                            } else null
                        )
                    }
                    }
                }

                FieldLabel("CATEGORY")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                ) {
                    taskCategoriesFor(scheduleKind).forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0))
                        ) {
                            CategorySwatch(
                                category = cat,
                                selected = category == cat,
                                forTodo = scheduleKind == TaskScheduleKind.TODO,
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
                        TaskPriorityChip(
                            priority = p,
                            selected = priority == p,
                            onClick = { priority = p },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0))
                        )
                    }
                }

                AnimatedVisibility(
                    visible = scheduleKind == TaskScheduleKind.TASK,
                    enter = fadeIn(SweetMotion.mediumTween()) + expandVertically(SweetMotion.mediumTween()),
                    exit = fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween()),
                ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                FieldLabel("REPEAT")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                ) {
                    RecurrenceFreq.entries.forEach { freq ->
                        RepeatFreqChip(
                            label = freq.userLabel(),
                            selected = recurrenceFreq == freq,
                            onClick = {
                                recurrenceFreq = freq
                                if (freq == RecurrenceFreq.NONE) {
                                    recurrenceCountText = ""
                                    recurrenceWeekdays = emptySet()
                                    recurrenceMonthDay = RecurrenceMonthDay.OnThisDay
                                } else if (freq == RecurrenceFreq.WEEKLY) {
                                    if (recurrenceWeekdays.isEmpty()) {
                                        recurrenceWeekdays = setOf(selectedDate.weekdayIndex())
                                    }
                                } else if (freq == RecurrenceFreq.MONTHLY) {
                                    recurrenceMonthDay = RecurrenceMonthDay.OnThisDay
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(min = mockupDp(0))
                        )
                    }
                }

                AnimatedVisibility(
                    visible = recurrenceFreq != RecurrenceFreq.NONE,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column(modifier = Modifier.padding(top = mockupDp(10))) {
                        if (recurrenceFreq == RecurrenceFreq.WEEKLY) {
                            FieldLabel("ON THESE DAYS")
                            RecurrenceWeekdayRow(
                                selected = recurrenceWeekdays,
                                weekStartsOn = weekStartsOn,
                                onToggle = { day ->
                                    recurrenceWeekdays = if (day in recurrenceWeekdays) {
                                        val next = recurrenceWeekdays - day
                                        if (next.isEmpty()) recurrenceWeekdays else next
                                    } else {
                                        recurrenceWeekdays + day
                                    }
                                },
                                modifier = Modifier.padding(bottom = mockupDp(12)),
                            )
                        } else {
                            FieldLabel("ON DAY")
                            RecurrenceMonthDaySelector(
                                selectedDate = selectedDate,
                                value = recurrenceMonthDay,
                                onChange = { recurrenceMonthDay = it },
                                modifier = Modifier.padding(bottom = mockupDp(12)),
                            )
                        }
                        val intervalUnit = if (recurrenceFreq == RecurrenceFreq.WEEKLY) {
                            "weeks."
                        } else {
                            "months."
                        }
                        val intervalInlineStyle = recurrenceIntervalInlineTextStyle()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    "Repeat for",
                                    style = intervalInlineStyle,
                                    color = colors.ink,
                                    maxLines = 1,
                                )
                            }
                            RecurrenceCountStepper(
                                value = recurrenceCountFieldText(recurrenceCountText),
                                onValueChange = {
                                    recurrenceCountText = normalizeRecurrenceCountFieldInput(it)
                                },
                                onDecrement = {
                                    recurrenceCountText = stepRecurrenceCountDecrement(recurrenceCountText)
                                },
                                onIncrement = {
                                    recurrenceCountText = stepRecurrenceCountIncrement(recurrenceCountText)
                                },
                                placeholder = RECURRENCE_COUNT_INFINITE_LABEL,
                                textStyle = intervalInlineStyle,
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(min = mockupDp(0f), max = mockupDp(176f)),
                            )
                            Box(
                                modifier = Modifier
                                    .width(mockupDp(58f))
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    intervalUnit,
                                    style = intervalInlineStyle,
                                    color = colors.ink,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }
                }
                }

                FieldLabel("NOTES")
                NotesFormatToolbar(
                    onFormat = { format ->
                        val selection = notesFormatSelection ?: notesField.selection
                        notesField = applyNotesFormat(
                            notesField.copy(selection = selection),
                            format,
                        )
                        notesFormatSelection = notesField.selection
                        notesFieldFocused = true
                        notesFocusRequester.requestFocus()
                        scrollNotesIntoView()
                    },
                    modifier = Modifier.padding(bottom = mockupDp(6)),
                )
                SweetNotesField(
                    value = notesField,
                    onValueChange = { new ->
                        notesField = new
                        if (notesFieldFocused) {
                            notesFormatSelection = new.selection
                        }
                        if (notesFieldFocused) scrollNotesIntoView()
                    },
                    showRichPreview = !notesFieldFocused,
                    onRequestEdit = { notesFocusRequester.requestFocus() },
                    placeholder = "Add a note (optional)",
                    minLines = 3,
                    maxLines = 8,
                    textFieldModifier = formTextFieldModifier(
                        bringIntoView = notesBringIntoView,
                        onFocused = { focused ->
                            notesFieldFocused = focused
                            if (focused) {
                                notesFormatSelection = notesField.selection
                            }
                        },
                        scrollToEndOnFocus = true,
                    ).focusRequester(notesFocusRequester),
                )

                SubtaskEditorSection(
                    subtasks = subtasks,
                    onChange = { subtasks = it },
                    todoSubtaskDeadlines = scheduleKind == TaskScheduleKind.TODO,
                    parentDeadline = selectedDate,
                    calendarSystem = calendarSystem,
                    onPickSubtaskDeadline = { index ->
                        datePickerTarget = DatePickerTarget.SubtaskDeadline(index)
                        showDatePicker = true
                    },
                )
                } // end task/todo Column
                } // end task/todo branch
                } // end AnimatedContent activityKind
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

    val datePickerInitial = when (val target = datePickerTarget) {
        is DatePickerTarget.Alert -> {
            todoAlerts.firstOrNull { it.stableKey == target.stableKey }?.date ?: selectedDate
        }
        is DatePickerTarget.SubtaskDeadline -> {
            subtasks.getOrNull(target.index)?.displayDeadline(selectedDate) ?: selectedDate
        }
        DatePickerTarget.Deadline, null -> selectedDate
    }

    if (showDatePicker) {
        when (calendarSystem) {
            CalendarSystem.PERSIAN -> JalaliDatePickerDialog(
                initialDate = datePickerInitial,
                eventRepository = eventRepository,
                onDismiss = {
                    showDatePicker = false
                    datePickerTarget = null
                },
                onConfirm = { picked ->
                    when (val target = datePickerTarget) {
                        is DatePickerTarget.Alert -> {
                            todoAlerts = todoAlerts.map {
                                if (it.stableKey == target.stableKey) it.copy(date = picked) else it
                            }
                        }
                        is DatePickerTarget.SubtaskDeadline -> {
                            val next = subtasks.toMutableList()
                            val item = next.getOrNull(target.index) ?: return@JalaliDatePickerDialog
                            next[target.index] = item.copy(jalaliDate = picked.formatIso())
                            subtasks = next
                        }
                        DatePickerTarget.Deadline, null -> selectedDate = picked
                    }
                    showDatePicker = false
                    datePickerTarget = null
                }
            )
            CalendarSystem.GREGORIAN -> GregorianDatePickerDialog(
                initialDate = datePickerInitial,
                eventRepository = eventRepository,
                onDismiss = {
                    showDatePicker = false
                    datePickerTarget = null
                },
                onConfirm = { picked ->
                    when (val target = datePickerTarget) {
                        is DatePickerTarget.Alert -> {
                            todoAlerts = todoAlerts.map {
                                if (it.stableKey == target.stableKey) it.copy(date = picked) else it
                            }
                        }
                        is DatePickerTarget.SubtaskDeadline -> {
                            val next = subtasks.toMutableList()
                            val item = next.getOrNull(target.index) ?: return@GregorianDatePickerDialog
                            next[target.index] = item.copy(jalaliDate = picked.formatIso())
                            subtasks = next
                        }
                        DatePickerTarget.Deadline, null -> selectedDate = picked
                    }
                    showDatePicker = false
                    datePickerTarget = null
                }
            )
        }
    }

    if (showTimePicker) {
        SweetTimePickerDialog(
            reminderTime = timePickerSeed,
            onDismiss = {
                showTimePicker = false
                timePickerTarget = null
            },
            onConfirm = { time ->
                when (val target = timePickerTarget) {
                    is TimePickerTarget.Alert -> {
                        todoAlerts = todoAlerts.map {
                            if (it.stableKey == target.stableKey) it.copy(time = time) else it
                        }
                    }
                    is TimePickerTarget.HabitDay -> {
                        habitDayReminders = habitDayReminders + (target.weekdayIndex to time)
                    }
                    TimePickerTarget.TaskReminder, null -> {
                        reminderTime = time
                        reminderEnabled = true
                    }
                }
                showTimePicker = false
                timePickerTarget = null
            }
        )
    }
}

@Composable
private fun PaperSurface(
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
        val faceModifier = if (onClick != null) {
            Modifier.matchParentSize()
        } else {
            Modifier
        }
        Box(
            modifier = faceModifier
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
        color = formFieldLabelColor(),
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
private fun SweetNotesField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    showRichPreview: Boolean,
    onRequestEdit: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 3,
    maxLines: Int = 8,
    textFieldModifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val fieldTextStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f)
    )
    val multilineMinHeight = mockupDp(MockupDimens.FIELD_TEXT_F * minLines + 6f)
    val fieldPadding = Modifier
        .fillMaxWidth()
        .padding(horizontal = mockupDp(14), vertical = mockupDp(12))
        .defaultMinSize(minHeight = multilineMinHeight)
        .heightIn(min = multilineMinHeight)
    PaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = radius,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = multilineMinHeight),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = false,
                minLines = minLines,
                maxLines = maxLines,
                textStyle = fieldTextStyle.copy(color = colors.ink),
                cursorBrush = SolidColor(colors.pinkText),
                modifier = fieldPadding
                    .then(textFieldModifier)
                    .showKeyboardOnFocus(),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = multilineMinHeight),
                        contentAlignment = Alignment.TopStart,
                    ) {
                        if (value.text.isEmpty() && placeholder.isNotEmpty() && !showRichPreview) {
                            Text(
                                placeholder,
                                style = fieldTextStyle,
                                color = formPlaceholderColor(),
                            )
                        }
                        inner()
                    }
                }
            )
            if (showRichPreview) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(radius))
                        .background(colors.paper)
                        .clickable(onClick = onRequestEdit)
                        .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
                    contentAlignment = Alignment.TopStart,
                ) {
                    if (value.text.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            placeholder,
                            style = fieldTextStyle,
                            color = formPlaceholderColor(),
                        )
                    } else if (value.text.isNotEmpty()) {
                        RichNotesText(
                            text = value.text,
                            style = fieldTextStyle,
                            color = colors.ink,
                        )
                    }
                }
            }
        }
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
    maxLines: Int = Int.MAX_VALUE,
    isError: Boolean = false,
    readOnly: Boolean = false,
    textFieldModifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textAlign: TextAlign? = null,
    textStyle: TextStyle? = null,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val resolvedAlign = textAlign ?: TextAlign.Start
    val baseFieldTextStyle = textStyle ?: TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f),
    )
    val fieldTextStyle = baseFieldTextStyle.merge(
        TextStyle(textAlign = resolvedAlign),
    )
    val fieldContentAlignment = when (resolvedAlign) {
        TextAlign.Center -> Alignment.Center
        TextAlign.End, TextAlign.Right -> Alignment.CenterEnd
        else -> if (singleLine) Alignment.CenterStart else Alignment.TopStart
    }
    PaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = radius,
        modifier = modifier.then(
            if (isError) Modifier.border(mockupDp(1), colors.pinkText, RoundedCornerShape(radius)) else Modifier
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (singleLine) Modifier.fillMaxHeight() else Modifier)
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
                    textAlign = resolvedAlign,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                val multilineMinHeight = mockupDp(MockupDimens.FIELD_TEXT_F * minLines + 6f)
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    minLines = minLines,
                    maxLines = if (singleLine) 1 else maxLines,
                    keyboardOptions = keyboardOptions,
                    textStyle = fieldTextStyle.copy(color = colors.ink),
                    cursorBrush = SolidColor(colors.pinkText),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                        .then(
                            if (!singleLine) {
                                Modifier
                                    .defaultMinSize(minHeight = multilineMinHeight)
                                    .heightIn(min = multilineMinHeight)
                            } else {
                                Modifier
                            }
                        )
                        .then(textFieldModifier)
                        .showKeyboardOnFocus(),
                    decorationBox = { inner ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!singleLine) Modifier.heightIn(min = multilineMinHeight) else Modifier
                                ),
                            contentAlignment = fieldContentAlignment,
                        ) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    placeholder,
                                    style = fieldTextStyle,
                                    color = formPlaceholderColor(),
                                    textAlign = resolvedAlign,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = fieldContentAlignment,
                            ) {
                                inner()
                            }
                        }
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
    forTodo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSize = mockupDp(MockupDimens.SWATCH_ICON)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .clickable(onClick = onClick)
                .background(formChipFillColor(selected))
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mockupDp(6))
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                ThemeCategoryTaskIcon(
                    category = category,
                    size = iconSize,
                    forTodo = forTodo,
                )
            }
            Text(
                category.label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F)
                ),
                color = if (selected) formChipTextColor(selected) else formMetaMutedColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RecurrenceCountStepper(
    value: String,
    onValueChange: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "∞",
    textStyle: TextStyle? = null,
) {
    val controlHeight = mockupDp(44)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecurrenceCountStepButton(
            label = "−",
            onClick = onDecrement,
            modifier = Modifier.size(controlHeight),
        )
        SweetField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier
                .weight(1f)
                .height(controlHeight),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textAlign = TextAlign.Center,
            textStyle = textStyle,
        )
        RecurrenceCountStepButton(
            label = "+",
            onClick = onIncrement,
            modifier = Modifier.size(controlHeight),
        )
    }
}

@Composable
private fun RecurrenceCountStepButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    PaperSurface(
        shadowDepth = mockupDp(MockupDimens.FORM_FIELD_SHADOW),
        cornerRadius = radius,
        modifier = modifier,
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(20f),
                    lineHeight = mockupSp(22f),
                ),
                color = colors.purpleDeep,
            )
        }
    }
}

@Composable
private fun RecurrenceMonthDaySelector(
    selectedDate: JalaliDate,
    value: RecurrenceMonthDay,
    onChange: (RecurrenceMonthDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fixedDay = (value as? RecurrenceMonthDay.Fixed)?.day ?: selectedDate.day
    var dayText by remember(value, selectedDate) {
        mutableStateOf(fixedDay.coerceIn(1, 31).toString())
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(6)),
        ) {
            RepeatFreqChip(
                label = "on this day",
                selected = value is RecurrenceMonthDay.OnThisDay,
                onClick = { onChange(RecurrenceMonthDay.OnThisDay) },
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = mockupDp(0)),
            )
            RepeatFreqChip(
                label = "Last",
                selected = value is RecurrenceMonthDay.Last,
                onClick = { onChange(RecurrenceMonthDay.Last) },
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = mockupDp(0)),
            )
            RepeatFreqChip(
                label = "Select a day",
                selected = value is RecurrenceMonthDay.Fixed,
                onClick = {
                    val day = dayText.toIntOrNull()?.coerceIn(1, 31) ?: selectedDate.day
                    dayText = day.toString()
                    onChange(RecurrenceMonthDay.Fixed(day))
                },
                modifier = Modifier
                    .weight(1.15f)
                    .widthIn(min = mockupDp(0)),
            )
        }
        AnimatedVisibility(
            visible = value is RecurrenceMonthDay.Fixed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = mockupDp(10)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RecurrenceCountStepper(
                    value = dayText,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }.take(2)
                        dayText = digits
                        val parsed = digits.toIntOrNull()?.coerceIn(1, 31)
                        if (parsed != null) {
                            onChange(RecurrenceMonthDay.Fixed(parsed))
                        }
                    },
                    onDecrement = {
                        val current = dayText.toIntOrNull()?.coerceIn(1, 31) ?: selectedDate.day
                        val next = (current - 1).coerceAtLeast(1)
                        dayText = next.toString()
                        onChange(RecurrenceMonthDay.Fixed(next))
                    },
                    onIncrement = {
                        val current = dayText.toIntOrNull()?.coerceIn(1, 31) ?: selectedDate.day
                        val next = (current + 1).coerceAtMost(31)
                        dayText = next.toString()
                        onChange(RecurrenceMonthDay.Fixed(next))
                    },
                    placeholder = selectedDate.day.toString(),
                    textStyle = recurrenceIntervalInlineTextStyle(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RecurrenceWeekdayRow(
    selected: Set<Int>,
    weekStartsOn: Int,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ordered = (0..6).map { (weekStartsOn + it) % 7 }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(mockupDp(6)),
    ) {
        ordered.forEach { dayIndex ->
            val label = JalaliDate.WEEKDAY_NAMES_EN_SHORT[dayIndex]
            RepeatFreqChip(
                label = label,
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
private fun RepeatFreqChip(
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
                fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F)
            ),
            color = if (selected) formChipTextColor(selected) else formMetaMutedColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .clickable(onClick = onClick)
                .background(formChipFillColor(selected))
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6))
        )
    }
}

