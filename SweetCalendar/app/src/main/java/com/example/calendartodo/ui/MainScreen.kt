package com.example.calendartodo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.data.prefs.AppPreferences
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.addtask.TaskBottomSheet
import com.example.calendartodo.ui.addtask.TaskFormData
import com.example.calendartodo.ui.archive.ArchiveScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel
import com.example.calendartodo.ui.calendar.DayEvent
import com.example.calendartodo.ui.calendar.MonthScreen
import com.example.calendartodo.ui.components.CompleteCelebrationDialog
import com.example.calendartodo.ui.components.PixelConfirmDialog
import com.example.calendartodo.ui.components.SweetBottomNav
import com.example.calendartodo.ui.components.SweetFab
import com.example.calendartodo.ui.components.SweetUndoSnackbar
import com.example.calendartodo.ui.daydetail.DayDetailScreen
import com.example.calendartodo.ui.holiday.HolidayDetailScreen
import com.example.calendartodo.ui.navigation.AppDestination
import com.example.calendartodo.ui.offline.OfflineScreen
import com.example.calendartodo.ui.permission.NotificationPermissionScreen
import com.example.calendartodo.ui.search.SearchScreen
import com.example.calendartodo.ui.settings.SettingsScreen
import com.example.calendartodo.ui.stats.StatsScreen
import com.example.calendartodo.ui.taskdetail.TaskDetailScreen
import com.example.calendartodo.ui.theme.CalendarTodoTheme
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.today.TodayScreen
import com.example.calendartodo.ui.week.WeekScreen
import com.example.calendartodo.ui.welcome.WelcomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed class SheetState {
    data object Hidden : SheetState()
    data class Add(val date: JalaliDate) : SheetState()
    data class Edit(val task: TaskEntity) : SheetState()
}

private sealed class OverlayState {
    data object None : OverlayState()
    data class TaskDetail(val task: TaskEntity) : OverlayState()
    data class DayDetail(val date: JalaliDate) : OverlayState()
    data object Search : OverlayState()
    data object Archive : OverlayState()
    data object Stats : OverlayState()
    data class HolidayDetail(val date: JalaliDate, val event: DayEvent) : OverlayState()
    data object NotificationPermission : OverlayState()
    data object Offline : OverlayState()
}

