package com.sweetcalendar.data

import com.sweetcalendar.data.local.TaskEntity

enum class TaskListScope {
    All,
    Scheduled,
    Todo,
    Habit,
}

fun TaskListScope.filterTasks(tasks: List<TaskEntity>): List<TaskEntity> = when (this) {
    TaskListScope.All -> tasks
    TaskListScope.Scheduled -> tasks.filter { !it.isToDo() }
    TaskListScope.Todo -> tasks.filter { it.isToDo() }
    TaskListScope.Habit -> emptyList()
}

fun activityScopeForPicker(scope: TaskListScope): TaskListScope = when (scope) {
    TaskListScope.Todo -> TaskListScope.Todo
    TaskListScope.Habit -> TaskListScope.Habit
    TaskListScope.Scheduled, TaskListScope.All -> TaskListScope.Scheduled
}

fun TaskListScope.archiveEmptyMessage(): String = when (this) {
    TaskListScope.Todo -> "No completed to-dos in this period"
    TaskListScope.Habit -> "No completed habits in this period"
    TaskListScope.Scheduled, TaskListScope.All -> "No completed tasks in this period"
}

fun TaskListScope.searchPlaceholder(): String = when (this) {
    TaskListScope.Todo -> "Search to-dos…"
    TaskListScope.Habit -> "Search habits…"
    TaskListScope.Scheduled, TaskListScope.All -> "Search tasks…"
}

fun TaskListScope.recoverEmptyMessage(): String = when (this) {
    TaskListScope.Todo -> "No deleted to-dos in this period"
    TaskListScope.Habit -> "Deleted habits can't be recovered"
    TaskListScope.Scheduled, TaskListScope.All -> "No deleted tasks in this period"
}

fun TaskListScope.searchEmptyMessage(): String = when (this) {
    TaskListScope.Todo -> "No matching to-dos"
    TaskListScope.Habit -> "No matching habits"
    TaskListScope.Scheduled, TaskListScope.All -> "No matching tasks"
}
