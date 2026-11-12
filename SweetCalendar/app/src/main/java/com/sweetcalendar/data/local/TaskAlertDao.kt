package com.sweetcalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskAlertDao {

    @Query(
        "SELECT * FROM task_alerts WHERE taskId = :taskId ORDER BY jalaliDate ASC, reminderTime ASC"
    )
    suspend fun getForTask(taskId: Long): List<TaskAlertEntity>

    @Query(
        """
        SELECT a.* FROM task_alerts a
        INNER JOIN tasks t ON t.id = a.taskId
        WHERE t.isDone = 0 AND t.deletedAt IS NULL AND t.scheduleKind = 'TODO'
        ORDER BY a.jalaliDate ASC, a.reminderTime ASC
        """
    )
    suspend fun getActiveForIncompleteTodos(): List<TaskAlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<TaskAlertEntity>)

    @Query("DELETE FROM task_alerts WHERE taskId = :taskId")
    suspend fun deleteForTask(taskId: Long)
}
