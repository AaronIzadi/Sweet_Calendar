package com.example.calendartodo.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CandySprinklesBackground
import com.example.calendartodo.ui.components.GummyIcon
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.PixelFab
import com.example.calendartodo.ui.components.PixelPanel
import com.example.calendartodo.ui.components.TaskCard
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
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(CreamFrosting)) {
        CandySprinklesBackground(modifier = Modifier.fillMaxSize())

        Column(modifier = Modifier.fillMaxSize()) {
            CalendarTopBar(onBack = onBack, onToday = viewModel::goToToday)

            MonthHeader(
                month = state.visibleMonth,
                isLoading = state.isLoadingEvents,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth
            )

            PixelPanel(
                modifier = Modifier
                    .weight(1f)
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
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(horizontal = 8.dp),
                state = state,
                onEdit = onEditTask,
                onDelete = onDeleteTask,
                onComplete = onCompleteTask
            )
        }

        PixelFab(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            backgroundColor = BubblegumPink
        ) {
            WrappedCandyIcon(size = 28.dp)
        }
    }
}

@Composable
private fun CalendarTopBar(onBack: () -> Unit, onToday: () -> Unit) {
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
            PixelButton(onClick = onBack, backgroundColor = LemonYellow) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to tasks",
                    tint = ChocolateBrown,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                "See all tasks",
                style = MaterialTheme.typography.titleSmall,
                color = CreamFrosting
            )
            PixelButton(onClick = onToday, backgroundColor = LemonYellow) {
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
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellSize = maxWidth / 7
            val weeks = cells.chunked(7)
            Column(modifier = Modifier.fillMaxWidth()) {
                weeks.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { cell ->
                            DayCell(
                                cell = cell,
                                isSelected = cell?.date == selectedDate,
                                onClick = onDayClick,
                                modifier = Modifier.size(cellSize)
                            )
                        }
                        repeat(7 - week.size) {
                            Spacer(Modifier.size(cellSize))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    cell: DayCellInfo?,
    isSelected: Boolean,
    onClick: (JalaliDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
    onEdit: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    onComplete: (TaskEntity) -> Unit
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
            items(state.selectedDayTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onEdit = { onEdit(task) },
                    onComplete = { onComplete(task) },
                    onDelete = { onDelete(task) },
                    modifier = Modifier.height(120.dp)
                )
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}
