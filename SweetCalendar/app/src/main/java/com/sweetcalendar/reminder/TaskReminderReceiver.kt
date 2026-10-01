package com.sweetcalendar.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val body = intent.getStringExtra(EXTRA_BODY).orEmpty()
        val dateTime = intent.getStringExtra(EXTRA_DATE_TIME).orEmpty()
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (title.isBlank()) return

        NotificationHelper.showReminder(context, notificationId, title, body, dateTime)
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_BODY = "body"
        const val EXTRA_DATE_TIME = "date_time"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }
}
