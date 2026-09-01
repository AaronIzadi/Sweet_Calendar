package com.example.calendartodo.ui.week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun WeekScreen(
    tasks: List<TaskEntity>,
    weekStartsOn: Int = 0,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val today = remember { JalaliDate.today() }
    val weekDays = remember(weekStartsOn) { JalaliDate.weekContaining(today, weekStartsOn) }
    var selectedDay by remember { mutableStateOf(today) }

    val weekStart = weekDays.first()
    val weekEnd = weekDays.last()
    val tasksByDay = remember(tasks, weekDays) {
        weekDays.associateWith { day ->
            tasks.filter { it.jalaliDate == day.formatIso() }
                .sortedWith(compareBy({ it.isDone }, { it.reminderTime.orEmpty() }))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text("This week", style = MaterialTheme.typography.titleMedium, color = colors.ink)
            Text(
                JalaliDate.weekRange(weekStart, weekEnd),
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weekDays.forEach { day ->
                val dayTasks = tasksByDay[day].orEmpty()
                val pendingCount = dayTasks.count { !it.isDone }
                val selected = day == selectedDay
                DayChip(
                    day = day,
                    pendingCount = pendingCount,
                    selected = selected,
                    onClick = { selectedDay = day }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp)
        ) {
            val dayTasks = tasksByDay[selectedDay].orEmpty()
            val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[selectedDay.weekdayIndex()].uppercase()
            val month = JalaliDate.MONTH_NAMES_EN[selectedDay.month - 1].uppercase()
            item {
                SweetSectionLabel("$weekday · $month ${selectedDay.day}")
            }
            if (dayTasks.isEmpty()) {
                item {
                    Text(
                        "No tasks this day",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(dayTasks, key = { it.id }) { task ->
                    SweetTaskCard(
                        task = task,
                        onClick = { onEditTask(task) },
                        onToggleComplete = { onCompleteTask(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun DayChip(
    day: JalaliDate,
    pendingCount: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors
    val dow = JalaliDate.WEEKDAY_NAMES_EN_SHORT[day.weekdayIndex()].uppercase().take(3)
    Column(
        modifier = Modifier
            .size(width = 44.dp, height = 72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.purple else colors.paper)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            dow,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color(0xFFEFE6FA) else colors.muted
        )
        Text(
            day.day.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) Color.White else colors.ink
        )
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { i ->
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            if (i < pendingCount.coerceAtMost(3)) {
                                if (selected) Color.White else colors.pinkDeep
                            } else colors.line
                        )
                )
            }
        }
    }
}
