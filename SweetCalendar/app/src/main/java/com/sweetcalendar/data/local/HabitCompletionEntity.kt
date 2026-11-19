package com.sweetcalendar.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Marks a habit as done for a specific Jalali calendar day.
 */
@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId", "jalaliDate"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitId"), Index("jalaliDate")],
)
data class HabitCompletionEntity(
    val habitId: Long,
    val jalaliDate: String,
    val completedAt: Long = System.currentTimeMillis(),
)
