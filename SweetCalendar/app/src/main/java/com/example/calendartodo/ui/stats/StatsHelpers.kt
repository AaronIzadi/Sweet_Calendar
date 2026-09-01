package com.example.calendartodo.ui.stats

import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate

enum class ArchiveFilter(val label: String) {
    ThisWeek("This week"),
    ThisMonth("This month"),
    AllTime("All time")
}

fun filterCompletedTasks(tasks: List<TaskEntity>, filter: ArchiveFilter): List<TaskEntity> {
    val done = tasks.filter { it.isDone }
    val today = JalaliDate.today()
    return when (filter) {
        ArchiveFilter.AllTime -> done
        ArchiveFilter.ThisWeek -> {
            val week = JalaliDate.weekContaining(today).map { it.formatIso() }.toSet()
            done.filter { it.jalaliDate in week }
        }
        ArchiveFilter.ThisMonth -> {
            done.filter {
                val d = JalaliDate.parseIso(it.jalaliDate)
                d.year == today.year && d.month == today.month
            }
        }
    }.sortedByDescending { it.jalaliDate }
}

fun groupTasksByDay(tasks: List<TaskEntity>): List<Pair<String, List<TaskEntity>>> {
    val today = JalaliDate.today().formatIso()
    val yesterday = JalaliDate.today().minusDays(1).formatIso()
    return tasks.groupBy { it.jalaliDate }
        .toList()
        .sortedByDescending { it.first }
        .map { (iso, dayTasks) ->
            val date = JalaliDate.parseIso(iso)
            val label = when (iso) {
                today -> "TODAY · ${JalaliDate.MONTH_NAMES_EN[date.month - 1].uppercase()} ${date.day}"
                yesterday -> "YESTERDAY · ${JalaliDate.MONTH_NAMES_EN[date.month - 1].uppercase()} ${date.day}"
                else -> "${JalaliDate.WEEKDAY_NAMES_EN_SHORT[date.weekdayIndex()].uppercase()} · ${JalaliDate.MONTH_NAMES_EN[date.month - 1].uppercase()} ${date.day}"
            }
            label to dayTasks
        }
}

fun computeMonthCompletionPercent(tasks: List<TaskEntity>): Int {
    val today = JalaliDate.today()
    val monthTasks = tasks.filter {
        val d = JalaliDate.parseIso(it.jalaliDate)
        d.year == today.year && d.month == today.month
    }
    if (monthTasks.isEmpty()) return 0
    return ((monthTasks.count { it.isDone }.toFloat() / monthTasks.size) * 100).toInt()
}

data class WeekBar(val label: String, val ratio: Float, val isToday: Boolean)

fun computeWeeklyBars(tasks: List<TaskEntity>): List<WeekBar> {
    val today = JalaliDate.today()
    val week = JalaliDate.weekContaining(today)
    return week.map { day ->
        val iso = day.formatIso()
        val dayTasks = tasks.filter { it.jalaliDate == iso }
        val ratio = if (dayTasks.isEmpty()) 0f
        else dayTasks.count { it.isDone }.toFloat() / dayTasks.size
        WeekBar(
            label = JalaliDate.WEEKDAY_NAMES_EN_SHORT[day.weekdayIndex()].uppercase().take(2),
            ratio = ratio,
            isToday = day == today
        )
    }
}

/** Last 28 days, levels 0–4 for heatmap intensity. */
fun computeHeatmapLevels(tasks: List<TaskEntity>): List<Int> {
    val today = JalaliDate.today()
    return (27 downTo 0).map { offset ->
        val day = today.minusDays(offset)
        val dayTasks = tasks.filter { it.jalaliDate == day.formatIso() }
        when {
            dayTasks.isEmpty() -> 0
            else -> {
                val ratio = dayTasks.count { it.isDone }.toFloat() / dayTasks.size
                when {
                    ratio >= 1f -> 4
                    ratio >= 0.75f -> 3
                    ratio >= 0.5f -> 2
                    ratio > 0f -> 1
                    else -> 0
                }
            }
        }
    }
}

fun searchTasks(tasks: List<TaskEntity>, query: String): List<TaskEntity> {
    if (query.isBlank()) return emptyList()
    val q = query.trim().lowercase()
    return tasks.filter {
        it.title.lowercase().contains(q) ||
            it.notes.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
    }.sortedWith(compareBy({ it.isDone }, { it.jalaliDate }))
}
