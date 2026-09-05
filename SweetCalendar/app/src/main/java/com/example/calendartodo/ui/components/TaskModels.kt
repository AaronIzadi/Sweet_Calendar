package com.example.calendartodo.ui.components

enum class TaskPriority(val label: String, val sparkleCount: Int) {
    Low("Low", 1),
    Medium("Medium", 2),
    High("High", 3);

    companion object {
        fun fromString(value: String): TaskPriority = entries.find {
            it.label.equals(value, ignoreCase = true)
        } ?: Medium
    }
}
