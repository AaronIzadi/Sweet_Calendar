package com.sweetcalendar.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.get(context)
                val taskDao = db.taskDao()
                val alertDao = db.taskAlertDao()
                val habitDao = db.habitDao()
                val scheduler = TaskReminderScheduler(context.applicationContext)
                taskDao.getActiveReminders().forEach { task ->
                    try {
                        if (task.isToDo()) return@forEach
                        scheduler.schedule(task)
                    } catch (_: Exception) {
                        // Skip tasks the OS refuses to schedule after reboot.
                    }
                }
                val alerts = alertDao.getActiveForIncompleteTodos()
                val tasksById = taskDao.getAllActive().associateBy { it.id }
                alerts.groupBy { it.taskId }.forEach { (taskId, rows) ->
                    val task = tasksById[taskId] ?: return@forEach
                    try {
                        scheduler.scheduleTodoAlerts(task, rows)
                    } catch (_: Exception) {
                        // Skip alerts the OS refuses to schedule after reboot.
                    }
                }
                val habitDays = habitDao.getAllDays().groupBy { it.habitId }
                habitDao.getEnabled().forEach { habit ->
                    try {
                        scheduler.scheduleHabit(
                            com.sweetcalendar.data.HabitWithDays(
                                habit = habit,
                                days = habitDays[habit.id].orEmpty(),
                            )
                        )
                    } catch (_: Exception) {
                        // Skip habits the OS refuses to schedule after reboot.
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
