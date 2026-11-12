package com.sweetcalendar.data

import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
fun priorityProgressWeight(priority: String): Int = when (priority.trim().lowercase()) {
    "low" -> 1
    "high" -> 3
    else -> 2
}

data class TaskProgressSlice(
    val completed: Float,
    val total: Float,
) {
    val ratio: Float get() = if (total <= 0f) 0f else (completed / total).coerceIn(0f, 1f)
}

fun taskProgressSlice(
    task: TaskEntity,
    subtasks: List<SubtaskEntity>,
): TaskProgressSlice {
    if (subtasks.isEmpty()) {
        return if (task.isDone) TaskProgressSlice(1f, 1f) else TaskProgressSlice(0f, 1f)
    }
  var total = 0f
    var completed = 0f
    subtasks.forEach { sub ->
        val w = priorityProgressWeight(sub.priority).toFloat()
        total += w
        if (sub.isDone) completed += w
    }
    return TaskProgressSlice(completed, total)
}

fun TaskEntity.effectiveIsDone(subtasks: List<SubtaskEntity>): Boolean {
    if (subtasks.isEmpty()) return isDone
    return subtasks.all { it.isDone }
}

fun List<SubtaskEntity>.groupedByParent(): Map<Long, List<SubtaskEntity>> =
    groupBy { it.parentTaskId }

fun Map<Long, List<SubtaskEntity>>.forTask(taskId: Long): List<SubtaskEntity> =
    get(taskId).orEmpty()

fun aggregateJarCounts(
    tasks: List<TaskEntity>,
    subtasksByParent: Map<Long, List<SubtaskEntity>>,
): Pair<Float, Float> {
    var completed = 0f
    var total = 0f
    tasks.forEach { task ->
        val slice = taskProgressSlice(task, subtasksByParent.forTask(task.id))
        completed += slice.completed
        total += slice.total
    }
    return completed to total
}
