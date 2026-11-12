package com.sweetcalendar.data

import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.parsedDate
import com.sweetcalendar.ui.components.TaskCategory

fun TaskEntity.scheduleKind(): TaskScheduleKind = TaskScheduleKind.fromStored(scheduleKind)

fun TaskEntity.isScheduledTask(): Boolean = scheduleKind() == TaskScheduleKind.TASK

fun TaskEntity.isToDo(): Boolean = scheduleKind() == TaskScheduleKind.TODO

fun taskCategoriesFor(scheduleKind: TaskScheduleKind): List<TaskCategory> = when (scheduleKind) {
    TaskScheduleKind.TODO -> listOf(TaskCategory.Personal, TaskCategory.Work, TaskCategory.School)
    TaskScheduleKind.TASK -> listOf(TaskCategory.Personal, TaskCategory.Home, TaskCategory.Work)
}

fun TaskCategory.coercedFor(scheduleKind: TaskScheduleKind): TaskCategory {
    val allowed = taskCategoriesFor(scheduleKind)
  return if (this in allowed) this else allowed.first()
}

/** Incomplete to-dos appear only on their deadline date (calendar markers and day lists). */
fun TaskEntity.isToDoVisibleOn(date: JalaliDate): Boolean {
    if (!isToDo() || isDone) return false
    return parsedDate() == date
}

fun List<TaskEntity>.todosWithDeadlineInRange(
    start: JalaliDate,
    end: JalaliDate,
): List<TaskEntity> {
    if (start > end) return emptyList()
    return filter { task ->
        task.isToDo() && task.parsedDate() in start..end
    }.sortedWith(compareBy({ it.parsedDate() }, { it.createdAt }))
}
