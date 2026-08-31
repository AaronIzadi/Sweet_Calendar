package com.example.calendartodo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.calendartodo.ui.MainScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel
import com.example.calendartodo.ui.theme.CalendarTodoTheme

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            CalendarTodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.calendartodo.ui.theme.CreamFrosting
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
