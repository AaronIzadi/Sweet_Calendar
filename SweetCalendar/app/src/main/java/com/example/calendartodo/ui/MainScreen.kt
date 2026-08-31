package com.example.calendartodo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.addtask.TaskBottomSheet
import com.example.calendartodo.ui.calendar.CalendarScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel
import com.example.calendartodo.ui.components.CompleteCelebrationDialog
import com.example.calendartodo.ui.components.PixelConfirmDialog
import com.example.calendartodo.ui.navigation.AppDestination
import com.example.calendartodo.ui.tasklist.TaskListScreen
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting
import kotlinx.coroutines.launch

private sealed class SheetState {
    data object Hidden : SheetState()
    data class Add(val date: JalaliDate) : SheetState()
    data class Edit(val task: TaskEntity) : SheetState()
}

@Composable
fun MainScreen(viewModel: CalendarViewModel) {
    val calendarState by viewModel.uiState.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    var destination by remember { mutableStateOf(AppDestination.TaskList) }
    var sheetState by remember { mutableStateOf<SheetState>(SheetState.Hidden) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = ChocolateBrown,
                    contentColor = CreamFrosting,
                    actionColor = BubblegumPink
                )
            }
        },
        containerColor = CreamFrosting
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (destination) {
                AppDestination.TaskList -> TaskListScreen(
                    tasks = allTasks,
                    onOpenCalendar = { destination = AppDestination.Calendar },
                    onAddTask = { sheetState = SheetState.Add(JalaliDate.today()) },
                    onEditTask = { sheetState = SheetState.Edit(it) },
                    onCompleteTask = { task ->
                        if (!task.isDone) {
                            viewModel.toggleTaskDone(task)
                            showCelebration = true
                            showSnackbar("Task completed!")
                        } else {
                            viewModel.toggleTaskDone(task)
                            showSnackbar("Task marked as incomplete")
                        }
                    },
                    onDeleteTask = { taskToDelete = it }
                )
                AppDestination.Calendar -> CalendarScreen(
                    viewModel = viewModel,
                    onBack = { destination = AppDestination.TaskList },
                    onAddTask = { sheetState = SheetState.Add(calendarState.selectedDate) },
                    onEditTask = { sheetState = SheetState.Edit(it) },
                    onDeleteTask = { taskToDelete = it },
                    onCompleteTask = { task ->
                        if (!task.isDone) {
                            viewModel.toggleTaskDone(task)
                            showCelebration = true
                            showSnackbar("Task completed!")
                        } else {
                            viewModel.toggleTaskDone(task)
                            showSnackbar("Task marked as incomplete")
                        }
                    }
                )
            }
        }
    }

    when (val sheet = sheetState) {
        SheetState.Hidden -> Unit
        is SheetState.Add -> {
            val d = sheet.date
            TaskBottomSheet(
                dayLabel = d.formatIso(),
                existingTask = null,
                onDismiss = { sheetState = SheetState.Hidden },

                onConfirm = { form ->
                    viewModel.addTask(form, sheet.date)
                    sheetState = SheetState.Hidden
                    showSnackbar("Task added!")
                }
            )
        }
        is SheetState.Edit -> {
            val task = sheet.task
            val d = JalaliDate.parseIso(task.jalaliDate)
            TaskBottomSheet(
                dayLabel = d.formatIso(),
                existingTask = task,
                onDismiss = { sheetState = SheetState.Hidden },

                onConfirm = { form ->
                    viewModel.updateTask(task, form)
                    sheetState = SheetState.Hidden
                    showSnackbar("Task updated!")
                }
            )
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
                showSnackbar("Task deleted", actionLabel = "Undo") {
                    viewModel.restoreTask(task)
                }
            },
            onDismiss = { taskToDelete = null }
        )
    }

    if (showCelebration) {
        CompleteCelebrationDialog(onDismiss = { showCelebration = false })
    }
}
