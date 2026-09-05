package com.example.calendartodo.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun themeTodayJarLabel(): String =
    if (SweetTheme.isSpace) "Mission status" else "Today's jar"

@Composable
fun themeAddTaskButtonText(isEdit: Boolean): String = when {
    isEdit -> "SAVE CHANGES"
    SweetTheme.isSpace -> "ADD TO MISSION"
    else -> "ADD TO JAR"
}

@Composable
fun themeEmptySubtitle(): String =
    if (SweetTheme.isSpace) {
        "Your mission log is empty — add a task and track your progress through orbit."
    } else {
        "Your jar is empty — add a task and watch it fill up with candy as you check things off."
    }

@Composable
fun themeProfileStats(streak: Int, completed: Int): String =
    if (SweetTheme.isSpace) {
        "$streak day streak · $completed missions complete"
    } else {
        "$streak day streak · $completed candies earned"
    }

@Composable
fun themeStatsTitle(): String =
    if (SweetTheme.isSpace) "Mission status" else "Your candy jar"

@Composable
fun themeStatsSubtitle(): String =
    if (SweetTheme.isSpace) "A clearer look at how you're doing" else "A sweeter look at how you're doing"

@Composable
fun themeCelebrationTitle(): String =
    if (SweetTheme.isSpace) "Mission complete!" else "Sweet job!"

@Composable
fun themeCelebrationMessage(): String =
    if (SweetTheme.isSpace) {
        "You completed the mission successfully!"
    } else {
        "You completed the task successfully!"
    }

@Composable
fun themeWelcomeTagline(): String =
    if (SweetTheme.isSpace) {
        "A Jalali calendar to-do list, planned one orbit at a time."
    } else {
        "A Persian calendar to-do list, decorated one candy at a time."
    }
