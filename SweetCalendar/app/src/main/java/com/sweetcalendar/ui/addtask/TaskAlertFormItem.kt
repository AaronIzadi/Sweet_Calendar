package com.sweetcalendar.ui.addtask

import com.sweetcalendar.data.local.TaskAlertEntity
import com.sweetcalendar.jalali.JalaliDate
data class TaskAlertFormItem(
    val stableKey: Long = System.nanoTime(),
    val persistedId: Long = 0,
    val date: JalaliDate,
    val time: String,
) {
    fun toEntity(taskId: Long): TaskAlertEntity = TaskAlertEntity(
        id = persistedId,
        taskId = taskId,
        jalaliDate = date.formatIso(),
        reminderTime = time,
    )

    companion object {
        fun fromEntity(entity: TaskAlertEntity): TaskAlertFormItem = TaskAlertFormItem(
            stableKey = entity.id,
            persistedId = entity.id,
            date = JalaliDate.parseIso(entity.jalaliDate),
            time = entity.reminderTime,
        )
    }
}

private fun TaskAlertEntity.parsedDate(): JalaliDate = JalaliDate.parseIso(jalaliDate)
