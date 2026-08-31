package com.example.calendartodo.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.CandySprinklesBackground
import com.example.calendartodo.ui.components.EmptyTasksState
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.TaskCard
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting

@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onOpenCalendar: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamFrosting)
    ) {
        CandySprinklesBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Hello",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BubblegumPink
                    )
                    Text(
                        "All tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BubblegumPink
                    )
                }
                PixelButton(onClick = onOpenCalendar, backgroundColor = CreamFrosting) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Open calendar",
                        tint = ChocolateBrown,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (tasks.isEmpty()) {
                    EmptyTasksState(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onEdit = { onEditTask(task) },
                                onComplete = { onCompleteTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                        item { Spacer(Modifier.height(88.dp)) }
                    }
                }
            }

            PixelButton(
                onClick = onAddTask,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                backgroundColor = BubblegumPink
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = CreamFrosting,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        "Add Task",
                        style = MaterialTheme.typography.titleSmall,
                        color = CreamFrosting
                    )
                }
            }
        }
    }
}
