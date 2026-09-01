package com.example.calendartodo.ui.components

enum class TaskPriority(val label: String) {
    Low("Low"),
    Medium("Medium"),
    High("High");

    companion object {
        fun fromString(value: String): TaskPriority = entries.find {
            it.label.equals(value, ignoreCase = true)
        } ?: Medium
    }
}
