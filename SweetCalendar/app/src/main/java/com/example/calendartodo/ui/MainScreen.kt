package com.example.calendartodo.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.addtask.TaskBottomSheet
import com.example.calendartodo.ui.calendar.CalendarScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel
import com.example.calendartodo.ui.components.CompleteCelebrationDialog
import com.example.calendartodo.ui.components.PixelConfirmDialog
import com.example.calendartodo.ui.navigation.AppDestination
import com.example.calendartodo.ui.tasklist.TaskListScreen

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
    var taskToComplete by remember { mutableStateOf<TaskEntity?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    when (destination) {
        AppDestination.TaskList -> TaskListScreen(
            tasks = allTasks,
            onOpenCalendar = { destination = AppDestination.Calendar },
            onAddTask = { sheetState = SheetState.Add(JalaliDate.today()) },
            onEditTask = { sheetState = SheetState.Edit(it) },
            onCompleteTask = { taskToComplete = it },
            onDeleteTask = { taskToDelete = it }
        )
        AppDestination.Calendar -> CalendarScreen(
            viewModel = viewModel,
            onBack = { destination = AppDestination.TaskList },
            onAddTask = { sheetState = SheetState.Add(calendarState.selectedDate) },
            onEditTask = { sheetState = SheetState.Edit(it) },
            onDeleteTask = { taskToDelete = it },
            onCompleteTask = { taskToComplete = it }
        )
    }

    when (val sheet = sheetState) {
        SheetState.Hidden -> Unit
        is SheetState.Add -> {
            val d = sheet.date
            TaskBottomSheet(
                dayLabel = "${d.day} ${JalaliDate.MONTH_NAMES[d.month - 1]} ${d.year}",
                existingTask = null,
                onDismiss = { sheetState = SheetState.Hidden },
                onConfirm = { form ->
                    viewModel.addTask(form, sheet.date)
                    sheetState = SheetState.Hidden
                }
            )
        }
        is SheetState.Edit -> {
            val task = sheet.task
            val d = JalaliDate.parseIso(task.jalaliDate)
            TaskBottomSheet(
                dayLabel = "${d.day} ${JalaliDate.MONTH_NAMES[d.month - 1]} ${d.year}",
                existingTask = task,
                onDismiss = { sheetState = SheetState.Hidden },
                onConfirm = { form ->
                    viewModel.updateTask(task, form)
                    sheetState = SheetState.Hidden
                }
            )
        }
    }

    taskToDelete?.let { task ->
        PixelConfirmDialog(
            title = "Delete task?",
            message = "Are you sure you want to delete \"${task.title}\"?",
            confirmLabel = "Yes",
            onConfirm = {
                viewModel.deleteTask(task)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }

    taskToComplete?.let { task ->
        PixelConfirmDialog(
            title = "Mark complete?",
            message = "Mark \"${task.title}\" as completed?",
            confirmLabel = "Yes",
            onConfirm = {
                viewModel.toggleTaskDone(task)
                taskToComplete = null
                showCelebration = true
            },
            onDismiss = { taskToComplete = null }
        )
    }

    if (showCelebration) {
        CompleteCelebrationDialog(onDismiss = { showCelebration = false })
    }
}
