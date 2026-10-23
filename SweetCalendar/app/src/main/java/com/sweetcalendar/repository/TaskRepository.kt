package com.sweetcalendar.repository

import com.sweetcalendar.data.local.TaskDao
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.recurrence.RecurrenceEditScope
import com.sweetcalendar.recurrence.RecurrenceEditor
import com.sweetcalendar.recurrence.RecurrenceFreq
import com.sweetcalendar.recurrence.isVirtualOccurrence
import com.sweetcalendar.recurrence.recurrenceFrequency
import com.sweetcalendar.recurrence.withRecurrence
import com.sweetcalendar.ui.addtask.TaskFormData
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    private val recurrenceEditor = RecurrenceEditor(dao)

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    fun observeDeleted(): Flow<List<TaskEntity>> = dao.observeDeleted()

    fun observeForDate(jalaliDate: String): Flow<List<TaskEntity>> = dao.observeForDate(jalaliDate)

    fun observeDatesWithTasks(): Flow<List<String>> = dao.observeDatesWithTasks()

    suspend fun addTask(
        title: String,
        notes: String,
        jalaliDate: String,
        reminderTime: String? = null,
        category: String = "",
        priority: String = "Medium",
        repeatWeekly: Boolean = false,
        recurrenceFreq: RecurrenceFreq = RecurrenceFreq.fromLegacyRepeatWeekly(repeatWeekly),
        recurrenceInterval: Int = 1,
        recurrenceWeekdays: Set<Int> = emptySet(),
    ): Long {
        return dao.upsert(
            TaskEntity(
                title = title,
                notes = notes,
                jalaliDate = jalaliDate,
                reminderTime = reminderTime,
                category = category,
                priority = priority,
                repeatWeekly = recurrenceFreq == RecurrenceFreq.WEEKLY,
            ).withRecurrence(
                recurrenceFreq,
                interval = recurrenceInterval.coerceAtLeast(1),
                count = null,
                weekdays = recurrenceWeekdays,
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
        val completedAt = if (done) System.currentTimeMillis() else null
        dao.setDone(id, done, completedAt)
    }
}
