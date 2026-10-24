package com.sweetcalendar.recurrence

import com.sweetcalendar.data.local.TaskDao
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.addtask.TaskFormData
import java.util.UUID

class RecurrenceEditor(private val dao: TaskDao) {

    fun needsScopeForEdit(task: TaskEntity): Boolean = task.seriesId != null

    fun needsScopeForDelete(task: TaskEntity): Boolean = task.seriesId != null

    suspend fun applyEdit(
        task: TaskEntity,
        form: TaskFormData,
        scope: RecurrenceEditScope,
    ): TaskEntity {
        if (!needsScopeForEdit(task)) {
            return saveSimple(task, form)
        }
        return when (scope) {
            RecurrenceEditScope.THIS_INSTANCE -> editThisInstance(task, form)
            RecurrenceEditScope.THIS_AND_FOLLOWING -> editThisAndFollowing(task, form)
            RecurrenceEditScope.ENTIRE_SERIES -> editEntireSeries(task, form)
        }
    }

    suspend fun applyDelete(task: TaskEntity, scope: RecurrenceEditScope) {
        if (!needsScopeForDelete(task)) {
            dao.softDelete(task.id, System.currentTimeMillis())
            return
        }
        val seriesId = task.seriesId
        if (seriesId == null) {
            dao.softDelete(task.id, System.currentTimeMillis())
            return
        }
        val now = System.currentTimeMillis()
        when (scope) {
            RecurrenceEditScope.THIS_INSTANCE -> dao.softDelete(task.id, now)
            RecurrenceEditScope.ENTIRE_SERIES -> {
                dao.getActiveBySeriesId(seriesId).forEach { dao.softDelete(it.id, now) }
            }
            RecurrenceEditScope.THIS_AND_FOLLOWING -> {
                val split = task.parsedDate()
                val untilStr = split.minusDays(1).formatIso()
                val siblings = dao.getActiveBySeriesId(seriesId)
                siblings.forEach { sibling ->
                    when {
                        sibling.parsedDate() < split ->
                            dao.update(sibling.copy(seriesUntil = untilStr))
                        else -> dao.softDelete(sibling.id, now)
                    }
                }
            }
        }
    }

    suspend fun spawnNextAfterComplete(task: TaskEntity): TaskEntity? {
        val seriesId = task.seriesId
        val anchor = if (seriesId != null) {
            dao.getActiveBySeriesId(seriesId).minByOrNull { it.parsedDate() }?.parsedDate()
                ?: task.parsedDate()
        } else {
            task.parsedDate()
        }
        if (!task.canSpawnNextOccurrence(anchor)) return null
        val max = task.recurrenceCount
        if (max != null) {
            val completedIndex = RecurrenceEngine.occurrenceIndex(
                anchor,
                task.parsedDate(),
                task.recurrenceFrequency(),
                interval = 1,
                weekdays = task.recurrenceWeekdaySet(),
                monthDay = task.recurrenceMonthDayRule(),
            ) ?: return null
            if (completedIndex >= max) return null
        }
        val freq = task.recurrenceFrequency()
        val next = RecurrenceEngine.nextOccurrence(
            from = task.parsedDate(),
            anchor = anchor,
            freq = freq,
            interval = 1,
            weekdays = task.recurrenceWeekdaySet(),
            monthDay = task.recurrenceMonthDayRule(),
        )
        return task.copy(
            id = 0,
            jalaliDate = next.formatIso(),
            isDone = false,
            completedAt = null,
            isRecurrenceException = false,
            deletedAt = null,
        )
    }

    private suspend fun saveSimple(task: TaskEntity, form: TaskFormData): TaskEntity {
        val updated = task.applyFormFields(
            title = form.title,
            notes = form.notes,
            jalaliDate = form.jalaliDate.formatIso(),
            reminderTime = form.reminderTime,
            category = form.category,
            priority = form.priority,
            freq = form.recurrenceFreq,
            recurrenceCount = form.recurrenceCount,
            recurrenceWeekdays = form.recurrenceWeekdays,
            recurrenceMonthDay = form.recurrenceMonthDay,
        )
        dao.update(updated)
        return updated
    }

