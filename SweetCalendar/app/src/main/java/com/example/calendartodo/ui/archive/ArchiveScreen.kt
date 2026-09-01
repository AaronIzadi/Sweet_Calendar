package com.example.calendartodo.ui.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.stats.ArchiveFilter
import com.example.calendartodo.ui.stats.computeMonthCompletionPercent
import com.example.calendartodo.ui.stats.filterCompletedTasks
import com.example.calendartodo.ui.stats.groupTasksByDay
import com.example.calendartodo.ui.today.computeStreak
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun ArchiveScreen(
    tasks: List<TaskEntity>,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    var filter by remember { mutableStateOf(ArchiveFilter.ThisWeek) }
    val completed = remember(tasks, filter) { filterCompletedTasks(tasks, filter) }
    val grouped = remember(completed) { groupTasksByDay(completed) }
    val totalDone = tasks.count { it.isDone }
    val streak = remember(tasks) { computeStreak(tasks) }
    val monthPct = remember(tasks) { computeMonthCompletionPercent(tasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "←",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.purpleDeep,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(end = 12.dp)
            )
            Text("Completed", style = MaterialTheme.typography.titleMedium, color = colors.ink)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("$totalDone", "TOTAL DONE", modifier = Modifier.weight(1f))
            StatCard("$streak", "DAY STREAK", modifier = Modifier.weight(1f))
            StatCard("$monthPct%", "THIS MONTH", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArchiveFilter.entries.forEach { f ->
                val active = f == filter
                Text(
                    f.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (active) Color.White else colors.muted,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (active) colors.purple else colors.paper)
                        .clickable { filter = f }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(horizontal = 18.dp)) {
            if (grouped.isEmpty()) {
                item {
                    Text(
                        "No completed tasks in this period",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                grouped.forEach { (label, dayTasks) ->
                    item {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.muted,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(dayTasks, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.paper)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = colors.purpleDeep)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.muted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
