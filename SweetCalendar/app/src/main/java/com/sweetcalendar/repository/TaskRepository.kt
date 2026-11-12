package com.sweetcalendar.repository

import com.sweetcalendar.data.TaskScheduleKind
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.effectiveIsDone
import com.sweetcalendar.data.local.SubtaskDao
import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskAlertDao
import com.sweetcalendar.data.local.TaskAlertEntity
import com.sweetcalendar.data.local.TaskDao
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.ui.addtask.SubtaskFormItem
import com.sweetcalendar.ui.addtask.TaskAlertFormItem
import com.sweetcalendar.recurrence.RecurrenceEditScope
import com.sweetcalendar.recurrence.RecurrenceEditor
import com.sweetcalendar.recurrence.RecurrenceFreq
import com.sweetcalendar.recurrence.RecurrenceMonthDay
import com.sweetcalendar.recurrence.isVirtualOccurrence
import com.sweetcalendar.recurrence.recurrenceFrequency
import com.sweetcalendar.recurrence.withRecurrence
import com.sweetcalendar.ui.addtask.TaskFormData
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val dao: TaskDao,
    private val alertDao: TaskAlertDao,
    private val subtaskDao: SubtaskDao,
) {

    private val recurrenceEditor = RecurrenceEditor(dao)

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    fun observeDeleted(): Flow<List<TaskEntity>> = dao.observeDeleted()

    fun observeForDate(jalaliDate: String): Flow<List<TaskEntity>> = dao.observeForDate(jalaliDate)

    fun observeDatesWithTasks(): Flow<List<String>> = dao.observeDatesWithTasks()

    fun observeSubtasks(): Flow<List<SubtaskEntity>> = subtaskDao.observeAll()

    suspend fun getSubtasksForParent(parentId: Long): List<SubtaskEntity> =
        subtaskDao.getForParent(parentId)

    suspend fun replaceSubtasks(parentId: Long, items: List<SubtaskFormItem>) {
        subtaskDao.deleteForParent(parentId)
        if (items.isEmpty()) return
        val entities = items
            .filter { it.title.isNotBlank() }
            .mapIndexed { index, item ->
                item.copy(sortOrder = index).toEntity(parentId)
            }
        subtaskDao.upsertAll(entities)
        syncParentCompletion(parentId)
    }

    suspend fun toggleSubtaskDone(subtask: SubtaskEntity) {
        subtaskDao.setDone(subtask.id, !subtask.isDone)
        syncParentCompletion(subtask.parentTaskId)
    }

    suspend fun setSubtasksDone(parentId: Long, done: Boolean) {
        val subs = subtaskDao.getForParent(parentId)
        subs.forEach { subtaskDao.setDone(it.id, done) }
        syncParentCompletion(parentId)
    }

    private suspend fun syncParentCompletion(parentId: Long) {
        val subs = subtaskDao.getForParent(parentId)
        if (subs.isEmpty()) return
        val task = dao.getById(parentId) ?: return
        val done = task.effectiveIsDone(subs)
        val completedAt = if (done) System.currentTimeMillis() else null
        dao.setDone(parentId, done, completedAt)
    }

    suspend fun addTask(
        title: String,
        notes: String,
        jalaliDate: String,
        reminderTime: String? = null,
        category: String = "",
        priority: String = "Medium",
        repeatWeekly: Boolean = false,
        recurrenceFreq: RecurrenceFreq = RecurrenceFreq.fromLegacyRepeatWeekly(repeatWeekly),
        recurrenceCount: Int? = null,
        recurrenceWeekdays: Set<Int> = emptySet(),
        recurrenceMonthDay: RecurrenceMonthDay = RecurrenceMonthDay.OnThisDay,
        scheduleKind: TaskScheduleKind = TaskScheduleKind.TASK,
    ): Long {
        val freq = if (scheduleKind == TaskScheduleKind.TODO) RecurrenceFreq.NONE else recurrenceFreq
        return dao.upsert(
            TaskEntity(
                title = title,
                notes = notes,
                jalaliDate = jalaliDate,
                reminderTime = reminderTime,
                category = category,
                priority = priority,
                repeatWeekly = freq == RecurrenceFreq.WEEKLY,
                scheduleKind = scheduleKind.name,
            ).withRecurrence(
                freq,
                interval = 1,
                count = recurrenceCount,
                weekdays = recurrenceWeekdays,
                monthDay = recurrenceMonthDay,
            )
        )
    }

    suspend fun insertTask(task: TaskEntity): Long = dao.upsert(task)

    /** Inserts a projected occurrence so complete/edit apply to the correct day. */
    suspend fun materializeOccurrence(task: TaskEntity): TaskEntity {
        if (!task.isVirtualOccurrence()) return task
        val entity = task.copy(
            id = 0,
            isDone = false,
            completedAt = null,
            deletedAt = null,
            isRecurrenceException = false,
        ).withRecurrence(task.recurrenceFrequency(), count = task.recurrenceCount)
        val newId = dao.upsert(entity)
        return entity.copy(id = newId)
    }

    suspend fun updateTask(task: TaskEntity) = dao.update(task.copy(deletedAt = null))

    suspend fun applyRecurrenceEdit(
        task: TaskEntity,
        form: TaskFormData,
        scope: RecurrenceEditScope,
    ): TaskEntity = recurrenceEditor.applyEdit(task, form, scope)

    fun needsRecurrenceScopeForEdit(task: TaskEntity): Boolean =
        recurrenceEditor.needsScopeForEdit(task)

    fun needsRecurrenceScopeForDelete(task: TaskEntity): Boolean =
        recurrenceEditor.needsScopeForDelete(task)

    suspend fun applyRecurrenceDelete(task: TaskEntity, scope: RecurrenceEditScope) {
        recurrenceEditor.applyDelete(task, scope)
    }

    suspend fun spawnNextAfterComplete(task: TaskEntity): TaskEntity? =
        recurrenceEditor.spawnNextAfterComplete(task)

    suspend fun getActiveBySeriesId(seriesId: String): List<TaskEntity> =
        dao.getActiveBySeriesId(seriesId)

    suspend fun getActiveReminders(): List<TaskEntity> = dao.getActiveReminders()

    suspend fun getAlertsForTask(taskId: Long): List<TaskAlertEntity> =
        alertDao.getForTask(taskId)

    suspend fun getActiveTodoAlerts(): List<TaskAlertEntity> =
        alertDao.getActiveForIncompleteTodos()

    suspend fun replaceAlerts(taskId: Long, alerts: List<TaskAlertFormItem>) {
        alertDao.deleteForTask(taskId)
        if (alerts.isEmpty()) return
        alertDao.insertAll(alerts.map { it.toEntity(taskId) })
    }

    suspend fun getAllActiveTodosWithAlerts(): List<Pair<TaskEntity, List<TaskAlertEntity>>> {
        val todos = dao.getAllActive().filter { it.isToDo() && !it.isDone }
        return todos.map { task ->
            task to alertDao.getForTask(task.id)
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.softDelete(task.id, System.currentTimeMillis())
    }

    suspend fun restoreTask(task: TaskEntity) {
        dao.restore(task.id)
    }

    suspend fun permanentlyDeleteTask(task: TaskEntity) {
        dao.hardDelete(task.id)
    }

    suspend fun purgeExpiredDeleted(
        retentionMs: Long = DELETED_TASK_RETENTION_MS,
        now: Long = System.currentTimeMillis()
    ) {
        dao.purgeDeletedBefore(now - retentionMs)
    }

    suspend fun setDone(id: Long, done: Boolean) {
        val subs = subtaskDao.getForParent(id)
        if (subs.isNotEmpty()) {
            setSubtasksDone(id, done)
            return
        }
        val completedAt = if (done) System.currentTimeMillis() else null
        dao.setDone(id, done, completedAt)
    }
}
