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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.addtask.AddTaskDialog
import com.example.calendartodo.ui.components.CandySprinklesBackground
import com.example.calendartodo.ui.components.GummyIcon
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.PixelFab
import com.example.calendartodo.ui.components.PixelPanel
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.components.pixelCell
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.CaramelOrange
import com.example.calendartodo.ui.theme.CherryRed
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CottonCandyPink
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.SkyBlue

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(CreamFrosting)) {
        CandySprinklesBackground(modifier = Modifier.fillMaxSize())

        Column(modifier = Modifier.fillMaxSize()) {
            CandyTopBar(onToday = viewModel::goToToday)

            MonthHeader(
                month = state.visibleMonth,
                isLoading = state.isLoadingEvents,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth
            )

            PixelPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                backgroundColor = CottonCandyPink.copy(alpha = 0.85f)
            ) {
                MonthGrid(
                    cells = state.monthCells,
                    selectedDate = state.selectedDate,
                    onDayClick = viewModel::selectDate
                )
            }

            Spacer(Modifier.height(8.dp))

            SelectedDayPanel(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                state = state,
                onToggleDone = viewModel::toggleTaskDone,
                onDelete = viewModel::deleteTask
            )
        }

        PixelFab(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            backgroundColor = BubblegumPink
        ) {
            LollipopIcon(size = 28.dp)
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
private fun CandyTopBar(onToday: () -> Unit) {
    PixelPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        backgroundColor = BubblegumPink
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LollipopIcon(size = 28.dp)
                Spacer(Modifier.size(8.dp))
                Column {
                    Text(
                        "Sweet Calendar",
                        style = MaterialTheme.typography.titleMedium,
                        color = CreamFrosting
                    )
                    Text(
                        "تقویم قندی",
                        style = MaterialTheme.typography.labelSmall,
                        color = CreamFrosting.copy(alpha = 0.9f)
                    )
                }
            }
            PixelButton(
                onClick = onToday,
                backgroundColor = LemonYellow
            ) {
                Icon(
                    Icons.Default.Today,
                    contentDescription = "Today",
                    tint = ChocolateBrown,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
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
        PixelButton(onClick = onPrevious, backgroundColor = GrapePurple) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Previous month",
                tint = CreamFrosting,
                modifier = Modifier.size(18.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            WrappedCandyIcon(size = 18.dp)
            Spacer(Modifier.size(6.dp))
            Text(
                "${JalaliDate.MONTH_NAMES[month.month - 1]} ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                color = ChocolateBrown
            )
            if (isLoading) {
                Spacer(Modifier.size(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = BubblegumPink
                )
            }
        }
        PixelButton(onClick = onNext, backgroundColor = GrapePurple) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Next month",
                tint = CreamFrosting,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun MonthGrid(
    cells: List<DayCellInfo?>,
    selectedDate: JalaliDate,
    onDayClick: (JalaliDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            JalaliDate.WEEKDAY_NAMES_SHORT.forEach { name ->
                Text(
                    name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(((cells.size / 7 + 1) * 44).dp),
            userScrollEnabled = false
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
            .padding(1.dp)
            .pixelCell(
                selected = isSelected,
                selectedColor = BubblegumPink,
                defaultColor = CreamFrosting.copy(alpha = 0.7f)
            )
            .then(if (cell != null) Modifier.clickable { onClick(cell.date) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (cell != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = cell.date.day.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isSelected -> CreamFrosting
                        cell.isHoliday -> CherryRed
                        else -> ChocolateBrown
                    },
                    textAlign = TextAlign.Center
                )
                Row {
                    if (cell.hasEvent) CandyDot(color = CaramelOrange, selected = isSelected)
                    if (cell.hasTask) CandyDot(color = MintGreen, selected = isSelected)
                }
            }
        }
    }
}

@Composable
private fun CandyDot(color: Color, selected: Boolean) {
    GummyIcon(
        modifier = Modifier.padding(1.dp),
        size = 8.dp,
        color = if (selected) CreamFrosting else color
    )
}

@Composable
private fun SelectedDayPanel(
    modifier: Modifier = Modifier,
    state: CalendarUiState,
    onToggleDone: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val d = state.selectedDate
    PixelPanel(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = SkyBlue.copy(alpha = 0.75f)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WrappedCandyIcon(size = 16.dp)
                    Spacer(Modifier.size(6.dp))
                    Text(
                        "${d.day} ${JalaliDate.MONTH_NAMES[d.month - 1]} ${d.year}",
                        style = MaterialTheme.typography.titleSmall,
                        color = ChocolateBrown,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            if (state.selectedDayEvents.isEmpty() && state.selectedDayTasks.isEmpty()) {
                item {
                    Text(
                        "No sweets on this day yet!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
            items(state.selectedDayEvents) { event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            if (event.isHoliday) CherryRed.copy(alpha = 0.2f) else LemonYellow.copy(alpha = 0.35f)
                        )
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GummyIcon(
                        size = 12.dp,
                        color = if (event.isHoliday) CherryRed else CaramelOrange
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(event.description, style = MaterialTheme.typography.bodySmall, color = ChocolateBrown)
                }
            }
            items(state.selectedDayTasks) { task ->
                TaskRow(task = task, onToggleDone = onToggleDone, onDelete = onDelete)
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    onToggleDone: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(MintGreen.copy(alpha = if (task.isDone) 0.15f else 0.35f))
            .clickable { onToggleDone(task) }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(if (task.isDone) BubblegumPink else CreamFrosting)
                .clickable { onToggleDone(task) },
            contentAlignment = Alignment.Center
        ) {
            if (task.isDone) {
                Text("✓", style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
            }
        }
        Spacer(Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodySmall,
                color = ChocolateBrown,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )
            if (task.notes.isNotBlank()) {
                Text(
                    task.notes,
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown.copy(alpha = 0.7f)
                )
            }
        }
        PixelButton(onClick = { onDelete(task) }, backgroundColor = CherryRed.copy(alpha = 0.8f)) {
            Text("X", style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
        }
    }
}
