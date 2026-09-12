package com.sweetcalendar

import android.app.Application
import com.sweetcalendar.data.local.AppDatabase
import com.sweetcalendar.data.remote.NetworkModule
import com.sweetcalendar.export.TaskExportRepository
import com.sweetcalendar.reminder.NotificationHelper
import com.sweetcalendar.repository.EventRepository
import com.sweetcalendar.repository.TaskRepository

class CalendarTodoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepository(AppDatabase.get(this).taskDao())
    }

    val eventRepository: EventRepository by lazy {
        val db = AppDatabase.get(this)
        EventRepository(db.eventCacheDao(), db.eventMonthCacheDao(), NetworkModule.timeIrCalendarClient)
    }

    val taskExportRepository: TaskExportRepository by lazy {
        TaskExportRepository(this, AppDatabase.get(this).taskDao())
    }
}