@Composable
fun MainScreen(
    viewModel: CalendarViewModel,
    preferences: AppPreferences,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onRequestNotificationPermission: () -> Unit = {}
) {
    val calendarState by viewModel.uiState.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    var destination by remember { mutableStateOf(AppDestination.Today) }
    var overlay by remember { mutableStateOf<OverlayState>(OverlayState.None) }
    var sheetState by remember { mutableStateOf<SheetState>(SheetState.Hidden) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    var showWelcome by remember { mutableStateOf(!preferences.hasSeenWelcome) }
    var pendingNotificationPrompt by remember { mutableStateOf(false) }
    var undoTask by remember { mutableStateOf<TaskEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf(preferences.userName) }

    LaunchedEffect(preferences.showHolidays, preferences.weekStartsOn) {
        viewModel.applyPreferences(preferences.showHolidays, preferences.weekStartsOn)
    }

    fun deleteWithUndo(task: TaskEntity) {
        viewModel.deleteTask(task)
        undoTask = task
        if (overlay is OverlayState.TaskDetail) {
            overlay = OverlayState.None
        }
    }

    LaunchedEffect(undoTask?.id) {
        val task = undoTask ?: return@LaunchedEffect
        delay(4000)
        if (undoTask?.id == task.id) {
            undoTask = null
        }
    }

    fun openTaskDetail(task: TaskEntity) {
        overlay = OverlayState.TaskDetail(task)
    }

    fun showSnackbar(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onAction?.invoke()
            }
        }
    }

    fun finishWelcome() {
        preferences.hasSeenWelcome = true
        showWelcome = false
    }

    fun maybePromptForNotifications(form: TaskFormData) {
        if (form.reminderTime != null && !preferences.hasSeenNotificationRationale) {
            pendingNotificationPrompt = true
        }
    }

    fun onTaskSaved(form: TaskFormData) {
        maybePromptForNotifications(form)
        if (pendingNotificationPrompt) {
            overlay = OverlayState.NotificationPermission
        }
    }

    fun completeTask(task: TaskEntity) {
        val dayTasks = allTasks.filter { it.jalaliDate == task.jalaliDate }
        if (!task.isDone) {
            val willFillJar = dayTasks.all { it.isDone || it.id == task.id }
            viewModel.toggleTaskDone(task)
            if (willFillJar) showCelebration = true
            showSnackbar(if (willFillJar) "Jar full for the day!" else "Task completed!")
        } else {
            viewModel.toggleTaskDone(task)
            showSnackbar("Task marked incomplete")
        }
    }

    CalendarTodoTheme(darkTheme = darkMode) {
        ProvideMockupScale {
        val colors = SweetTheme.colors
        if (showWelcome) {
            WelcomeScreen(
                onStart = { finishWelcome() },
                onSkip = { finishWelcome() }
            )
            return@ProvideMockupScale
        }

        if (overlay == OverlayState.NotificationPermission) {
            NotificationPermissionScreen(
                onAllow = {
                    preferences.hasSeenNotificationRationale = true
                    pendingNotificationPrompt = false
                    onRequestNotificationPermission()
                    overlay = OverlayState.None
                },
                onDismiss = {
                    preferences.hasSeenNotificationRationale = true
                    pendingNotificationPrompt = false
                    overlay = OverlayState.None
                }
            )
            return@ProvideMockupScale
        }

        val showMainChrome = overlay == OverlayState.None

        Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = colors.chocDeep,
                        contentColor = colors.cream,
                        actionColor = colors.lemon
                    )
                }
            },
            containerColor = colors.cream,
            bottomBar = {
                if (showMainChrome) {
                    Column {
                        Box(
                            modifier = Modifier
                                .background(colors.paper)
                        ) {
                            SweetBottomNav(selected = destination, onSelect = { destination = it })
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (val current = overlay) {
                    OverlayState.None -> {
                        when (destination) {
                            AppDestination.Today -> TodayScreen(
                                tasks = allTasks,
                                userName = userName,
                                onEditTask = { openTaskDetail(it) },
                                onCompleteTask = { completeTask(it) },
                                onDeleteTask = { deleteWithUndo(it) },
                                onOpenSearch = { overlay = OverlayState.Search },
                                onOpenStats = { overlay = OverlayState.Stats },
                                onOpenArchive = { overlay = OverlayState.Archive }
                            )
                            AppDestination.Week -> WeekScreen(
                                modifier = Modifier.fillMaxSize(),
                                tasks = allTasks,
                                weekStartsOn = calendarState.weekStartsOn,
                                onEditTask = { openTaskDetail(it) },
                                onCompleteTask = { completeTask(it) },
                                onDeleteTask = { deleteWithUndo(it) }
                            )
                            AppDestination.Month -> MonthScreen(
                                viewModel = viewModel,
                                onDayClick = { date ->
                                    viewModel.selectDate(date)
                                    overlay = OverlayState.DayDetail(date)
                                },
                                onOfflineClick = { overlay = OverlayState.Offline }
                            )
                            AppDestination.Settings -> SettingsScreen(
                                tasks = allTasks,
                                userName = userName,
                                darkMode = darkMode,
                                showHolidays = preferences.showHolidays,
                                weekStartsOn = preferences.weekStartsOn,
                                onDarkModeChange = { enabled ->
                                    preferences.darkMode = enabled
                                    onDarkModeChange(enabled)
                                },
                                onShowHolidaysChange = { enabled ->
                                    preferences.showHolidays = enabled
                                    viewModel.setShowHolidays(enabled)
                                },
                                onWeekStartsOnChange = { day ->
                                    preferences.weekStartsOn = day
                                    viewModel.setWeekStartsOn(day)
                                },
                                onUserNameChange = { name ->
                                    preferences.userName = name
                                    userName = name
                                },
                                onOpenStats = { overlay = OverlayState.Stats },
                                onOpenArchive = { overlay = OverlayState.Archive },
                                onOpenSearch = { overlay = OverlayState.Search }
                            )
                        }

                        if (destination != AppDestination.Settings) {
                            SweetFab(
                                onClick = {
                                    val date = when (destination) {
                                        AppDestination.Week -> calendarState.selectedDate
                                        AppDestination.Month -> calendarState.selectedDate
                                        else -> JalaliDate.today()
                                    }
                                    sheetState = SheetState.Add(date)
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 18.dp, bottom = 18.dp)
                            )
                        }
                    }
                    OverlayState.Search -> SearchScreen(
                        tasks = allTasks,
                        recentSearches = preferences.recentSearches,
                        onBack = { overlay = OverlayState.None },
                        onTaskClick = { openTaskDetail(it) },
                        onSearchSubmitted = { preferences.addRecentSearch(it) }
                    )
                    OverlayState.Archive -> ArchiveScreen(
                        tasks = allTasks,
                        onBack = { overlay = OverlayState.None },
                        onTaskClick = { openTaskDetail(it) },
                        onCompleteTask = { completeTask(it) },
                        onDeleteTask = { deleteWithUndo(it) }
                    )
                    OverlayState.Stats -> StatsScreen(
                        tasks = allTasks,
                        onBack = { overlay = OverlayState.None }
                    )
                    is OverlayState.TaskDetail -> {
                        val liveTask = allTasks.find { it.id == current.task.id } ?: current.task
                        TaskDetailScreen(
                            task = liveTask,
                            onBack = { overlay = OverlayState.None },
                            onEdit = { sheetState = SheetState.Edit(liveTask) },
                            onComplete = { completeTask(liveTask) },
                            onDelete = { taskToDelete = liveTask }
                        )
                    }
                    is OverlayState.DayDetail -> {
                        LaunchedEffect(current.date) {
                            viewModel.selectDate(current.date)
                        }
                        val dayTasks = allTasks.filter { it.jalaliDate == current.date.formatIso() }
                        val dayEvents = if (calendarState.selectedDate == current.date) {
                            calendarState.selectedDayEvents
                        } else {
                            emptyList()
                        }
                        DayDetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            date = current.date,
                            tasks = dayTasks,
                            events = dayEvents,
                            onBack = { overlay = OverlayState.None },
                            onTaskClick = { openTaskDetail(it) },
                            onAddTask = { sheetState = SheetState.Add(current.date) },
                            onHolidayClick = { event ->
                                overlay = OverlayState.HolidayDetail(current.date, event)
                            }
                        )
                    }
                    is OverlayState.HolidayDetail -> HolidayDetailScreen(
                        date = current.date,
                        title = current.event.description,
                        description = current.event.additionalDescription.ifBlank { current.event.description },
                        isHoliday = current.event.isHoliday,
                        onBack = { overlay = OverlayState.DayDetail(current.date) }
                    )
                    OverlayState.Offline -> OfflineScreen(
                        onBack = { overlay = OverlayState.None },
                        onRetry = {
                            viewModel.retryEventsLoad()
                            overlay = OverlayState.None
                        }
                    )
                    OverlayState.NotificationPermission -> Unit
                }

                undoTask?.let { task ->
                    SweetUndoSnackbar(
                        message = "\"${task.title}\" deleted",
                        visible = true,
                        onUndo = {
                            viewModel.restoreTask(task)
                            undoTask = null
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = if (showMainChrome) 88.dp else 16.dp)
                    )
                }
            }
        }

        when (val sheet = sheetState) {
            SheetState.Hidden -> Unit
            is SheetState.Add -> {
                TaskBottomSheet(
                    date = sheet.date,
                    existingTask = null,
                    onDismiss = { sheetState = SheetState.Hidden },
                    onConfirm = { form ->
                        viewModel.addTask(form)
                        sheetState = SheetState.Hidden
                        onTaskSaved(form)
                        showSnackbar("Task added to jar!")
                    }
                )
            }
            is SheetState.Edit -> {
                val task = sheet.task
                val d = JalaliDate.parseIso(task.jalaliDate)
                TaskBottomSheet(
                    date = d,
                    existingTask = task,
                    onDismiss = { sheetState = SheetState.Hidden },
                    onConfirm = { form ->
                        viewModel.updateTask(task, form)
                        sheetState = SheetState.Hidden
                        overlay = OverlayState.TaskDetail(
                            task.copy(
                                title = form.title,
                                notes = form.notes,
                                jalaliDate = form.jalaliDate.formatIso(),
                                reminderTime = form.reminderTime,
                                category = form.category,
                                priority = form.priority,
                                repeatWeekly = form.repeatWeekly
                            )
                        )
                        onTaskSaved(form)
                        showSnackbar("Task updated!")
                    }
                )
            }
        }
        }

    taskToDelete?.let { task ->
        PixelConfirmDialog(
            title = "Delete task?",
            message = "Are you sure you want to delete \"${task.title}\"?",
            confirmLabel = "Delete",
            dismissLabel = "Keep",
            onConfirm = {
                viewModel.deleteTask(task)
                taskToDelete = null
                overlay = OverlayState.None
                undoTask = task
            },
            onDismiss = { taskToDelete = null }
        )
    }

    if (showCelebration) {
        CompleteCelebrationDialog(onDismiss = { showCelebration = false })
    }
        }
    }
}
