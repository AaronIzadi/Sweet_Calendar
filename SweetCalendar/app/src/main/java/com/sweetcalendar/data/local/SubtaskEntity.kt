package com.sweetcalendar.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentTaskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("parentTaskId")],
)
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentTaskId: Long,
    val title: String,
    val priority: String = "Medium",
    val isDone: Boolean = false,
    val sortOrder: Int = 0,
    /** Jalali ISO date when parent is a to-do; null inherits the parent deadline. */
    val jalaliDate: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
