package com.sweetcalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE isEnabled = 1 ORDER BY createdAt ASC")
    suspend fun getEnabled(): List<HabitEntity>

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    suspend fun getAll(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM habit_days ORDER BY habitId ASC, weekdayIndex ASC")
    fun observeAllDays(): Flow<List<HabitDayEntity>>

    @Query("SELECT * FROM habit_days WHERE habitId = :habitId ORDER BY weekdayIndex ASC")
    suspend fun getDaysForHabit(habitId: Long): List<HabitDayEntity>

    @Query("SELECT * FROM habit_days ORDER BY habitId ASC, weekdayIndex ASC")
    suspend fun getAllDays(): List<HabitDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDay(day: HabitDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDays(days: List<HabitDayEntity>)

    @Query("DELETE FROM habit_days WHERE habitId = :habitId")
    suspend fun deleteDaysForHabit(habitId: Long)

    @Query("SELECT * FROM habit_completions")
    fun observeCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions")
    suspend fun getAllCompletions(): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions WHERE jalaliDate = :jalaliDate")
    fun observeCompletionsForDate(jalaliDate: String): Flow<List<HabitCompletionEntity>>

    @Query(
        "SELECT * FROM habit_completions WHERE habitId = :habitId AND jalaliDate = :jalaliDate LIMIT 1",
    )
    suspend fun getCompletion(habitId: Long, jalaliDate: String): HabitCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND jalaliDate = :jalaliDate")
    suspend fun deleteCompletion(habitId: Long, jalaliDate: String)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)
}
