package com.example.calendartodo.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.theme.SweetTheme
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
fun MonthScreen(
    viewModel: CalendarViewModel,
    onDayClick: (JalaliDate) -> Unit,
    onOfflineClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = SweetTheme.colors
    val today = JalaliDate.today()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
    ) {
        if (state.eventsLoadFailed && state.showHolidays) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFFF1DC))
                    .padding(10.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOfflineClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "Couldn't load holidays",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.chocDeep
                    )
                }
                Text(
                    "Retry",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.purpleDeep,
                    modifier = Modifier.clickable { viewModel.retryEventsLoad() }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonthChevron("‹", onClick = viewModel::goToPreviousMonth)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${JalaliDate.MONTH_NAMES_EN[state.visibleMonth.month - 1]} ${state.visibleMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.ink
                )
                val gregorianMonth = state.visibleMonth.toGregorianCalendar()
                val monthName = DateFormatSymbols(Locale.ENGLISH).months[gregorianMonth.get(java.util.Calendar.MONTH)]
                Text(
                    "$monthName ${gregorianMonth.get(java.util.Calendar.YEAR)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.85f
                    ),
                    color = colors.muted
                )
                if (state.selectedDate != today || state.visibleMonth.year != today.year || state.visibleMonth.month != today.month) {
                    Text(
                        "Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.pinkDeep,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.paper)
                            .clickable { viewModel.goToToday() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (state.isLoadingEvents) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp).padding(top = 4.dp),
                        strokeWidth = 2.dp,
                        color = colors.pink
                    )
                }
            }
            MonthChevron("›", onClick = viewModel::goToNextMonth)
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
            JalaliDate.weekdayLabels(state.weekStartsOn).forEach { name ->
                Text(
                    name.uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted
                )
            }
        }

        val weeks = state.monthCells.chunked(7)
        Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    week.forEachIndexed { colIndex, cell ->
                        MonthDayCell(
                            cell = cell,
                            colIndex = colIndex,
                            isSelected = cell?.date == state.selectedDate,
                            isToday = cell?.date == today,
                            modifier = Modifier.weight(1f),
                            onClick = { date ->
                                viewModel.selectDate(date)
                                onDayClick(date)
                            }
                        )
                    }
                    repeat(7 - week.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LegendItem(color = colors.pinkDeep, shape = DotShape.Circle, label = "Personal")
            LegendItem(color = colors.purpleDeep, shape = DotShape.Diamond, label = "Work")
            LegendItem(color = colors.mintDeep, shape = DotShape.Leaf, label = "Home")
            if (state.showHolidays) {
                LegendItem(color = colors.lemonDeep, shape = DotShape.Square, label = "Occasion")
                LegendItem(color = colors.mintDeep, shape = DotShape.Ring, label = "Holiday")
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

private enum class DotShape { Circle, Diamond, Leaf, Ring, Square }

@Composable
private fun MonthChevron(label: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.paper)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = colors.purpleDeep)
    }
}

@Composable
private fun MonthDayCell(
    cell: DayCellInfo?,
    colIndex: Int,
    isSelected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: (JalaliDate) -> Unit
) {
    val colors = SweetTheme.colors
    if (cell == null) {
        Box(modifier.aspectRatio(1f))
        return
    }
    val isFriday = cell.date.weekdayIndex() == 6
    val bg = when {
        isToday -> colors.pink
        cell.isHoliday -> colors.holidayBg
        isFriday -> colors.weekendBg
        else -> colors.paper
    }
    val textColor = when {
        isToday -> Color.White
        else -> colors.ink
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .then(if (isToday) Modifier else Modifier)
            .clickable { onClick(cell.date) },
        contentAlignment = Alignment.Center
    ) {
        Text(cell.date.day.toString(), style = MaterialTheme.typography.bodySmall, color = textColor)
        val hasMarkers = cell.hasTask || cell.isHoliday || cell.hasOccasion
        if (hasMarkers) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                cell.taskCategories.forEach { category ->
                    val (color, shape) = categoryDotStyleComposable(category)
                    CategoryDot(color, shape, isToday)
                }
                if (cell.hasOccasion) {
                    CategoryDot(colors.lemonDeep, DotShape.Square, isToday)
                }
                if (cell.isHoliday) {
                    CategoryDot(colors.mintDeep, DotShape.Ring, isToday)
                }
            }
        }
    }
}

@Composable
private fun categoryDotStyleComposable(category: TaskCategory): Pair<Color, DotShape> {
    val colors = SweetTheme.colors
    return when (category) {
        TaskCategory.Personal -> colors.pinkDeep to DotShape.Circle
        TaskCategory.Work -> colors.purpleDeep to DotShape.Diamond
        TaskCategory.Home -> colors.mintDeep to DotShape.Leaf
    }
}

@Composable
private fun CategoryDot(color: Color, shape: DotShape, onToday: Boolean) {
    val dotColor = if (onToday) Color.White.copy(alpha = 0.9f) else color
    when (shape) {
        DotShape.Circle -> Box(Modifier.size(4.dp).clip(CircleShape).background(dotColor))
        DotShape.Diamond -> Box(Modifier.size(4.dp).clip(RoundedCornerShape(1.dp)).background(dotColor))
        DotShape.Leaf -> Box(Modifier.size(4.dp).clip(RoundedCornerShape(topStartPercent = 0, topEndPercent = 50, bottomEndPercent = 0, bottomStartPercent = 50)).background(dotColor))
        DotShape.Square -> Box(Modifier.size(4.dp).clip(RoundedCornerShape(1.dp)).background(dotColor))
        DotShape.Ring -> Box(Modifier.size(4.dp).border(1.5.dp, dotColor, CircleShape))
    }
}

@Composable
private fun LegendItem(color: Color, shape: DotShape, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(Modifier.size(9.dp)) {
            CategoryDot(color, shape, false)
        }
        Text(label, style = MaterialTheme.typography.bodySmall, color = SweetTheme.colors.muted)
    }
}

/** Kept for day-detail navigation from month grid */
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit
) {
    MonthScreen(viewModel = viewModel, onDayClick = { })
}
