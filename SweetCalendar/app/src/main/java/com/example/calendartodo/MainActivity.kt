package com.example.calendartodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.calendartodo.ui.calendar.CalendarScreen
import com.example.calendartodo.ui.calendar.CalendarViewModel
import com.example.calendartodo.ui.theme.CalendarTodoTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels {
        val app = application as CalendarTodoApp
        CalendarViewModel.Factory(app.taskRepository, app.eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarTodoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalendarScreen(viewModel = viewModel)
                }
            }
        }
    }
}
