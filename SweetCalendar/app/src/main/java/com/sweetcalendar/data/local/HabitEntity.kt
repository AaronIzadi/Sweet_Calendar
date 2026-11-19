package com.sweetcalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A weekly habit that repeats forever on chosen weekdays.
 *
 * While [isEnabled] is true it appears on matching weekdays from [enabledFromJalali]
 * onward. When disabled, it only appears on matching weekdays in
 * [[enabledFromJalali], [disabledAtJalali]] (inclusive).
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** [com.sweetcalendar.ui.habit.HabitCategory] label; default Personal. */
    val category: String = "Personal",
    /** [com.sweetcalendar.ui.components.TaskPriority] label; default Medium. */
    val priority: String = "Medium",
    val isEnabled: Boolean = true,
    /** Jalali ISO date when the habit was first created. */
    val createdJalali: String,
    /** Start of the current (or last) enabled window. */
    val enabledFromJalali: String,
    /** End of the last enabled window; null while the habit is enabled. */
    val disabledAtJalali: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
