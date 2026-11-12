package com.sweetcalendar.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.sweetcalendar.data.forTask
import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.data.taskProgressSlice
import com.sweetcalendar.recurrence.occurrenceKey
import com.sweetcalendar.ui.motion.appearOnEnter

fun LazyListScope.scheduledAndTodoTaskSections(
    scheduled: List<TaskEntity>,
    todos: List<TaskEntity>,
    subtasksByParent: Map<Long, List<SubtaskEntity>> = emptyMap(),
    scheduledLabel: String = "TODAY",
    todoLabel: String = "TO DO",
    isNewTask: (Long) -> Boolean,
    onTaskClick: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onDeleteTask: ((TaskEntity) -> Unit)? = null,
    style: TaskCardStyle = TaskCardStyle.Mockup,
    done: List<TaskEntity> = emptyList(),
    completedHeader: (@Composable () -> Unit)? = null,
) {
    if (scheduled.isNotEmpty()) {
        item { SweetSectionLabel(scheduledLabel) }
        items(scheduled, key = { it.occurrenceKey() }) { task ->
            val isNew = isNewTask(task.id)
            val subs = subtasksByParent.forTask(task.id)
            val ratio = taskProgressSlice(task, subs).ratio.takeIf { subs.isNotEmpty() }
            SweetTaskCard(
                task = task,
                modifier = sweetTaskItemMotion().appearOnEnter(isNew),
                onClick = { onTaskClick(task) },
                onToggleComplete = { onCompleteTask(task) },
                onDelete = onDeleteTask?.let { delete -> { delete(task) } },
                style = style,
                subtaskProgressRatio = ratio,
            )
        }
    }
    if (todos.isNotEmpty()) {
        item { SweetSectionLabel(todoLabel) }
        items(todos, key = { it.occurrenceKey() }) { task ->
            val isNew = isNewTask(task.id)
            val subs = subtasksByParent.forTask(task.id)
            val ratio = taskProgressSlice(task, subs).ratio.takeIf { subs.isNotEmpty() }
            SweetTaskCard(
                task = task,
                modifier = sweetTaskItemMotion().appearOnEnter(isNew),
                onClick = { onTaskClick(task) },
                onToggleComplete = { onCompleteTask(task) },
                onDelete = onDeleteTask?.let { delete -> { delete(task) } },
                style = style,
                subtaskProgressRatio = ratio,
            )
        }
    }
    if (done.isNotEmpty() && completedHeader != null) {
        item { completedHeader() }
        items(done, key = { it.occurrenceKey() }) { task ->
            val isNew = isNewTask(task.id)
            val subs = subtasksByParent.forTask(task.id)
            val ratio = taskProgressSlice(task, subs).ratio.takeIf { subs.isNotEmpty() }
            SweetTaskCard(
                task = task,
                modifier = sweetTaskItemMotion().appearOnEnter(isNew),
                onClick = { onTaskClick(task) },
                onToggleComplete = { onCompleteTask(task) },
                onDelete = onDeleteTask?.let { delete -> { delete(task) } },
                style = style,
                subtaskProgressRatio = ratio,
            )
        }
    }
}
