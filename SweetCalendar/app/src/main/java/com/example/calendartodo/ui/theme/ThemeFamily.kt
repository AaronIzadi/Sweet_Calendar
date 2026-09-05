package com.example.calendartodo.ui.theme

enum class ThemeFamily(val settingsValue: String) {
    Candy("Bubblegum"),
    Space("Orbit");

    companion object {
        fun fromPref(value: String): ThemeFamily =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: Candy
    }

    val widgetJarLabel: String
        get() = when (this) {
            Space -> "MISSION STATUS"
            Candy -> "TODAY'S JAR"
        }

    val taskAddedSnackbar: String
        get() = when (this) {
            Space -> "Task added to mission!"
            Candy -> "Task added to jar!"
        }
}