    private suspend fun editThisInstance(task: TaskEntity, form: TaskFormData): TaskEntity {
        val inSeries = task.seriesId != null
        val base = task.applyFormFields(
            title = form.title,
            notes = form.notes,
            jalaliDate = form.jalaliDate.formatIso(),
            reminderTime = form.reminderTime,
            category = form.category,
            priority = form.priority,
            freq = form.recurrenceFreq,
            recurrenceCount = form.recurrenceCount,
            recurrenceWeekdays = form.recurrenceWeekdays,
            recurrenceMonthDay = form.recurrenceMonthDay,
        )
        val final = when {
            inSeries && form.recurrenceFreq == RecurrenceFreq.NONE -> base.copy(
                recurrenceFreq = RecurrenceFreq.NONE.name,
                repeatWeekly = false,
                isRecurrenceException = true,
                seriesId = task.seriesId,
            )
            inSeries -> base.copy(isRecurrenceException = true)
            else -> base
        }
        dao.update(final)
        return final
    }

    private suspend fun editEntireSeries(task: TaskEntity, form: TaskFormData): TaskEntity {
        val seriesId = task.seriesId ?: return saveSimple(task, form)
        val siblings = dao.getActiveBySeriesId(seriesId)
        var result = task
        val freq = form.recurrenceFreq
        val newSeriesId = if (freq == RecurrenceFreq.NONE) null else seriesId
        siblings.forEach { sibling ->
            val dateStr = if (sibling.id == task.id) {
                form.jalaliDate.formatIso()
            } else {
                sibling.jalaliDate
            }
            val updated = sibling.copy(
                title = form.title,
                notes = form.notes,
                jalaliDate = dateStr,
                reminderTime = form.reminderTime,
                category = form.category,
                priority = form.priority,
                repeatWeekly = freq == RecurrenceFreq.WEEKLY,
                recurrenceFreq = freq.name,
                recurrenceInterval = 1,
                recurrenceCount = if (freq == RecurrenceFreq.NONE) null else form.recurrenceCount,
                recurrenceWeekdays = recurrenceWeekdaysStorage(
                    freq,
                    JalaliDate.parseIso(dateStr),
                    form.recurrenceWeekdays,
                    form.recurrenceMonthDay,
                ),
                seriesId = newSeriesId,
                seriesUntil = if (freq == RecurrenceFreq.NONE) null else sibling.seriesUntil,
                isRecurrenceException = false,
            )
            dao.update(updated)
            if (sibling.id == task.id) result = updated
        }
        return result
    }

    private suspend fun editThisAndFollowing(task: TaskEntity, form: TaskFormData): TaskEntity {
        val seriesId = task.seriesId ?: return saveSimple(task, form)
        val split = task.parsedDate()
        val untilStr = split.minusDays(1).formatIso()
        val newSeriesId = UUID.randomUUID().toString()
        val freq = form.recurrenceFreq
        val siblings = dao.getActiveBySeriesId(seriesId)
        var result = task
        siblings.forEach { sibling ->
            when {
                sibling.parsedDate() < split -> {
                    dao.update(sibling.copy(seriesUntil = untilStr))
                }
                else -> {
                    val dateStr = if (sibling.id == task.id) {
                        form.jalaliDate.formatIso()
                    } else {
                        sibling.jalaliDate
                    }
                    val updated = sibling.applyFormFields(
                        title = form.title,
                        notes = form.notes,
                        jalaliDate = dateStr,
                        reminderTime = form.reminderTime,
                        category = form.category,
                        priority = form.priority,
                        freq = freq,
                        recurrenceCount = form.recurrenceCount,
                        recurrenceWeekdays = form.recurrenceWeekdays,
                        recurrenceMonthDay = form.recurrenceMonthDay,
                    ).copy(
                        seriesId = if (freq == RecurrenceFreq.NONE) null else newSeriesId,
                        seriesUntil = null,
                        isRecurrenceException = false,
                    )
                    dao.update(updated)
                    if (sibling.id == task.id) result = updated
                }
            }
        }
        return result
    }
}
