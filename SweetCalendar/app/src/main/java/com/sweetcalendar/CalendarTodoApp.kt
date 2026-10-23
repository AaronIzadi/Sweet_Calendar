package com.sweetcalendar

import android.app.Application
import com.sweetcalendar.data.local.AppDatabase
import com.sweetcalendar.data.remote.NetworkModule
import com.sweetcalendar.export.TaskExportRepository
import com.sweetcalendar.reminder.NotificationHelper
import com.sweetcalendar.repository.EventRepository
import com.sweetcalendar.repository.TaskRepository
import com.sweetcalendar.widget.WidgetPreviews
import java.util.Locale

class CalendarTodoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
        WidgetPreviews.sync(this)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepository(AppDatabase.get(this).taskDao())
    }

    val eventRepository: EventRepository by lazy {
        val db = AppDatabase.get(this)
        EventRepository(
            dao = db.eventCacheDao(),
            monthCacheDao = db.eventMonthCacheDao(),
            timeIrClient = NetworkModule.timeIrCalendarClient,
            nagerClient = NetworkModule.nagerHolidaysClient,
            holidayCountryCode = {
                Locale.getDefault().country.takeIf { it.length == 2 } ?: "US"
            },
        )
    }

    val taskExportRepository: TaskExportRepository by lazy {
        TaskExportRepository(this, AppDatabase.get(this).taskDao())
    }
}
