package com.example.calendartodo.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.addtask.AddTaskDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تقویم و کارها") },
                actions = {
                    IconButton(onClick = viewModel::goToToday) {
                        Icon(Icons.Default.Today, contentDescription = "Today")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MonthHeader(
                month = state.visibleMonth,
                isLoading = state.isLoadingEvents,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth
            )
            MonthGrid(
                cells = state.monthCells,
                selectedDate = state.selectedDate,
                onDayClick = viewModel::selectDate
            )
            Spacer(Modifier.height(8.dp))
            SelectedDayPanel(
                state = state,
                onToggleDone = viewModel::toggleTaskDone,
                onDelete = viewModel::deleteTask
            )
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            dayLabel = "${state.selectedDate.day} ${JalaliDate.MONTH_NAMES[state.selectedDate.month - 1]}",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, notes ->
                viewModel.addTask(title, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MonthHeader(
    month: JalaliDate,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Previous month")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${JalaliDate.MONTH_NAMES[month.month - 1]} ${month.year}",
                style = MaterialTheme.typography.titleMedium
            )
            if (isLoading) {
                Spacer(Modifier.size(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Next month")
        }
    }
}

@Composable
private fun MonthGrid(
    cells: List<DayCellInfo?>,
    selectedDate: JalaliDate,
    onDayClick: (JalaliDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            JalaliDate.WEEKDAY_NAMES_SHORT.forEach { name ->
                Text(
                    name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(((cells.size / 7 + 1) * 48).dp)
        ) {
            items(cells) { cell ->
                DayCell(cell = cell, isSelected = cell?.date == selectedDate, onClick = onDayClick)
            }
        }
    }
}

@Composable
private fun DayCell(
    cell: DayCellInfo?,
    isSelected: Boolean,
    onClick: (JalaliDate) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else Modifier
            )
            .then(
                if (cell != null) Modifier.clickable { onClick(cell.date) } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = cell.date.day.toString(),
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        cell.isHoliday -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = if (cell.isHoliday) FontWeight.Bold else FontWeight.Normal
                )
                Row {
                    if (cell.hasEvent) Dot(color = MaterialTheme.colorScheme.secondary, selected = isSelected)
                    if (cell.hasTask) Dot(color = MaterialTheme.colorScheme.tertiary, selected = isSelected)
                }
            }
        }
    }
}

@Composable
private fun Dot(color: Color, selected: Boolean) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(5.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.onPrimary else color)
    )
}

@Composable
private fun SelectedDayPanel(
    state: CalendarUiState,
    onToggleDone: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val d = state.selectedDate
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text(
                "${d.day} ${JalaliDate.MONTH_NAMES[d.month - 1]} ${d.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        if (state.selectedDayEvents.isEmpty() && state.selectedDayTasks.isEmpty()) {
            item {
                Text(
                    "No events or tasks for this day.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
        items(state.selectedDayEvents) { event ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (event.isHoliday) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                )
                Spacer(Modifier.size(8.dp))
                Text(event.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(state.selectedDayTasks) { task ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = task.isDone, onCheckedChange = { onToggleDone(task) })
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                    )
                    if (task.notes.isNotBlank()) {
                        Text(
                            task.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { onDelete(task) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete task")
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
