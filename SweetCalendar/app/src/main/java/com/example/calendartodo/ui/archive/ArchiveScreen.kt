package com.example.calendartodo.ui.archive

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.stats.ArchiveFilter
import com.example.calendartodo.ui.stats.computeMonthCompletionPercent
import com.example.calendartodo.ui.stats.filterCompletedTasks
import com.example.calendartodo.ui.stats.groupTasksByDay
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import com.example.calendartodo.ui.today.computeStreak

private val ArchiveStatLabelColor = Color(0xFF9A8878)
private val ArchiveDayLabelColor = Color(0xFFB39D89)

@Composable
fun ArchiveScreen(
    tasks: List<TaskEntity>,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
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

    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Text(
            "Completed",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.ARCHIVE_TITLE),
                lineHeight = mockupSp(20f)
            ),
            color = colors.ink,
            modifier = Modifier.padding(
                start = mockupDp(18),
                end = mockupDp(18),
                top = mockupDp(16),
                bottom = mockupDp(4)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(14),
                    bottom = mockupDp(4)
                ),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
        ) {
            ArchiveStatCard("$totalDone", "TOTAL DONE", modifier = Modifier.weight(1f))
            ArchiveStatCard("$streak", "DAY STREAK", modifier = Modifier.weight(1f))
            ArchiveStatCard("$monthPct%", "THIS MONTH", modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(14),
                    bottom = mockupDp(4)
                ),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(8))
        ) {
            ArchiveFilter.entries.forEach { option ->
                ArchiveFilterChip(
                    label = option.label,
                    selected = option == filter,
                    onClick = { filter = option }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = mockupDp(18))
        ) {
            if (grouped.isEmpty()) {
                item {
                    Text(
                        "No completed tasks in this period",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(12f)
                        ),
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = mockupDp(24))
                    )
                }
            } else {
                grouped.forEach { (label, dayTasks) ->
                    item {
                        ArchiveDayLabel(label)
                    }
                    items(
                        dayTasks.sortedBy { it.reminderTime.orEmpty() },
                        key = { it.id }
                    ) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onTaskClick(task) },
                            style = TaskCardStyle.Mockup
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(mockupDp(24))) }
        }
    }
}

@Composable
private fun ArchiveStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.ARCHIVE_STAT_RADIUS))

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.ARCHIVE_STAT_SHADOW))
                .clip(shape)
                .background(colors.line)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .padding(horizontal = mockupDp(10), vertical = mockupDp(12)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.ARCHIVE_STAT_NUM),
                    lineHeight = mockupSp(22f)
                ),
                color = colors.purpleDeep
            )
            Text(
                label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.ARCHIVE_STAT_LBL),
                    lineHeight = mockupSp(11f),
                    letterSpacing = mockupSp(0.2f)
                ),
                color = ArchiveStatLabelColor,
                modifier = Modifier.padding(top = mockupDp(2))
            )
        }
    }
}

@Composable
private fun ArchiveFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.ARCHIVE_FILTER_RADIUS))

    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.ARCHIVE_FILTER_SHADOW))
                .clip(shape)
                .background(if (selected) colors.purpleDeep else colors.line)
        )
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.ARCHIVE_FILTER_TEXT),
                lineHeight = mockupSp(13f)
            ),
            color = if (selected) Color.White else ArchiveStatLabelColor,
            modifier = Modifier
                .clip(shape)
                .background(if (selected) colors.purple else colors.paper)
                .clickable(onClick = onClick)
                .padding(horizontal = mockupDp(12), vertical = mockupDp(7))
        )
    }
}

@Composable
private fun ArchiveDayLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = PixelFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.ARCHIVE_DAY_LABEL),
            lineHeight = mockupSp(12f),
            letterSpacing = mockupSp(0.3f)
        ),
        color = ArchiveDayLabelColor,
        modifier = Modifier.padding(top = mockupDp(16), bottom = mockupDp(8))
    )
}
