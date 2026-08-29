package com.example.calendartodo

import android.app.Application
import com.example.calendartodo.data.local.AppDatabase
import com.example.calendartodo.data.remote.NetworkModule
import com.example.calendartodo.repository.EventRepository
import com.example.calendartodo.repository.TaskRepository

class CalendarTodoApp : Application() {

    val taskRepository: TaskRepository by lazy {
        TaskRepository(AppDatabase.get(this).taskDao())
    }

    val eventRepository: EventRepository by lazy {
        EventRepository(AppDatabase.get(this).eventCacheDao(), NetworkModule.holidayApi)
    }
}
