package com.example.calendartodo.repository

import com.example.calendartodo.data.local.TaskDao
import com.example.calendartodo.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()

    fun observeForDate(jalaliDate: String): Flow<List<TaskEntity>> = dao.observeForDate(jalaliDate)

    fun observeDatesWithTasks(): Flow<List<String>> = dao.observeDatesWithTasks()

    suspend fun addTask(
        title: String,
        notes: String,
        jalaliDate: String,
        reminderTime: String? = null,
        category: String = ""
    ): Long {
        return dao.upsert(
            TaskEntity(
                title = title,
                notes = notes,
                jalaliDate = jalaliDate,
                reminderTime = reminderTime,
                category = category
            )
        )
    }

    suspend fun updateTask(task: TaskEntity) = dao.update(task)

    suspend fun getActiveReminders(): List<TaskEntity> = dao.getActiveReminders()

    suspend fun deleteTask(task: TaskEntity) = dao.delete(task)

    suspend fun setDone(id: Long, done: Boolean) = dao.setDone(id, done)
}
