package com.example.calendartodo.ui.today

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CheckCandyIcon
import com.example.calendartodo.ui.components.EmptyStateContent
import com.example.calendartodo.ui.components.JarProgressCard
import com.example.calendartodo.ui.components.StreakPill
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.components.formatDisplayWithWeekday
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import com.example.calendartodo.ui.theme.MintGreen
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
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onEditTask: (TaskEntity) -> Unit,
    onAddTask: () -> Unit = {},
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${timeOfDayGreeting()}, $userName",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(MockupDimens.GREET_TITLE),
                            lineHeight = mockupSp(22f)
                        ),
                        color = colors.ink
                    )
                    Text(
                        today.formatDisplayWithWeekday(calendarSystem),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.GREET_DATE_F),
                            lineHeight = mockupSp(16f)
                        ),
                        color = colors.muted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                StreakPill(
                    streak = streak,
                    modifier = Modifier.clickable(onClick = onOpenStats)
                )
            }

            JarProgressCard(
                label = "Today's jar",
                completed = done.size,
                total = todayTasks.size,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp)
        ) {
            if (todayTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, top = 24.dp, bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateContent(onAddTask = onAddTask)
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
                            style = TaskCardStyle.Mockup
                        )
                    }
                }
                if (done.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(top = 6.dp, bottom = 6.dp)
                                .clickable(onClick = onOpenArchive),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CheckCandyIcon(
                                size = mockupDp(MockupDimens.COMPLETED_ROW_ICON),
                                bgColor = if (colors.isDark) colors.mint else MintGreen
                            )
                            Text(
                                "${done.size} completed today",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(MockupDimens.COMPLETED_LABEL_F)
                                ),
                                color = colors.muted
                            )
                        }
                    }
                    items(done, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onEditTask(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            style = TaskCardStyle.Mockup
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(90.dp)) }
        }
    }
}
