package com.sweetcalendar.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sweetcalendar.MainActivity
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.TaskAlertEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.formatTime12h
import java.util.Calendar

class TaskReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

  /** Single-day task reminder (existing behavior). */
    fun schedule(task: TaskEntity) {
        if (task.isToDo()) return
        cancelTaskReminders(task.id)
        if (task.isDone) return
        val time = task.reminderTime ?: return
        val triggerAt = reminderMillis(task.jalaliDate, time) ?: return
        val now = System.currentTimeMillis()
        scheduleAlarm(
            title = task.title,
            body = buildTaskBody(task, isEarly = false),
            dateTimeLabel = formatDateTime(task.jalaliDate, time),
            triggerAt = triggerAt,
            requestCode = taskRequestCode(task.id, isEarly = false),
            now = now,
        )
        scheduleAlarm(
            title = task.title,
            body = buildTaskBody(task, isEarly = true),
            dateTimeLabel = formatDateTime(task.jalaliDate, time),
            triggerAt = triggerAt - EARLY_OFFSET_MS,
            requestCode = taskRequestCode(task.id, isEarly = true),
            now = now,
        )
    }

    fun scheduleTodoAlerts(task: TaskEntity, alerts: List<TaskAlertEntity>) {
        if (!task.isToDo()) return
        cancelTodoAlerts(alerts.map { it.id })
        cancelTaskReminders(task.id)
        if (task.isDone) return
        val now = System.currentTimeMillis()
        alerts.forEach { alert ->
            val triggerAt = reminderMillis(alert.jalaliDate, alert.reminderTime) ?: return@forEach
            scheduleAlarm(
                title = task.title,
                body = buildTaskBody(task, isEarly = false),
                dateTimeLabel = formatDateTime(alert.jalaliDate, alert.reminderTime),
                triggerAt = triggerAt,
                requestCode = alertRequestCode(alert.id, isEarly = false),
                now = now,
            )
            scheduleAlarm(
                title = task.title,
                body = buildTaskBody(task, isEarly = true),
                dateTimeLabel = formatDateTime(alert.jalaliDate, alert.reminderTime),
                triggerAt = triggerAt - EARLY_OFFSET_MS,
                requestCode = alertRequestCode(alert.id, isEarly = true),
                now = now,
            )
        }
    }

    fun cancel(taskId: Long) {
        cancelTaskReminders(taskId)
    }

    fun cancelTodoAlerts(alertIds: List<Long>) {
        alertIds.forEach { id ->
            cancelAlarm(alertRequestCode(id, isEarly = false))
            cancelAlarm(alertRequestCode(id, isEarly = true))
        }
    }

    private fun scheduleAlarm(
        title: String,
        body: String,
        dateTimeLabel: String,
        triggerAt: Long,
        requestCode: Int,
        now: Long,
    ) {
        if (triggerAt <= now) return
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TITLE, title)
            putExtra(TaskReminderReceiver.EXTRA_BODY, body)
            putExtra(TaskReminderReceiver.EXTRA_DATE_TIME, dateTimeLabel)
            putExtra(TaskReminderReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        setAlarm(triggerAt, pendingIntent, requestCode)
    }

    private fun setAlarm(triggerAt: Long, pendingIntent: PendingIntent, requestCode: Int) {
        val showIntent = PendingIntent.getActivity(
            context,
            requestCode + SHOW_INTENT_REQUEST_CODE_OFFSET,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms() -> {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent,
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerAt, showIntent),
                        pendingIntent,
                    )
                }
                else -> {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            }
        } catch (_: SecurityException) {
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent,
                )
            } catch (_: Exception) {
                // Task stays saved even if the OS blocks reminder scheduling.
            }
        } catch (_: Exception) {
            // Ignore scheduling failures so saving a task never crashes the app.
        }
    }

    private fun cancelTaskReminders(taskId: Long) {
        cancelAlarm(taskRequestCode(taskId, isEarly = false))
        cancelAlarm(taskRequestCode(taskId, isEarly = true))
    }

    private fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildTaskBody(task: TaskEntity, isEarly: Boolean): String {
        val parts = mutableListOf<String>()
        if (isEarly) parts += "Reminder in 10 minutes"
        if (task.notes.isNotBlank()) parts += task.notes
        if (task.category.isNotBlank()) parts += task.category
        return parts.joinToString(" · ").ifBlank { task.title }
    }

    private fun formatDateTime(jalaliDate: String, time: String): String {
        val date = JalaliDate.parseIso(jalaliDate)
        return "${date.formatIso()}, ${formatTime12h(time)}"
    }

    companion object {
        private const val EARLY_OFFSET_MS = 10 * 60 * 1000L
        private const val SHOW_INTENT_REQUEST_CODE_OFFSET = 2_000_000
        private const val TASK_REQUEST_BASE = 100_000
        private const val ALERT_REQUEST_BASE = 2_100_000

        private fun taskRequestCode(taskId: Long, isEarly: Boolean): Int {
            val base = (TASK_REQUEST_BASE + (taskId % 800_000)).toInt()
            return if (isEarly) base * 2 + 1 else base * 2
        }

        private fun alertRequestCode(alertId: Long, isEarly: Boolean): Int {
            val base = (ALERT_REQUEST_BASE + (alertId % 800_000)).toInt()
            return if (isEarly) base * 2 + 1 else base * 2
        }

        fun reminderMillis(jalaliDate: String, time: String): Long? {
            val parts = time.split(":")
            if (parts.size != 2) return null
            val hour = parts[0].toIntOrNull() ?: return null
            val minute = parts[1].toIntOrNull() ?: return null
            if (hour !in 0..23 || minute !in 0..59) return null
            val cal = JalaliDate.parseIso(jalaliDate).toGregorianCalendar()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}
