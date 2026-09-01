package com.example.calendartodo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.calendartodo.data.prefs.AppPreferences
import com.example.calendartodo.ui.MainScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels {
        val app = application as CalendarTodoApp
        CalendarViewModel.Factory(application, app.taskRepository, app.eventRepository)
    }

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* optional; reminders still schedule without it on older APIs */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = AppPreferences(this)
        setContent {
            var darkMode by remember { mutableStateOf(preferences.darkMode) }
            MainScreen(
                viewModel = viewModel,
                preferences = preferences,
                darkMode = darkMode,
                onDarkModeChange = { darkMode = it },
                onRequestNotificationPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }
    }
}
