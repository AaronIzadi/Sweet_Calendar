package com.sweetcalendar.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One weekday a habit repeats on.
 *
 * [weekdayIndex] matches [com.sweetcalendar.jalali.JalaliDate.weekdayIndex]:
 * 0 = Saturday … 6 = Friday.
 */
@Entity(
    tableName = "habit_days",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitId")],
)
data class HabitDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val weekdayIndex: Int,
    /** 24h "HH:mm", or null for no reminder on this day. */
    val reminderTime: String? = null,
)
