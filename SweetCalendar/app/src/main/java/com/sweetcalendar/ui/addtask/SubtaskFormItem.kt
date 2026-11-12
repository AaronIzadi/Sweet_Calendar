package com.sweetcalendar.ui.addtask

import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.jalali.JalaliDate

data class SubtaskFormItem(
    val id: Long? = null,
    val title: String,
    val priority: String = "Medium",
    val isDone: Boolean = false,
    val sortOrder: Int = 0,
    val jalaliDate: String? = null,
    /** Stable identity for editor list animations (new rows use [System.nanoTime]). */
    val clientKey: Long = 0L,
) {
    fun stableKey(): Long = when {
        id != null && id > 0L -> id
        clientKey != 0L -> clientKey
        else -> sortOrder.toLong()
    }

    fun displayDeadline(parentDefault: JalaliDate): JalaliDate =
        jalaliDate?.takeIf { it.isNotBlank() }?.let { JalaliDate.parseIso(it) } ?: parentDefault

    fun toEntity(parentTaskId: Long): SubtaskEntity = SubtaskEntity(
        id = id ?: 0,
        parentTaskId = parentTaskId,
        title = title.trim(),
        priority = priority,
        isDone = isDone,
        sortOrder = sortOrder,
        jalaliDate = jalaliDate?.takeIf { it.isNotBlank() },
    )

    companion object {
        fun fromEntity(entity: SubtaskEntity) = SubtaskFormItem(
            id = entity.id,
            title = entity.title,
            priority = entity.priority,
            isDone = entity.isDone,
            sortOrder = entity.sortOrder,
            jalaliDate = entity.jalaliDate,
            clientKey = entity.id,
        )
    }
}
