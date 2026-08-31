package com.example.calendartodo.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import java.util.Calendar

class TaskReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(task: TaskEntity) {
        cancel(task.id)
        if (task.isDone) return
        val time = task.reminderTime ?: return
        val triggerAt = reminderMillis(task.jalaliDate, time) ?: return
        val now = System.currentTimeMillis()
        scheduleAlarm(task, triggerAt, requestCode(task.id, false), isEarly = false, now)
        scheduleAlarm(task, triggerAt - EARLY_OFFSET_MS, requestCode(task.id, true), isEarly = true, now)
    }

    fun cancel(taskId: Long) {
        cancelAlarm(requestCode(taskId, false))
        cancelAlarm(requestCode(taskId, true))
    }

    private fun scheduleAlarm(
        task: TaskEntity,
        triggerAt: Long,
        requestCode: Int,
        isEarly: Boolean,
        now: Long
    ) {
        if (triggerAt <= now) return
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TITLE, task.title)
            putExtra(TaskReminderReceiver.EXTRA_BODY, buildBody(task, isEarly))
            putExtra(TaskReminderReceiver.EXTRA_DATE_TIME, formatDateTime(task))
            putExtra(TaskReminderReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildBody(task: TaskEntity, isEarly: Boolean): String {
        val parts = mutableListOf<String>()
        if (isEarly) parts += "Reminder in 10 minutes"
        if (task.notes.isNotBlank()) parts += task.notes
        if (task.category.isNotBlank()) parts += task.category
        return parts.joinToString(" · ").ifBlank { task.title }
    }

    private fun formatDateTime(task: TaskEntity): String {
        val date = JalaliDate.parseIso(task.jalaliDate)
        val time = task.reminderTime.orEmpty()
        return date.formatIso() + if (time.isNotBlank()) ", $time" else ""
    }

    companion object {
        private const val EARLY_OFFSET_MS = 10 * 60 * 1000L

        private fun requestCode(taskId: Long, isEarly: Boolean): Int {
            val base = (taskId % Int.MAX_VALUE).toInt()
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
