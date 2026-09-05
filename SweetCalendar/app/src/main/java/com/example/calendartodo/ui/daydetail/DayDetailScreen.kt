package com.example.calendartodo.ui.daydetail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.calendar.DayEvent
import com.example.calendartodo.ui.components.JarProgressCard
import com.example.calendartodo.ui.components.NavPeppermintIcon
import com.example.calendartodo.ui.components.SweetFab
import com.example.calendartodo.ui.components.SweetIconButton
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.components.formatAlternateCalendarLine
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val DayDetailSubLight = Color(0xFF8A7867)
private val DayEventMetaLight = Color(0xFF9A8878)

@Composable
private fun dayDetailSubColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else DayDetailSubLight
}

@Composable
private fun dayEventMetaColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else DayEventMetaLight
}

@Composable
fun DayDetailScreen(
    date: JalaliDate,
    tasks: List<TaskEntity>,
    events: List<DayEvent>,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onAddTask: () -> Unit,
    onHolidayClick: (DayEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val colors = SweetTheme.colors
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val primaryTitle = when (calendarSystem) {
        CalendarSystem.PERSIAN -> "${JalaliDate.MONTH_NAMES_EN[date.month - 1]} ${date.day}"
        CalendarSystem.GREGORIAN -> {
            val g = com.example.calendartodo.jalali.GregorianDate.fromJalali(date)
            "${com.example.calendartodo.jalali.GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}"
        }
    }
    val secondaryLine = "${weekday} · ${date.formatAlternateCalendarLine(calendarSystem)}"
    val done = tasks.count { it.isDone }
    val holidays = events.filter { it.isHoliday && it.description.isNotBlank() }
    val occasions = events.filter { !it.isHoliday && it.description.isNotBlank() }
    val eventIconSize = mockupDp(MockupDimens.HOLIDAY_EVENT_ICON)

    Box(modifier = modifier.fillMaxSize().background(colors.cream)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(16),
                        bottom = mockupDp(6)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SweetIconButton(label = "←", onClick = onBack)
                Column(modifier = Modifier.padding(start = mockupDp(MockupDimens.DAY_DETAIL_HEADER_GAP))) {
                    Text(
                        primaryTitle,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(MockupDimens.DAY_DETAIL_TITLE),
                            lineHeight = mockupSp(22f)
                        ),
                        color = colors.ink
                    )
                    Text(
                        secondaryLine,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.DAY_DETAIL_SUB_F),
                            lineHeight = mockupSp(15f)
                        ),
                        color = dayDetailSubColor()
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = mockupDp(18))
            ) {
                item {
                    Spacer(Modifier.height(mockupDp(6)))
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
                            style = TaskCardStyle.Mockup
                        )
                    }
                }
                if (occasions.isNotEmpty()) {
                    item { SweetSectionLabel("OCCASIONS") }
                    items(occasions.size) { index ->
                        val event = occasions[index]
                        DayEventCard(
                            title = event.description,
                            meta = "Calendar occasion · time.ir",
                            accentColor = colors.purpleDeep,
                            icon = { NavPeppermintIcon(size = eventIconSize) },
                            onClick = { onHolidayClick(event) }
                        )
                    }
                }
                if (holidays.isNotEmpty()) {
                    item { SweetSectionLabel("HOLIDAY") }
                    items(holidays.size) { index ->
                        val event = holidays[index]
                        DayEventCard(
                            title = event.description,
                            meta = "Official occasion · time.ir",
                            accentColor = colors.mintDeep,
                            icon = { NavPeppermintIcon(size = eventIconSize) },
                            onClick = { onHolidayClick(event) }
                        )
                    }
                }
                if (tasks.isEmpty() && holidays.isEmpty() && occasions.isEmpty()) {
                    item {
                        Text(
                            "Nothing scheduled for this day",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = mockupSp(MockupDimens.DAY_DETAIL_SUB_F),
                                lineHeight = mockupSp(15f)
                            ),
                            color = dayDetailSubColor(),
                            modifier = Modifier.padding(vertical = mockupDp(24))
                        )
                    }
                }
                item { Spacer(Modifier.height(mockupDp(90))) }
            }
        }

        SweetFab(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = mockupDp(18), bottom = mockupDp(18))
        )
    }
}

@Composable
private fun DayEventCard(
    title: String,
    meta: String,
    accentColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(14))
    val taskAccentH = mockupDp(56)

    Box(modifier = modifier.padding(bottom = mockupDp(10))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(2))
                .clip(shape)
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = mockupDp(MockupDimens.TASK_ACCENT_W), height = taskAccentH)
                    .background(accentColor)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = mockupDp(MockupDimens.TASK_CARD_PAD_H),
                        vertical = mockupDp(MockupDimens.TASK_CARD_PAD_V)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
            ) {
                icon()
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(MockupDimens.TASK_TITLE_F),
                            lineHeight = mockupSp(17f)
                        ),
                        color = colors.ink,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        meta,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.TASK_META_F),
                            lineHeight = mockupSp(13f)
                        ),
                        color = dayEventMetaColor(),
                        modifier = Modifier.padding(top = mockupDp(2))
                    )
                }
            }
        }
    }
}
