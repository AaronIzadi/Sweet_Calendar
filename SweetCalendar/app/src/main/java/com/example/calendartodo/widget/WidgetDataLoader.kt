package com.example.calendartodo.widget

import android.content.Context
import com.example.calendartodo.data.local.AppDatabase
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import kotlinx.coroutines.runBlocking

data class TodayWidgetData(
    val dateLabel: String,
    val done: Int,
    val total: Int,
    val progress: Int,
    val displayTasks: List<TaskEntity>
)

object WidgetDataLoader {

    fun loadToday(context: Context): TodayWidgetData = runBlocking {
        val today = JalaliDate.today()
        val tasks = AppDatabase.get(context).taskDao().getForDate(today.formatIso())
        val done = tasks.count { it.isDone }
        val total = tasks.size
        val progress = if (total > 0) (done * 100) / total else 0
        val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[today.weekdayIndex()]
        val month = JalaliDate.MONTH_NAMES_EN[today.month - 1]
        val pending = tasks.filter { !it.isDone }.take(3)
        val doneTasks = tasks.filter { it.isDone }.take(3 - pending.size)
        TodayWidgetData(
            dateLabel = "$month ${today.day} · $weekday",
            done = done,
            total = total,
            progress = progress,
            displayTasks = pending + doneTasks
        )
    }
}

fun formatWidgetTime(time24: String): String {
    val parts = time24.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return time24
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val amPm = if (h < 12) "AM" else "PM"
    val hour12 = when (val mod = h % 12) { 0 -> 12; else -> mod }
    return "$hour12:${m.toString().padStart(2, '0')} $amPm"
}

fun widgetTaskTitle(task: TaskEntity): String {
    if (task.isDone) return task.title
    val timeSuffix = task.reminderTime?.let { " · ${formatWidgetTime(it)}" }.orEmpty()
    return task.title + timeSuffix
}

object SweetWidgets {
    fun updateAll(context: Context) {
        TodayWidgetProvider.updateInstances(context)
        JarWidgetProvider.updateInstances(context)
    }
}
