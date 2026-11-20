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
import com.sweetcalendar.recurrence.anchorTaskIdFromVirtual
import com.sweetcalendar.recurrence.isVirtualOccurrence
import com.sweetcalendar.recurrence.recurrenceFrequency
import com.sweetcalendar.recurrence.withRecurrence
import com.sweetcalendar.ui.addtask.TaskFormData
import java.util.UUID
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

    /**
     * Assigns a stable [TaskEntity.seriesId] to every active recurring task that lacks one.
     * Prevents virtual projections and spawned rows from drifting into duplicate series.
     */
    suspend fun ensureRecurringSeriesIds() {
        dao.getAllActive()
            .filter { it.recurrenceFrequency() != RecurrenceFreq.NONE && it.seriesId == null }
            .forEach { orphan ->
                dao.update(orphan.withRecurrence(orphan.recurrenceFrequency()))
            }
    }

    /**
     * Inserts a projected occurrence so complete/edit/delete apply to the correct day.
     * Keeps the anchor's seriesId (assigning one if missing) so the day does not also
     * keep a virtual duplicate.
     */
    suspend fun materializeOccurrence(task: TaskEntity): TaskEntity {
        if (!task.isVirtualOccurrence()) return task
        val freq = task.recurrenceFrequency()
        val seriesId = ensureSeriesIdForVirtual(task, freq)
        val entity = task.copy(
            id = 0,
            seriesId = seriesId,
            isDone = false,
            completedAt = null,
            deletedAt = null,
            isRecurrenceException = false,
            recurrenceFreq = freq.name,
            recurrenceCount = if (freq == RecurrenceFreq.NONE) null else task.recurrenceCount,
            repeatWeekly = freq == RecurrenceFreq.WEEKLY,
        )
        val newId = dao.upsert(entity)
        return entity.copy(id = newId)
    }

    private suspend fun ensureSeriesIdForVirtual(
        task: TaskEntity,
        freq: RecurrenceFreq,
    ): String? {
        if (freq == RecurrenceFreq.NONE) return null
        task.seriesId?.let { return it }
        val newSeriesId = UUID.randomUUID().toString()
        val anchorId = anchorTaskIdFromVirtual(task.id)
        val anchor = anchorId?.let { dao.getById(it) }
        if (anchor != null) {
            val existing = anchor.seriesId
            if (existing != null) return existing
            dao.update(anchor.withRecurrence(freq, seriesId = newSeriesId))
            return newSeriesId
        }
        return newSeriesId
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

    /**
     * Restores a soft-deleted task. Virtual occurrence ids are never stored; those resolve
     * to the materialized row created during delete (match by date + series).
     */
    suspend fun restoreTask(task: TaskEntity): TaskEntity? {
        val id = resolveRestorableId(task) ?: return null
        dao.restore(id)
        return dao.getById(id)
    }

    private suspend fun resolveRestorableId(task: TaskEntity): Long? {
        if (!task.isVirtualOccurrence()) return task.id
        val dateIso = task.jalaliDate
        val seriesId = task.seriesId
            ?: anchorTaskIdFromVirtual(task.id)?.let { dao.getById(it)?.seriesId }
        val deletedOnDate = dao.getAllIncludingDeleted()
            .filter { it.deletedAt != null && it.jalaliDate == dateIso && it.id > 0 }
        val match = when {
            seriesId != null -> deletedOnDate.filter { it.seriesId == seriesId }
            else -> deletedOnDate.filter {
                it.title == task.title &&
                    (it.seriesId != null || it.recurrenceFrequency() != RecurrenceFreq.NONE)
            }
        }.maxByOrNull { it.deletedAt ?: 0L }
        return match?.id
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
