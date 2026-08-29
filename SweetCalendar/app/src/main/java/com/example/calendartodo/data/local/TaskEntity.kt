package com.example.calendartodo.data.local

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
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
