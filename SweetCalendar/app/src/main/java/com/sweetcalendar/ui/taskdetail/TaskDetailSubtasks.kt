package com.sweetcalendar.ui.taskdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.data.taskProgressSlice
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.parsedDate
import com.sweetcalendar.ui.components.SettingsBoxUncheckedIcon
import com.sweetcalendar.ui.components.TaskCardPriorityMarker
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.ThemeCompletedCheckIcon
import com.sweetcalendar.ui.components.formatDisplayShort
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MintGreen
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
fun TaskDetailSubtasksSection(
    task: TaskEntity,
    subtasks: List<SubtaskEntity>,
    onToggle: (SubtaskEntity) -> Unit,
    modifier: Modifier = Modifier,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
) {
    if (subtasks.isEmpty()) return
    val colors = SweetTheme.colors
    val progress = taskProgressSlice(task = task, subtasks = subtasks)
    val parentDeadline = task.parsedDate()
    val showSubDeadlines = task.isToDo()
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = mockupDp(8)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "SUBTASKS",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SECTION_LABEL_F),
                ),
                color = colors.purpleDeep,
            )
            Text(
                "${(progress.ratio * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = colors.muted,
            )
        }
        subtasks.forEach { sub ->
            val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
            val stroke = mockupDp(1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = mockupDp(8))
                    .drawBehind {
                        val strokePx = stroke.toPx()
                        val half = strokePx / 2f
                        drawRoundRect(
                            color = colors.fieldOutline,
                            topLeft = Offset(half, half),
                            size = Size(size.width - strokePx, size.height - strokePx),
                            cornerRadius = CornerRadius(radius.toPx()),
                            style = Stroke(
                                width = strokePx,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f),
                            ),
                        )
                    }
                    .padding(mockupDp(10)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(sub) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                ) {
                    Box(
                        modifier = Modifier.size(mockupDp(18)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (sub.isDone) {
                            ThemeCompletedCheckIcon(
                                size = mockupDp(18),
                                bgColor = if (colors.isDark) colors.mint else MintGreen,
                            )
                        } else {
                            SettingsBoxUncheckedIcon(size = mockupDp(16))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            sub.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (sub.isDone) TextDecoration.LineThrough else null,
                            ),
                            color = if (sub.isDone) colors.muted else colors.ink,
                        )
                        if (showSubDeadlines) {
                            val deadline = sub.jalaliDate
                                ?.takeIf { it.isNotBlank() }
                                ?.let { JalaliDate.parseIso(it) }
                                ?: parentDeadline
                            Text(
                                "Due ${deadline.formatDisplayShort(calendarSystem)}",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = SecondaryTextWeight,
                                    fontSize = mockupSp(11f),
                                ),
                                color = colors.muted,
                                modifier = Modifier.padding(top = mockupDp(2)),
                            )
                        }
                    }
                    TaskCardPriorityMarker(priority = TaskPriority.fromString(sub.priority))
                }
            }
        }
    }
}
