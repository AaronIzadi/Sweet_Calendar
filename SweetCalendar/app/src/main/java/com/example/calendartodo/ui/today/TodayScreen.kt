package com.example.calendartodo.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.IceCreamIcon
import com.example.calendartodo.ui.components.JarProgressCard
import com.example.calendartodo.ui.components.StreakPill
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.formatDisplayWithWeekday
import com.example.calendartodo.ui.theme.SweetTheme
import java.util.Calendar

private fun timeOfDayGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}

fun computeStreak(tasks: List<TaskEntity>): Int {
    val today = JalaliDate.today()
    var streak = 0
    var day = today
    while (true) {
        val iso = day.formatIso()
        val dayTasks = tasks.filter { it.jalaliDate == iso }
        if (dayTasks.isEmpty()) break
        if (dayTasks.all { it.isDone }) {
            streak++
            day = day.minusDays(1)
        } else if (day == today) {
            break
        } else {
            break
        }
    }
    return streak
}

@Composable
fun TodayScreen(
    tasks: List<TaskEntity>,
    userName: String,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onOpenArchive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val today = remember { JalaliDate.today() }
    val todayIso = today.formatIso()
    val todayTasks = remember(tasks) { tasks.filter { it.jalaliDate == todayIso } }
    val pending = todayTasks.filter { !it.isDone }
    val done = todayTasks.filter { it.isDone }
    val streak = remember(tasks) { computeStreak(tasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp)
        ) {
            item {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${timeOfDayGreeting()}, $userName",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.ink
                        )
                        Text(
                            today.formatDisplayWithWeekday(),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.muted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (streak > 0) {
                        StreakPill(streak, modifier = Modifier.clickable(onClick = onOpenStats))
                    }
                    Text(
                        "⌕",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.purpleDeep,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable(onClick = onOpenSearch)
                    )
                }
                JarProgressCard(
                    label = "Today's jar",
                    completed = done.size,
                    total = todayTasks.size.coerceAtLeast(done.size + pending.size),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (todayTasks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IceCreamIcon(size = 120.dp)
                        Spacer(Modifier.height(18.dp))
                        Text(
                            "Nothing on the menu today",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.ink
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Your jar is empty — add a task and watch it fill up with candy as you check things off.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.muted,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }
            } else {
                if (pending.isNotEmpty()) {
                    item { SweetSectionLabel("TODAY") }
                    items(pending, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onEditTask(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
                if (done.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .clickable(onClick = onOpenArchive),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${done.size} completed today",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.muted
                            )
                        }
                    }
                    items(done, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onEditTask(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}
