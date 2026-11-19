package com.sweetcalendar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sweetcalendar.CalendarTodoApp
import com.sweetcalendar.data.habitsOnDate
import com.sweetcalendar.data.local.TaskAlertEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.prefs.AppPreferences
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.RecurrenceEditScope
import com.sweetcalendar.recurrence.parsedDate
import com.sweetcalendar.recurrence.isVirtualOccurrence
import com.sweetcalendar.recurrence.occurrenceKey
import com.sweetcalendar.recurrence.resolveOccurrence
import com.sweetcalendar.recurrence.sameOccurrenceAs
import com.sweetcalendar.profile.isBirthdayTask
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.recurrence.tasksOnDate
import com.sweetcalendar.ui.addtask.RecurrenceEditScopeDialog
import com.sweetcalendar.ui.addtask.RecurrenceScopeMode
import com.sweetcalendar.ui.addtask.TaskBottomSheet
import com.sweetcalendar.ui.addtask.TaskFormData
import com.sweetcalendar.ui.archive.ArchiveScreen
import com.sweetcalendar.ui.calendar.CalendarViewModel
import com.sweetcalendar.ui.calendar.DayEvent
import com.sweetcalendar.ui.calendar.MonthScreen
import com.sweetcalendar.ui.components.PixelConfirmDialog
import com.sweetcalendar.ui.components.SweetBottomNav
import com.sweetcalendar.ui.components.SweetFab
import com.sweetcalendar.ui.components.SweetRecoverSnackbar
import com.sweetcalendar.ui.daydetail.DayDetailScreen
import com.sweetcalendar.ui.habit.HabitBottomSheet
import com.sweetcalendar.ui.habit.HabitDetailScreen
import com.sweetcalendar.ui.holiday.HolidayDetailScreen
import com.sweetcalendar.ui.navigation.AppDestination
import com.sweetcalendar.ui.navigation.sweetFullScreenSheetEnter
import com.sweetcalendar.ui.navigation.sweetFullScreenSheetExit
import com.sweetcalendar.ui.navigation.sweetScreenTransition
import com.sweetcalendar.ui.navigation.sweetScreenTransitionByDepth
import com.sweetcalendar.ui.offline.OfflineScreen
import com.sweetcalendar.ui.snackbar.RecoverSnackbarScreen
import com.sweetcalendar.ui.permission.NotificationPermissionScreen
import com.sweetcalendar.ui.search.SearchScreen
import com.sweetcalendar.ui.settings.SettingsScreen
import com.sweetcalendar.ui.stats.StatsScreen
import com.sweetcalendar.ui.taskdetail.TaskDetailScreen
import com.sweetcalendar.ui.theme.CalendarTodoTheme
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.ThemeFamily
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.rememberReducedMotion
import com.sweetcalendar.ui.today.TodayScreen
import com.sweetcalendar.ui.week.WeekScreen
import com.sweetcalendar.ui.welcome.WelcomeScreen
import androidx.compose.ui.platform.LocalContext
import com.sweetcalendar.reminder.NotificationPermissionHelper
import com.sweetcalendar.widget.SweetWidgets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed class SheetState {
    data object Hidden : SheetState()
    data class Add(val date: JalaliDate) : SheetState()
    data class Edit(val task: TaskEntity) : SheetState()
    data class EditHabit(val habit: HabitWithDays) : SheetState()
}

private sealed class OverlayState {
    data object None : OverlayState()
    data class TaskDetail(val task: TaskEntity, val returnTo: OverlayState = None) : OverlayState()
    data class HabitDetail(val habitId: Long, val returnTo: OverlayState = None) : OverlayState()
    data class DayDetail(val date: JalaliDate) : OverlayState()
    data object Search : OverlayState()
    data object Archive : OverlayState()
    data object Stats : OverlayState()
    data class HolidayDetail(
        val date: JalaliDate,
        val event: DayEvent,
        val returnTo: OverlayState = None,
    ) : OverlayState()
    data object NotificationPermission : OverlayState()
    data object Offline : OverlayState()
    data object RecoverSnackbar : OverlayState()
}

private data class ScreenKey(val overlay: OverlayState, val destination: AppDestination)

private sealed class AppRoot {
    data object Welcome : AppRoot()
    data object Main : AppRoot()
    data object NotificationPermission : AppRoot()
}

