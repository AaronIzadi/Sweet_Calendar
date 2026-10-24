package com.sweetcalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A personal to-do item. [jalaliDate] is stored as an ISO-ish "yyyy-MM-dd"
 * string in *Jalali* year/month/day so it sorts and groups naturally.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String = "",
    val jalaliDate: String, // e.g. "1403-05-12"
    val reminderTime: String? = null, // "HH:mm" in 24h format, null = no reminder
    val category: String = "",
    val priority: String = "Medium",
    val repeatWeekly: Boolean = false,
    /** Shared id for recurring instances spawned from the same rule. */
    val seriesId: String? = null,
    val recurrenceFreq: String = "NONE",
    /** Always 1 (every week/month); spacing is not user-configurable. */
    val recurrenceInterval: Int = 1,
    /** Total occurrences (first + N following weeks/months); null = no limit. */
    val recurrenceCount: Int? = null,
    /** Weekly: comma-separated weekday indices (0=Sat … 6=Fri). Monthly: "", "LAST", or "D1"…"D31". */
    val recurrenceWeekdays: String = "",
    /** Last Jalali date (inclusive) for this segment of a split series. */
    val seriesUntil: String? = null,
    /** When true, completing this row does not spawn the next occurrence. */
    val isRecurrenceException: Boolean = false,
    val isDone: Boolean = false,
    /** Local millis when the task was marked complete; cleared when marked incomplete. */
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    /** When set, task is in the trash and hidden from active lists. */
    val deletedAt: Long? = null
)
