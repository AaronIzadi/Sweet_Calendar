package com.sweetcalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {
    @Query(
        """
        SELECT * FROM subtasks
        WHERE parentTaskId = :parentTaskId
        ORDER BY sortOrder ASC, id ASC
        """
    )
    fun observeForParent(parentTaskId: Long): Flow<List<SubtaskEntity>>

    @Query(
        """
        SELECT * FROM subtasks
        WHERE parentTaskId IN (:parentIds)
        ORDER BY parentTaskId ASC, sortOrder ASC, id ASC
        """
    )
    suspend fun getForParents(parentIds: List<Long>): List<SubtaskEntity>

    @Query(
        """
        SELECT * FROM subtasks
        WHERE parentTaskId = :parentId
        ORDER BY sortOrder ASC, id ASC
        """
    )
    suspend fun getForParent(parentId: Long): List<SubtaskEntity>

    @Query("SELECT * FROM subtasks ORDER BY parentTaskId ASC, sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(subtasks: List<SubtaskEntity>)

    @Query("DELETE FROM subtasks WHERE parentTaskId = :parentTaskId")
    suspend fun deleteForParent(parentTaskId: Long)

    @Query("UPDATE subtasks SET isDone = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query("DELETE FROM subtasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
