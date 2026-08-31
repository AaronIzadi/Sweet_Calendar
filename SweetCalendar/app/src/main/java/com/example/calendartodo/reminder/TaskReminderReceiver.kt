package com.example.calendartodo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.calendartodo.ui.alarm.AlarmActivity

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val body = intent.getStringExtra(EXTRA_BODY).orEmpty()
        val dateTime = intent.getStringExtra(EXTRA_DATE_TIME).orEmpty()
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (title.isBlank()) return

        NotificationHelper.showReminder(context, notificationId, title, body)

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(AlarmActivity.EXTRA_TITLE, title)
            putExtra(AlarmActivity.EXTRA_DESCRIPTION, body)
            putExtra(AlarmActivity.EXTRA_DATE_TIME, dateTime)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(alarmIntent)
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_BODY = "body"
        const val EXTRA_DATE_TIME = "date_time"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }
}
