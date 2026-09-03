package com.example.calendartodo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE deletedAt IS NULL ORDER BY jalaliDate ASC, createdAt ASC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE jalaliDate = :jalaliDate AND deletedAt IS NULL ORDER BY createdAt ASC")
    fun observeForDate(jalaliDate: String): Flow<List<TaskEntity>>

    @Query("SELECT DISTINCT jalaliDate FROM tasks WHERE deletedAt IS NULL")
    fun observeDatesWithTasks(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE tasks SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long)

    @Query("UPDATE tasks SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM tasks WHERE deletedAt IS NOT NULL AND deletedAt < :cutoff")
    suspend fun purgeDeletedBefore(cutoff: Long)

    @Query("UPDATE tasks SET isDone = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query(
        "SELECT * FROM tasks WHERE reminderTime IS NOT NULL AND isDone = 0 AND deletedAt IS NULL"
    )
    suspend fun getActiveReminders(): List<TaskEntity>

    @Query(
        "SELECT * FROM tasks WHERE jalaliDate = :jalaliDate AND deletedAt IS NULL " +
            "ORDER BY isDone ASC, createdAt ASC"
    )
    suspend fun getForDate(jalaliDate: String): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE deletedAt IS NULL ORDER BY jalaliDate ASC, createdAt ASC")
    suspend fun getAllActive(): List<TaskEntity>

    @Query("SELECT * FROM tasks ORDER BY jalaliDate DESC, createdAt ASC")
    suspend fun getAllIncludingDeleted(): List<TaskEntity>
}
