package com.sweetcalendar.data

import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.recurrence.isVirtualOccurrence

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

/**
 * Progress for a task in the day jar.
 *
 * - No subtasks: the task's own priority weight (m).
 * - With subtasks: the parent still owns weight m; each subtask is a share n/Σn of that m
 *   (Low=1, Medium=2, High=3), so completing a subtask fills (n/Σn)·m of the jar.
 */
fun taskProgressSlice(
    task: TaskEntity,
    subtasks: List<SubtaskEntity>,
): TaskProgressSlice {
    val parentWeight = priorityProgressWeight(task.priority).toFloat()
    if (subtasks.isEmpty()) {
        return if (task.isDone) TaskProgressSlice(parentWeight, parentWeight)
        else TaskProgressSlice(0f, parentWeight)
    }
    val subWeightSum = subtasks.sumOf { priorityProgressWeight(it.priority) }.toFloat()
    if (subWeightSum <= 0f) {
        return if (task.isDone) TaskProgressSlice(parentWeight, parentWeight)
        else TaskProgressSlice(0f, parentWeight)
    }
    val doneSubWeight = subtasks
        .filter { it.isDone }
        .sumOf { priorityProgressWeight(it.priority) }
        .toFloat()
    val completed = parentWeight * (doneSubWeight / subWeightSum)
    return TaskProgressSlice(completed, parentWeight)
}

fun habitProgressSlice(priority: String, isDone: Boolean): TaskProgressSlice {
    val w = priorityProgressWeight(priority).toFloat()
    return if (isDone) TaskProgressSlice(w, w) else TaskProgressSlice(0f, w)
}

fun aggregateHabitJarCounts(habits: List<Pair<String, Boolean>>): Pair<Float, Float> {
    var completed = 0f
    var total = 0f
    habits.forEach { (priority, isDone) ->
        val slice = habitProgressSlice(priority, isDone)
        completed += slice.completed
        total += slice.total
    }
    return completed to total
}

fun TaskEntity.effectiveIsDone(subtasks: List<SubtaskEntity>): Boolean {
    if (subtasks.isEmpty()) return isDone
    return subtasks.all { it.isDone }
}

fun List<SubtaskEntity>.groupedByParent(): Map<Long, List<SubtaskEntity>> =
    groupBy { it.parentTaskId }

fun Map<Long, List<SubtaskEntity>>.forTask(taskId: Long): List<SubtaskEntity> =
    get(taskId).orEmpty()

/**
 * Subtasks are stored against the real parent id. Virtual recurring occurrences use a
 * negative synthetic id, so resolve back to the anchor before lookup.
 */
fun Map<Long, List<SubtaskEntity>>.forTask(task: TaskEntity): List<SubtaskEntity> =
    forTask(task.subtaskParentId())

fun TaskEntity.subtaskParentId(): Long {
    if (!isVirtualOccurrence()) return id
    val raw = -id
    val anchor = raw / 1_000_000L
    return anchor.takeIf { it > 0 } ?: id
}

fun aggregateJarCounts(
    tasks: List<TaskEntity>,
    subtasksByParent: Map<Long, List<SubtaskEntity>>,
): Pair<Float, Float> {
    var completed = 0f
    var total = 0f
    tasks.forEach { task ->
        val slice = taskProgressSlice(task, subtasksByParent.forTask(task))
        completed += slice.completed
        total += slice.total
    }
    return completed to total
}
