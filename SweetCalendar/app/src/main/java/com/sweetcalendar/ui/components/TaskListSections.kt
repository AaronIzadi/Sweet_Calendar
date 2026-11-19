package com.sweetcalendar.ui.components

import androidx.compose.foundation.lazy.LazyItemScope
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
    doneScheduled: List<TaskEntity> = emptyList(),
    doneTodos: List<TaskEntity> = emptyList(),
    scheduledCompletedHeader: (@Composable () -> Unit)? = null,
    todoCompletedHeader: (@Composable () -> Unit)? = null,
) {
    taskSectionWithCompleted(
        label = scheduledLabel,
        pending = scheduled,
        completed = doneScheduled,
        completedHeader = scheduledCompletedHeader,
        pendingKey = { it.occurrenceKey() },
        completedKey = { "done:${it.occurrenceKey()}" },
    ) { task ->
        sectionTaskCard(
            task = task,
            subtasksByParent = subtasksByParent,
            isNew = isNewTask(task.id),
            onTaskClick = onTaskClick,
            onCompleteTask = onCompleteTask,
            onDeleteTask = onDeleteTask,
            style = style,
        )
    }
    taskSectionWithCompleted(
        label = todoLabel,
        pending = todos,
        completed = doneTodos,
        completedHeader = todoCompletedHeader,
        pendingKey = { it.occurrenceKey() },
        completedKey = { "done:${it.occurrenceKey()}" },
    ) { task ->
        sectionTaskCard(
            task = task,
            subtasksByParent = subtasksByParent,
            isNew = isNewTask(task.id),
            onTaskClick = onTaskClick,
            onCompleteTask = onCompleteTask,
            onDeleteTask = onDeleteTask,
            style = style,
        )
    }
}

private fun <T> LazyListScope.taskSectionWithCompleted(
    label: String,
    pending: List<T>,
    completed: List<T>,
    completedHeader: (@Composable () -> Unit)?,
    pendingKey: (T) -> Any,
    completedKey: (T) -> Any,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (pending.isEmpty() && completed.isEmpty()) return
    item { SweetSectionLabel(label) }
    items(pending, key = pendingKey, itemContent = itemContent)
    if (completed.isNotEmpty()) {
        if (completedHeader != null) {
            item { completedHeader() }
        } else {
            item {
                CompletedSectionLink(
                    count = completed.size,
                    suffix = "",
                )
            }
        }
        items(completed, key = completedKey, itemContent = itemContent)
    }
}

@Composable
private fun LazyItemScope.sectionTaskCard(
    task: TaskEntity,
    subtasksByParent: Map<Long, List<SubtaskEntity>>,
    isNew: Boolean,
    onTaskClick: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onDeleteTask: ((TaskEntity) -> Unit)?,
    style: TaskCardStyle,
) {
    val subs = subtasksByParent.forTask(task)
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
