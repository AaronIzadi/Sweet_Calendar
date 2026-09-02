package com.example.calendartodo.repository

import com.example.calendartodo.data.local.TaskDao
import com.example.calendartodo.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

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
        repeatWeekly: Boolean = false
    ): Long {
        return dao.upsert(
            TaskEntity(
                title = title,
                notes = notes,
                jalaliDate = jalaliDate,
                reminderTime = reminderTime,
                category = category,
                priority = priority,
                repeatWeekly = repeatWeekly
            )
        )
    }

    suspend fun updateTask(task: TaskEntity) = dao.update(task.copy(deletedAt = null))

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

    suspend fun setDone(id: Long, done: Boolean) = dao.setDone(id, done)
}
