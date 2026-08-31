package com.example.calendartodo.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CandySprinklesBackground
import com.example.calendartodo.ui.components.EmptyTasksState
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.TaskCard
import com.example.calendartodo.ui.components.pixelBorder
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import java.util.Calendar

enum class TaskFilter(val label: String) {
    All("All"),
    Today("Today"),
    Pending("Pending"),
    Done("Done")
}

fun sortTasks(tasks: List<TaskEntity>): List<TaskEntity> =
    tasks.sortedWith(
        compareBy<TaskEntity> { it.isDone }
            .thenBy { it.jalaliDate }
            .thenBy { it.reminderTime.orEmpty() }
            .thenBy { it.createdAt }
    )

fun filterTasks(tasks: List<TaskEntity>, filter: TaskFilter, query: String): List<TaskEntity> {
    val today = JalaliDate.today().formatIso()
    val filtered = when (filter) {
        TaskFilter.All -> tasks
        TaskFilter.Today -> tasks.filter { it.jalaliDate == today }
        TaskFilter.Pending -> tasks.filter { !it.isDone }
        TaskFilter.Done -> tasks.filter { it.isDone }
    }
    if (query.isBlank()) return sortTasks(filtered)
    val q = query.trim().lowercase()
    return sortTasks(
        filtered.filter {
            it.title.lowercase().contains(q) ||
                it.notes.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
        }
    )
}

private fun timeOfDayGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}

@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onOpenCalendar: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit
) {
    var filter by remember { mutableStateOf(TaskFilter.All) }
    var searchQuery by remember { mutableStateOf("") }
    val visibleTasks = remember(tasks, filter, searchQuery) {
        filterTasks(tasks, filter, searchQuery)
    }
    val today = remember { JalaliDate.today() }
    val pendingCount = remember(tasks) { tasks.count { !it.isDone } }

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
                        timeOfDayGreeting(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = BubblegumPink
                    )
                    Text(
                        "Your tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BubblegumPink
                    )
                    Text(
                        "${today.day} ${JalaliDate.MONTH_NAMES[today.month - 1]} ${today.year}" +
                            if (pendingCount > 0) " · $pendingCount pending" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.7f)
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

            Spacer(Modifier.height(12.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(Modifier.height(10.dp))

            FilterChips(
                selected = filter,
                onSelect = { filter = it }
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    tasks.isEmpty() -> EmptyTasksState(modifier = Modifier.align(Alignment.Center))
                    visibleTasks.isEmpty() -> NoResultsState(
                        modifier = Modifier.align(Alignment.Center),
                        hasSearch = searchQuery.isNotBlank()
                    )
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(visibleTasks, key = { it.id }) { task ->
                                TaskCard(
                                    task = task,
                                    onClick = { onEditTask(task) },
                                    onEdit = { onEditTask(task) },
                                    onComplete = { onCompleteTask(task) },
                                    onDelete = { onDeleteTask(task) }
                                )
                            }
                            item { Spacer(Modifier.height(88.dp)) }
                        }
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
                        contentDescription = "Add task",
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

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pixelBorder(borderWidth = 2.dp, shadowOffset = 2.dp)
            .background(CreamFrosting)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            tint = ChocolateBrown.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(8.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = ChocolateBrown),
            cursorBrush = SolidColor(BubblegumPink),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        "Search tasks…",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.4f)
                    )
                }
                inner()
            }
        )
    }
}

@Composable
private fun FilterChips(selected: TaskFilter, onSelect: (TaskFilter) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TaskFilter.entries) { filter ->
            val isSelected = filter == selected
            PixelButton(
                onClick = { onSelect(filter) },
                backgroundColor = when {
                    isSelected -> BubblegumPink
                    filter == TaskFilter.Done -> MintGreen.copy(alpha = 0.6f)
                    filter == TaskFilter.Pending -> LemonYellow
                    else -> CreamFrosting
                }
            ) {
                Text(
                    filter.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) CreamFrosting else ChocolateBrown
                )
            }
        }
    }
}

@Composable
private fun NoResultsState(modifier: Modifier = Modifier, hasSearch: Boolean) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (hasSearch) "No matching tasks" else "Nothing here yet",
            style = MaterialTheme.typography.titleSmall,
            color = ChocolateBrown
        )
        Spacer(Modifier.height(4.dp))
        Text(
            if (hasSearch) "Try a different search or filter" else "Try another filter tab",
            style = MaterialTheme.typography.bodySmall,
            color = ChocolateBrown.copy(alpha = 0.7f),
            textDecoration = TextDecoration.None
        )
    }
}
