package com.example.calendartodo.ui.daydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.calendar.DayEvent
import com.example.calendartodo.ui.components.JarProgressCard
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.SweetFab
import com.example.calendartodo.ui.components.SweetIconButton
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
fun DayDetailScreen(
    date: JalaliDate,
    tasks: List<TaskEntity>,
    events: List<DayEvent>,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    onAddTask: () -> Unit,
    onHolidayClick: (DayEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]
    val gregorian = remember(date) { date.toGregorianCalendar() }
    val gregMonth = DateFormatSymbols(Locale.ENGLISH).months[gregorian.get(java.util.Calendar.MONTH)]
    val gregDay = gregorian.get(java.util.Calendar.DAY_OF_MONTH)
    val done = tasks.count { it.isDone }
    val holidays = events.filter { it.isHoliday && it.description.isNotBlank() }
    val occasions = events.filter { !it.isHoliday && it.description.isNotBlank() }

    Box(modifier = modifier.fillMaxSize().background(colors.cream)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SweetIconButton(label = "←", onClick = onBack)
                Column(modifier = Modifier.padding(start = mockupDp(12))) {
                    Text("$month ${date.day}", style = MaterialTheme.typography.titleMedium, color = colors.ink)
                    Text("$weekday · $gregMonth $gregDay", style = MaterialTheme.typography.bodySmall, color = colors.muted)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp)
            ) {
                item {
                    JarProgressCard(
                        label = "Day's jar",
                        completed = done,
                        total = tasks.size.coerceAtLeast(done)
                    )
                }
                if (tasks.isNotEmpty()) {
                    item { SweetSectionLabel("TASKS") }
                    items(tasks, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
                if (occasions.isNotEmpty()) {
                    item { SweetSectionLabel("OCCASIONS") }
                    items(occasions.size) { index ->
                        val event = occasions[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.paper)
                                .clickable { onHolidayClick(event) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PeppermintCandyIcon(size = 16.dp)
                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(event.description, style = MaterialTheme.typography.titleSmall, color = colors.ink)
                                Text(
                                    "Calendar occasion · time.ir",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted
                                )
                            }
                        }
                    }
                }
                if (holidays.isNotEmpty()) {
                    item { SweetSectionLabel("HOLIDAY") }
                    items(holidays.size) { index ->
                        val event = holidays[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.paper)
                                .clickable { onHolidayClick(event) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PeppermintCandyIcon(size = 16.dp)
                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(event.description, style = MaterialTheme.typography.titleSmall, color = colors.ink)
                                Text(
                                    "Official occasion · time.ir",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted
                                )
                            }
                        }
                    }
                }
                if (tasks.isEmpty() && holidays.isEmpty() && occasions.isEmpty()) {
                    item {
                        Text(
                            "Nothing scheduled for this day",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.muted,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }

        SweetFab(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        )
    }
}
