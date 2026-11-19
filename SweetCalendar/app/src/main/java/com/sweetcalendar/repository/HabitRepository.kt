package com.sweetcalendar.repository

import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.local.HabitCompletionEntity
import com.sweetcalendar.data.local.HabitDao
import com.sweetcalendar.data.local.HabitDayEntity
import com.sweetcalendar.data.local.HabitEntity
import com.sweetcalendar.jalali.JalaliDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class HabitRepository(
    private val dao: HabitDao,
) {

    fun observeAll(): Flow<List<HabitWithDays>> =
        combine(dao.observeAll(), dao.observeAllDays()) { habits, days ->
            val byHabit = days.groupBy { it.habitId }
            habits.map { habit ->
                HabitWithDays(
                    habit = habit,
                    days = byHabit[habit.id].orEmpty().sortedBy { it.weekdayIndex },
                )
            }
        }

    fun observeCompletions(): Flow<List<HabitCompletionEntity>> =
        dao.observeCompletions()

    fun observeCompletedHabitIdsForDate(date: JalaliDate): Flow<Set<Long>> =
        dao.observeCompletionsForDate(date.formatIso()).map { rows ->
            rows.map { it.habitId }.toSet()
        }

    suspend fun getById(id: Long): HabitWithDays? {
        val habit = dao.getById(id) ?: return null
        return HabitWithDays(habit, dao.getDaysForHabit(id))
    }

    suspend fun getEnabledWithDays(): List<HabitWithDays> {
        val habits = dao.getEnabled()
        val days = dao.getAllDays().groupBy { it.habitId }
        return habits.map { HabitWithDays(it, days[it.id].orEmpty()) }
    }

    suspend fun addHabit(
        name: String,
        dayReminders: Map<Int, String?>,
        category: String = "Personal",
        priority: String = "Medium",
        startDate: JalaliDate = JalaliDate.today(),
    ): Long {
        val iso = startDate.formatIso()
        val id = dao.upsert(
            HabitEntity(
                name = name.trim(),
                category = category,
                priority = priority,
                isEnabled = true,
                createdJalali = iso,
                enabledFromJalali = iso,
                disabledAtJalali = null,
            )
        )
        replaceDays(id, dayReminders)
        return id
    }

    suspend fun updateHabit(
        habitId: Long,
        name: String,
        dayReminders: Map<Int, String?>,
        category: String,
        priority: String,
    ) {
        val existing = dao.getById(habitId) ?: return
        dao.update(
            existing.copy(
                name = name.trim(),
                category = category,
                priority = priority,
            ),
        )
        replaceDays(habitId, dayReminders)
    }

    suspend fun setEnabled(habitId: Long, enabled: Boolean) {
        val existing = dao.getById(habitId) ?: return
        val today = JalaliDate.today().formatIso()
        val updated = if (enabled) {
            existing.copy(
                isEnabled = true,
                enabledFromJalali = today,
                disabledAtJalali = null,
            )
        } else {
            existing.copy(
                isEnabled = false,
                disabledAtJalali = today,
            )
        }
        dao.update(updated)
    }

    suspend fun deleteHabit(habitId: Long) {
        dao.deleteCompletionsForHabit(habitId)
        dao.deleteDaysForHabit(habitId)
        dao.delete(habitId)
    }

    suspend fun setCompleted(habitId: Long, date: JalaliDate, completed: Boolean) {
        val iso = date.formatIso()
        if (completed) {
            dao.upsertCompletion(
                HabitCompletionEntity(
                    habitId = habitId,
                    jalaliDate = iso,
                ),
            )
        } else {
            dao.deleteCompletion(habitId, iso)
        }
    }

    suspend fun isCompleted(habitId: Long, date: JalaliDate): Boolean =
        dao.getCompletion(habitId, date.formatIso()) != null

    private suspend fun replaceDays(habitId: Long, dayReminders: Map<Int, String?>) {
        dao.deleteDaysForHabit(habitId)
        if (dayReminders.isEmpty()) return
        dao.upsertDays(
            dayReminders.map { (weekday, time) ->
                HabitDayEntity(
                    habitId = habitId,
                    weekdayIndex = weekday.coerceIn(0, 6),
                    reminderTime = time?.takeIf { it.isNotBlank() },
                )
            }
        )
    }
}
