package com.example.calendartodo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.calendartodo.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = AppDatabase.get(context).taskDao().getActiveReminders()
                val scheduler = TaskReminderScheduler(context.applicationContext)
                tasks.forEach { scheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