private fun OverlayState.depth(): Int = when (this) {
    OverlayState.None -> 0
    is OverlayState.TaskDetail, is OverlayState.HabitDetail, is OverlayState.HolidayDetail -> 2
    else -> 1
}

private fun AppRoot.depth(): Int = when (this) {
    AppRoot.Welcome -> 0
    AppRoot.Main -> 1
    AppRoot.NotificationPermission -> 2
}

@Composable
fun MainScreen(
    viewModel: CalendarViewModel,
    preferences: AppPreferences,
    darkMode: Boolean,
    themeFamily: ThemeFamily,
    onDarkModeChange: (Boolean) -> Unit,
    onThemeFamilyChange: (ThemeFamily) -> Unit,
    onRequestNotificationPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val eventRepository = remember(context) {
        (context.applicationContext as CalendarTodoApp).eventRepository
    }
    val calendarState by viewModel.uiState.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val allSubtasks by viewModel.allSubtasks.collectAsState()
    val allHabits by viewModel.allHabits.collectAsState()
    val habitCompletions by viewModel.habitCompletions.collectAsState()
    val deletedTasks by viewModel.deletedTasks.collectAsState()
    var destination by remember { mutableStateOf(AppDestination.Today) }
    var overlay by remember { mutableStateOf<OverlayState>(OverlayState.None) }
    var sheetState by remember { mutableStateOf<SheetState>(SheetState.Hidden) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<HabitWithDays?>(null) }
    var pendingRecurrenceEdit by remember {
        mutableStateOf<Pair<TaskEntity, TaskFormData>?>(null)
    }
    var pendingRecurrenceDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showWelcome by remember { mutableStateOf(!preferences.hasSeenWelcome) }
    var pendingNotificationPrompt by remember { mutableStateOf(false) }
    var recoverTask by remember { mutableStateOf<TaskEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf(preferences.userName) }
    var userBirthDate by remember { mutableStateOf(preferences.userBirthDate()) }
    var showHolidays by remember { mutableStateOf(preferences.showHolidays) }
    var weekStartsOn by remember { mutableStateOf(preferences.weekStartsOn) }
    var calendarSystem by remember { mutableStateOf(preferences.calendarSystem) }

    LaunchedEffect(showHolidays, weekStartsOn, calendarSystem) {
        preferences.showHolidays = showHolidays
        preferences.weekStartsOn = weekStartsOn
        preferences.calendarSystem = calendarSystem
        viewModel.applyPreferences(showHolidays, weekStartsOn, calendarSystem)
    }

    LaunchedEffect(userBirthDate) {
        viewModel.setUserBirthDate(userBirthDate)
    }

    LaunchedEffect(userName) {
        viewModel.setUserName(userName)
    }

    fun deleteWithRecover(task: TaskEntity) {
        if (task.isBirthdayTask()) return
        viewModel.deleteTask(task)
        recoverTask = task
        if (overlay is OverlayState.TaskDetail) {
            overlay = (overlay as OverlayState.TaskDetail).returnTo
        }
    }

    LaunchedEffect(recoverTask?.id) {
        val task = recoverTask ?: return@LaunchedEffect
        delay(MockupDimens.SNACKBAR_UNDO_VISIBLE_MS)
        if (recoverTask?.id == task.id) {
            recoverTask = null
        }
    }

    fun openTaskDetail(task: TaskEntity, returnTo: OverlayState = OverlayState.None) {
        overlay = OverlayState.TaskDetail(task, returnTo)
    }

    fun openHabitDetail(habit: HabitWithDays, returnTo: OverlayState = OverlayState.None) {
        overlay = OverlayState.HabitDetail(habit.id, returnTo)
    }

    fun onHabitSaved(hasReminder: Boolean) {
        pendingNotificationPrompt = NotificationPermissionHelper.shouldPromptForReminder(
            context = context,
            hasReminder = hasReminder,
        )
        if (pendingNotificationPrompt) {
            overlay = OverlayState.NotificationPermission
        }
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
        pendingNotificationPrompt = NotificationPermissionHelper.shouldPromptForReminder(
            context = context,
            hasReminder = form.reminderTime != null || form.alerts.isNotEmpty()
        )
    }

    fun onTaskSaved(form: TaskFormData) {
        maybePromptForNotifications(form)
        if (pendingNotificationPrompt) {
            overlay = OverlayState.NotificationPermission
        }
    }

    fun completeTask(task: TaskEntity) {
        if (task.isBirthdayTask()) return
        val listDate = if (task.isToDo()) JalaliDate.today() else task.parsedDate()
        val dayTasks = allTasks.tasksOnDate(listDate, userBirthDate, userName)
            .filter { !it.isBirthdayTask() }
        if (!task.isDone) {
            val willFillJar = dayTasks.all { it.isDone || it.sameOccurrenceAs(task) }
            viewModel.toggleTaskDone(task)
            showSnackbar(if (willFillJar) "Jar full for the day!" else "Task completed!")
        } else {
            viewModel.toggleTaskDone(task)
        }
    }

    CalendarTodoTheme(darkTheme = darkMode, themeFamily = themeFamily) {
        ProvideMockupScale {
        val colors = SweetTheme.colors
        val reducedMotion = rememberReducedMotion()
        var displayedSheet by remember { mutableStateOf<SheetState>(SheetState.Hidden) }
        if (sheetState != SheetState.Hidden) {
            displayedSheet = sheetState
        }
        val appRoot = when {
            showWelcome -> AppRoot.Welcome
            overlay == OverlayState.NotificationPermission -> AppRoot.NotificationPermission
            else -> AppRoot.Main
        }

        AnimatedContent(
            targetState = appRoot,
            transitionSpec = {
                if (reducedMotion) {
                    fadeIn(tween(SweetMotion.ShortMillis)) togetherWith fadeOut(tween(SweetMotion.ShortMillis))
                } else {
                    sweetScreenTransition(
                        fromDepth = initialState.depth(),
                        toDepth = targetState.depth(),
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            label = "app-root"
        ) { root ->
        when (root) {
            AppRoot.Welcome -> WelcomeScreen(
                onStart = { finishWelcome() },
                onSkip = { finishWelcome() }
            )
            AppRoot.NotificationPermission -> NotificationPermissionScreen(
                onAllow = {
                    pendingNotificationPrompt = false
                    onRequestNotificationPermission()
                    overlay = OverlayState.None
                },
                onDismiss = {
                    pendingNotificationPrompt = false
                    overlay = OverlayState.None
                }
            )
            AppRoot.Main -> {
        val showMainChrome = overlay == OverlayState.None ||
            overlay == OverlayState.Archive ||
            overlay == OverlayState.Stats ||
            overlay == OverlayState.RecoverSnackbar

        Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    val snackbarBottom = mockupDp(MockupDimens.SNACKBAR_BOTTOM)
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = snackbarBottom),
                    ) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = if (colors.isDark) colors.bannerBg else colors.chocDeep,
                            contentColor = if (colors.isDark) colors.ink else colors.cream,
                            actionColor = colors.lemon
                        )
                    }
                    recoverTask?.let { task ->
                        SweetRecoverSnackbar(
                            message = "\"${task.title}\" deleted",
                            visible = true,
                            onRecover = {
                                viewModel.restoreTask(task)
                                recoverTask = null
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = snackbarBottom),
                        )
                    }
                }
            },
            containerColor = colors.cream,
            bottomBar = {
                if (showMainChrome) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        Box(
                            modifier = Modifier
                                .background(colors.paper)
                        ) {
                            SweetBottomNav(
                                selected = destination,
                                onSelect = {
                                    destination = it
                                    if (overlay == OverlayState.Archive ||
                                        overlay == OverlayState.Stats ||
                                        overlay == OverlayState.RecoverSnackbar
                                    ) {
                                        overlay = OverlayState.None
                                    }
                                }
                            )
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
                val settingsScrollState = rememberSaveable(saver = ScrollState.Saver) {
                    ScrollState(0)
                }
                AnimatedContent(
                    targetState = ScreenKey(overlay, destination),
                    transitionSpec = {
                        if (reducedMotion) {
                            fadeIn(tween(SweetMotion.ShortMillis)) togetherWith fadeOut(tween(SweetMotion.ShortMillis))
                        } else {
                            sweetScreenTransitionByDepth(
                                depthOf = { it.overlay.depth() },
                                tabOrdinalOf = { it.destination.ordinal },
                            )
                        }
                    },
                    contentKey = {
                        when (val o = it.overlay) {
                            OverlayState.None -> it.destination
                            is OverlayState.DayDetail -> "day:${o.date.formatIso()}"
                            is OverlayState.TaskDetail -> "task:${o.task.occurrenceKey()}"
                            is OverlayState.HabitDetail -> "habit:${o.habitId}"
                            is OverlayState.HolidayDetail -> "holiday:${o.date.formatIso()}:${o.event.description}"
                            else -> o::class
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "screen"
                ) { key ->
                Box(Modifier.fillMaxSize().background(colors.cream)) {
                when (val current = key.overlay) {
                    OverlayState.None -> {
                        when (key.destination) {
                            AppDestination.Today -> TodayScreen(
                                tasks = allTasks,
                                habits = allHabits,
                                habitCompletions = habitCompletions,
                                subtasks = allSubtasks,
                                userName = userName,
                                userBirthDate = userBirthDate,
                                calendarSystem = calendarState.calendarSystem,
                                onEditTask = { openTaskDetail(it) },
                                onHabitClick = { openHabitDetail(it) },
                                onToggleHabitComplete = { occurrence ->
                                    viewModel.setHabitCompleted(
                                        habitId = occurrence.id,
                                        date = occurrence.date,
                                        completed = !occurrence.isDone,
                                    )
                                },
                                onAddTask = { sheetState = SheetState.Add(JalaliDate.today()) },
                                onCompleteTask = { completeTask(it) },
                                onDeleteTask = { deleteWithRecover(it) },
                                onOpenSearch = { overlay = OverlayState.Search },
                                onOpenStats = { overlay = OverlayState.Stats },
                                onOpenArchive = { overlay = OverlayState.Archive }
                            )
                            AppDestination.Week -> WeekScreen(
                                modifier = Modifier.fillMaxSize(),
                                tasks = allTasks,
                                habits = allHabits,
                                habitCompletions = habitCompletions,
                                userBirthDate = userBirthDate,
                                userName = userName,
                                weekStartsOn = calendarState.weekStartsOn,
                                calendarSystem = calendarState.calendarSystem,
                                onDayClick = { date ->
                                    viewModel.selectDate(date)
                                    overlay = OverlayState.DayDetail(date)
                                },
                                onHabitClick = { openHabitDetail(it) },
                            )
                            AppDestination.Month -> MonthScreen(
                                viewModel = viewModel,
                                onDayClick = { date ->
                                    viewModel.selectDate(date)
                                    overlay = OverlayState.DayDetail(date)
                                },
                                onOfflineClick = { overlay = OverlayState.Offline },
                                onEditTask = { openTaskDetail(it) },
                                onCompleteTask = { completeTask(it) },
                            )
                            AppDestination.Settings -> SettingsScreen(
                                scrollState = settingsScrollState,
                                tasks = allTasks,
                                userName = userName,
                                darkMode = darkMode,
                                themeFamily = themeFamily,
                                showHolidays = showHolidays,
                                weekStartsOn = weekStartsOn,
                                calendarSystem = calendarSystem,
                                exportRunner = viewModel.taskExportRunner,
                                onDarkModeChange = { enabled ->
                                    preferences.darkMode = enabled
                                    onDarkModeChange(enabled)
                                    SweetWidgets.updateAll(context)
                                },
                                onThemeFamilyChange = { family ->
                                    preferences.themeFamily = family
                                    onThemeFamilyChange(family)
                                    SweetWidgets.updateAll(context)
                                },
                                onShowHolidaysChange = { enabled ->
                                    showHolidays = enabled
                                    preferences.showHolidays = enabled
                                    viewModel.setShowHolidays(enabled)
                                },
                                onWeekStartsOnChange = { day ->
                                    weekStartsOn = day
                                    preferences.weekStartsOn = day
                                    viewModel.setWeekStartsOn(day)
                                },
                                onCalendarSystemChange = { system ->
                                    calendarSystem = system
                                    preferences.calendarSystem = system
                                    viewModel.setCalendarSystem(system)
                                },
                                onUserNameChange = { name ->
                                    preferences.userName = name
                                    userName = name
                                },
                                userBirthDate = userBirthDate,
                                eventRepository = eventRepository,
                                onUserBirthDateChange = { date ->
                                    preferences.birthDateJalali = date?.formatIso()
                                    userBirthDate = date
                                    SweetWidgets.updateAll(context)
                                },
                                onOpenStats = { overlay = OverlayState.Stats },
                                onOpenArchive = { overlay = OverlayState.Archive },
                                onOpenSearch = { overlay = OverlayState.Search },
                                onOpenRecoverSnackbar = { overlay = OverlayState.RecoverSnackbar },
                            )
                        }
                    }
                    OverlayState.Search -> SearchScreen(
                        tasks = allTasks,
                        habits = allHabits,
                        recentSearches = preferences.recentSearches,
                        onBack = { overlay = OverlayState.None },
                        onTaskClick = { openTaskDetail(it, OverlayState.Search) },
                        onHabitClick = { openHabitDetail(it, OverlayState.Search) },
                        onSearchSubmitted = { preferences.addRecentSearch(it) }
                    )
                    OverlayState.Archive -> ArchiveScreen(
                        tasks = allTasks,
                        habits = allHabits,
                        habitCompletions = habitCompletions,
                        onBack = { overlay = OverlayState.None },
                        onTaskClick = { openTaskDetail(it, OverlayState.Archive) },
                        onHabitClick = { openHabitDetail(it, OverlayState.Archive) },
                        onDeleteTask = { deleteWithRecover(it) }
                    )
                    OverlayState.Stats -> StatsScreen(
                        tasks = allTasks,
                        weekStartsOn = preferences.weekStartsOn,
                        onBack = { overlay = OverlayState.None }
                    )
                    is OverlayState.TaskDetail -> {
                        val liveTask = allTasks.resolveOccurrence(current.task)
                        val detailTask = if (current.task.isVirtualOccurrence()) {
                            current.task
                        } else {
                            liveTask
                        }
                        var todoAlerts by remember(detailTask.id) {
                            mutableStateOf<List<TaskAlertEntity>>(emptyList())
                        }
                        LaunchedEffect(detailTask.id, detailTask.scheduleKind) {
                            todoAlerts = if (detailTask.isToDo()) {
                                viewModel.getAlertsForTask(detailTask.id)
                            } else {
                                emptyList()
                            }
                        }
                        val detailSubtasks = remember(allSubtasks, detailTask.id) {
                            allSubtasks.filter { it.parentTaskId == detailTask.id }
                        }
                        TaskDetailScreen(
                            task = detailTask,
                            todoAlerts = todoAlerts,
                            subtasks = detailSubtasks,
                            onToggleSubtask = { viewModel.toggleSubtask(it) },
                            modifiable = !liveTask.isBirthdayTask(),
                            onBack = { overlay = current.returnTo },
                            onEdit = {
                                if (liveTask.isVirtualOccurrence()) {
                                    viewModel.materializeOccurrence(liveTask) { materialized ->
                                        sheetState = SheetState.Edit(materialized)
                                    }
                                } else {
                                    sheetState = SheetState.Edit(liveTask)
                                }
                            },
                            onComplete = { completeTask(liveTask) },
                            onDelete = {
                                if (viewModel.needsRecurrenceScopeForDelete(liveTask)) {
                                    pendingRecurrenceDelete = liveTask
                                } else {
                                    taskToDelete = liveTask
                                }
                            }
                        )
                    }
                    is OverlayState.HabitDetail -> {
                        val liveHabit = allHabits.firstOrNull { it.id == current.habitId }
                        if (liveHabit == null) {
                            LaunchedEffect(current.habitId) {
                                overlay = current.returnTo
                            }
                        } else {
                            val todayIso = JalaliDate.today().formatIso()
                            val isTodayDone = habitCompletions.any {
                                it.habitId == liveHabit.id && it.jalaliDate == todayIso
                            }
                            HabitDetailScreen(
                                habit = liveHabit,
                                onBack = { overlay = current.returnTo },
                                onEdit = { sheetState = SheetState.EditHabit(liveHabit) },
                                onToggleEnabled = {
                                    viewModel.setHabitEnabled(liveHabit.id, !liveHabit.isEnabled)
                                },
                                onDelete = { habitToDelete = liveHabit },
                                isTodayDone = isTodayDone,
                                onToggleTodayComplete = {
                                    viewModel.setHabitCompleted(
                                        habitId = liveHabit.id,
                                        date = JalaliDate.today(),
                                        completed = !isTodayDone,
                                    )
                                },
                            )
                        }
                    }
                    is OverlayState.DayDetail -> {
                        LaunchedEffect(current.date) {
                            viewModel.selectDate(current.date)
                        }
                        val dayTasks = allTasks.tasksOnDate(current.date, userBirthDate, userName)
                        val dayCompletedHabitIds = habitCompletions
                            .filter { it.jalaliDate == current.date.formatIso() }
                            .map { it.habitId }
                            .toSet()
                        val dayHabits = allHabits.habitsOnDate(current.date, dayCompletedHabitIds)
                        val dayEvents = if (calendarState.selectedDate == current.date) {
                            calendarState.selectedDayEvents
                        } else {
                            emptyList()
                        }
                        DayDetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            date = current.date,
                            tasks = dayTasks,
                            habits = dayHabits,
                            subtasks = allSubtasks,
                            events = dayEvents,
                            calendarSystem = calendarState.calendarSystem,
                            onBack = { overlay = OverlayState.None },
                            onTaskClick = { openTaskDetail(it, current) },
                            onHabitClick = { openHabitDetail(it, current) },
                            onToggleHabitComplete = { occurrence ->
                                viewModel.setHabitCompleted(
                                    habitId = occurrence.id,
                                    date = occurrence.date,
                                    completed = !occurrence.isDone,
                                )
                            },
                            onCompleteTask = { completeTask(it) },
                            onAddTask = { sheetState = SheetState.Add(current.date) },
                            onHolidayClick = { event ->
                                overlay = OverlayState.HolidayDetail(
                                    date = current.date,
                                    event = event,
                                    returnTo = current,
                                )
                            }
                        )
                    }
                    is OverlayState.HolidayDetail -> HolidayDetailScreen(
                        date = current.date,
                        title = current.event.description,
                        description = current.event.additionalDescription.ifBlank { current.event.description },
                        isHoliday = current.event.isHoliday,
                        calendarSystem = calendarState.calendarSystem,
                        onBack = { overlay = current.returnTo }
                    )
                    OverlayState.Offline -> OfflineScreen(
                        onBack = { overlay = OverlayState.None },
                        onRetry = {
                            viewModel.retryEventsLoad()
                            overlay = OverlayState.None
                        }
                    )
                    OverlayState.RecoverSnackbar -> RecoverSnackbarScreen(
                        deletedTasks = deletedTasks,
                        onBack = { overlay = OverlayState.None },
                        onRestore = { viewModel.restoreTask(it) },
                        onPermanentDelete = { viewModel.permanentlyDeleteTask(it) }
                    )
                    OverlayState.NotificationPermission -> Unit
                }
                }
                }

                AnimatedVisibility(
                    visible = overlay == OverlayState.None && destination != AppDestination.Settings,
                    enter = scaleIn(SweetMotion.shortTween()) + fadeIn(SweetMotion.shortTween()),
                    exit = scaleOut(SweetMotion.shortTween()) + fadeOut(SweetMotion.shortTween()),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 18.dp)
                ) {
                    SweetFab(
                        addSheetOpen = sheetState != SheetState.Hidden,
                        onClick = {
                            val date = when (destination) {
                                AppDestination.Week -> calendarState.selectedDate
                                AppDestination.Month -> calendarState.selectedDate
                                else -> JalaliDate.today()
                            }
                            sheetState = SheetState.Add(date)
                        }
                    )
                }

            }
        }

        AnimatedVisibility(
            visible = sheetState != SheetState.Hidden,
            enter = if (reducedMotion) fadeIn(tween(SweetMotion.ShortMillis)) else sweetFullScreenSheetEnter(),
            exit = if (reducedMotion) fadeOut(tween(SweetMotion.ShortMillis)) else sweetFullScreenSheetExit(),
            modifier = Modifier.fillMaxSize()
        ) {
            when (val sheet = displayedSheet) {
                SheetState.Hidden -> Unit
                is SheetState.Add -> {
                    TaskBottomSheet(
                        date = sheet.date,
                        existingTask = null,
                        calendarSystem = calendarState.calendarSystem,
                        eventRepository = eventRepository,
                        weekStartsOn = calendarState.weekStartsOn,
                        onDismiss = { sheetState = SheetState.Hidden },
                        onConfirm = { form ->
                            viewModel.addTask(form)
                            sheetState = SheetState.Hidden
                            onTaskSaved(form)
                            showSnackbar(themeFamily.taskAddedSnackbar)
                        },
                        onConfirmHabit = { form ->
                            viewModel.addHabit(form)
                            sheetState = SheetState.Hidden
                            onHabitSaved(form.dayReminders.values.any { it != null })
                            showSnackbar("Habit added!")
                        },
                    )
                }
                is SheetState.EditHabit -> {
                    HabitBottomSheet(
                        existing = sheet.habit,
                        weekStartsOn = calendarState.weekStartsOn,
                        onDismiss = { sheetState = SheetState.Hidden },
                        onConfirm = { form ->
                            viewModel.updateHabit(sheet.habit.id, form)
                            sheetState = SheetState.Hidden
                            onHabitSaved(form.dayReminders.values.any { it != null })
                            showSnackbar("Habit updated!")
                        },
                    )
                }
                is SheetState.Edit -> {
                    val task = sheet.task
                    val d = JalaliDate.parseIso(task.jalaliDate)
                    var existingAlerts by remember(task.id) {
                        mutableStateOf<List<TaskAlertEntity>?>(null)
                    }
                    LaunchedEffect(task.id) {
                        existingAlerts = viewModel.getAlertsForTask(task.id)
                    }
                    val loadedAlerts = existingAlerts
                    val existingSubtasks = remember(allSubtasks, task.id) {
                        allSubtasks
                            .filter { it.parentTaskId == task.id }
                            .map { com.sweetcalendar.ui.addtask.SubtaskFormItem.fromEntity(it) }
                    }
                    if (loadedAlerts == null) {
                        Unit
                    } else {
                    TaskBottomSheet(
                        date = d,
                        existingTask = task,
                        existingAlerts = loadedAlerts,
                        existingSubtasks = existingSubtasks,
                        calendarSystem = calendarState.calendarSystem,
                        eventRepository = eventRepository,
                        weekStartsOn = calendarState.weekStartsOn,
                        onDismiss = { sheetState = SheetState.Hidden },
                        onConfirm = { form ->
                            if (viewModel.needsRecurrenceScopeForEdit(task)) {
                                pendingRecurrenceEdit = task to form
                                sheetState = SheetState.Hidden
                            } else {
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
                                        scheduleKind = form.scheduleKind.name,
                                        repeatWeekly = form.repeatWeekly,
                                        recurrenceFreq = form.recurrenceFreq.name,
                                    ),
                                    returnTo = (overlay as? OverlayState.TaskDetail)?.returnTo
                                        ?: OverlayState.None
                                )
                                onTaskSaved(form)
                                showSnackbar("Task updated!")
                            }
                        }
                    )
                    }
                }
            }
        }

        pendingRecurrenceEdit?.let { (task, form) ->
            RecurrenceEditScopeDialog(
                mode = RecurrenceScopeMode.EDIT,
                onDismiss = { pendingRecurrenceEdit = null },
                onSelect = { scope ->
                    viewModel.updateTask(task, form, scope)
                    pendingRecurrenceEdit = null
                    overlay = OverlayState.TaskDetail(
                        task.copy(
                            title = form.title,
                            notes = form.notes,
                            jalaliDate = form.jalaliDate.formatIso(),
                            reminderTime = form.reminderTime,
                            category = form.category,
                            priority = form.priority,
                            repeatWeekly = form.repeatWeekly,
                            recurrenceFreq = form.recurrenceFreq.name,
                        ),
                        returnTo = (overlay as? OverlayState.TaskDetail)?.returnTo ?: OverlayState.None
                    )
                    onTaskSaved(form)
                    showSnackbar("Task updated!")
                }
            )
        }

        pendingRecurrenceDelete?.let { task ->
            RecurrenceEditScopeDialog(
                mode = RecurrenceScopeMode.DELETE,
                onDismiss = { pendingRecurrenceDelete = null },
                onSelect = { scope ->
                    viewModel.deleteTask(task, scope)
                    pendingRecurrenceDelete = null
                    overlay = (overlay as? OverlayState.TaskDetail)?.returnTo ?: OverlayState.None
                    recoverTask = task
                    showSnackbar("Task deleted")
                }
            )
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
                    overlay = (overlay as? OverlayState.TaskDetail)?.returnTo ?: OverlayState.None
                    recoverTask = task
                },
                onDismiss = { taskToDelete = null }
            )
        }

        habitToDelete?.let { habit ->
            PixelConfirmDialog(
                title = "Delete habit?",
                message = "Are you sure you want to delete \"${habit.name}\"?",
                confirmLabel = "Delete",
                dismissLabel = "Keep",
                onConfirm = {
                    viewModel.deleteHabit(habit.id)
                    habitToDelete = null
                    overlay = (overlay as? OverlayState.HabitDetail)?.returnTo ?: OverlayState.None
                },
                onDismiss = { habitToDelete = null }
            )
        }

        }
            }
        }
        }
    }
    }
}
