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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import kotlinx.coroutines.launch

private val ChipDowColor = Color(0xFF9A8878)
private val ChipSelectedDowColor = Color(0xFFEFE6FA)

@Composable
fun WeekScreen(
    tasks: List<TaskEntity>,
    weekStartsOn: Int = 0,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onEditTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val today = remember { JalaliDate.today() }
    val weekDays = remember(weekStartsOn) { JalaliDate.weekContaining(today, weekStartsOn) }
    var selectedDay by remember { mutableStateOf(today) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val weekStart = weekDays.first()
    val weekEnd = weekDays.last()
    val tasksByDay = remember(tasks, weekDays) {
        weekDays.associateWith { day ->
            tasks.filter { it.jalaliDate == day.formatIso() }
                .sortedWith(compareBy({ it.isDone }, { it.reminderTime.orEmpty() }))
        }
    }
    val daysWithTasks = remember(tasksByDay) {
        weekDays.filter { tasksByDay[it].orEmpty().isNotEmpty() }
    }
    val scrollIndexByDay = remember(daysWithTasks, tasksByDay) {
        var index = 0
        daysWithTasks.associateWith { day ->
            val start = index
            index += 1 + tasksByDay[day].orEmpty().size
            start
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(14),
                    bottom = mockupDp(4)
                )
        ) {
            Text(
                "This week",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.WEEK_TITLE),
                    lineHeight = mockupSp(21f)
                ),
                color = colors.ink,
                modifier = Modifier.padding(top = mockupDp(4))
            )
            Text(
                when (calendarSystem) {
                    CalendarSystem.PERSIAN -> JalaliDate.weekRange(weekStart, weekEnd)
                    CalendarSystem.GREGORIAN -> {
                        val gStart = GregorianDate.fromJalali(weekStart)
                        val gEnd = GregorianDate.fromJalali(weekEnd)
                        GregorianDate.weekRange(gStart, gEnd)
                    }
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = mockupSp(MockupDimens.WEEK_RANGE),
                    lineHeight = mockupSp(15f)
                ),
                color = colors.muted
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(
                    start = mockupDp(14),
                    end = mockupDp(14),
                    top = mockupDp(14),
                    bottom = mockupDp(8)
                ),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(8))
        ) {
            weekDays.forEach { day ->
                val dayTasks = tasksByDay[day].orEmpty()
                val pendingCount = dayTasks.count { !it.isDone }
                DayChip(
                    day = day,
                    calendarSystem = calendarSystem,
                    pendingCount = pendingCount,
                    selected = day == selectedDay,
                    onClick = {
                        selectedDay = day
                        scrollIndexByDay[day]?.let { index ->
                            scope.launch { listState.animateScrollToItem(index) }
                        }
                    }
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = mockupDp(18))
                .padding(top = mockupDp(8))
        ) {
            if (daysWithTasks.isEmpty()) {
                item {
                    Text(
                        "No tasks this week",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = mockupDp(12))
                    )
                }
            } else {
                daysWithTasks.forEach { day ->
                    val dayTasks = tasksByDay[day].orEmpty()
                    item(key = "label-${day.formatIso()}") {
                        WeekDayLabel(day, calendarSystem)
                    }
                    items(dayTasks, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onEditTask(task) },
                            style = TaskCardStyle.Mockup
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(mockupDp(90))) }
        }
    }
}

@Composable
private fun WeekDayLabel(day: JalaliDate, calendarSystem: CalendarSystem) {
    val colors = SweetTheme.colors
    val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[day.weekdayIndex()].uppercase()
    val label = when (calendarSystem) {
        CalendarSystem.PERSIAN -> {
            val month = JalaliDate.MONTH_NAMES_EN[day.month - 1].uppercase()
            "$weekday · $month ${day.day}"
        }
        CalendarSystem.GREGORIAN -> {
            val g = GregorianDate.fromJalali(day)
            val month = GregorianDate.MONTH_NAMES_EN[g.month - 1].uppercase()
            "$weekday · $month ${g.day}"
        }
    }
    Text(
        label,
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(MockupDimens.WEEK_DAY_LABEL),
            lineHeight = mockupSp(13f)
        ),
        color = colors.purpleDeep,
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(8))
    )
}

@Composable
private fun DayChip(
    day: JalaliDate,
    calendarSystem: CalendarSystem,
    pendingCount: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors
    val displayDay = when (calendarSystem) {
        CalendarSystem.PERSIAN -> day.day
        CalendarSystem.GREGORIAN -> GregorianDate.fromJalali(day).day
    }
    val dow = JalaliDate.WEEKDAY_NAMES_EN_SHORT[day.weekdayIndex()].uppercase()
    val shape = RoundedCornerShape(mockupDp(14))
    val chipWidth = mockupDp(MockupDimens.DAY_CHIP_MIN_W)
    val shadowOffset = if (selected) mockupDp(3) else mockupDp(2)

    Box(
        modifier = Modifier
            .width(chipWidth)
            .then(if (selected) Modifier.offset(y = mockupDp(-2)) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .width(chipWidth)
                .height(mockupDp(72))
                .offset(y = shadowOffset)
                .clip(shape)
                .background(if (selected) colors.purpleDeep else colors.line)
        )
        Column(
            modifier = Modifier
                .width(chipWidth)
                .clip(shape)
                .background(if (selected) colors.purple else colors.paper)
                .clickable(onClick = onClick)
                .padding(horizontal = mockupDp(4))
                .padding(top = mockupDp(10), bottom = mockupDp(8)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mockupDp(6))
        ) {
            Text(
                dow,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.DAY_CHIP_DOW),
                    lineHeight = mockupSp(11f)
                ),
                color = if (selected) ChipSelectedDowColor else ChipDowColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Text(
                displayDay.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.DAY_CHIP_NUM),
                    lineHeight = mockupSp(18f)
                ),
                color = if (selected) Color.White else colors.ink,
                maxLines = 1
            )
            Row(horizontalArrangement = Arrangement.spacedBy(mockupDp(2))) {
                repeat(3) { i ->
                    Box(
                        Modifier
                            .size(mockupDp(MockupDimens.DAY_CHIP_PIP))
                            .clip(RoundedCornerShape(mockupDp(1)))
                            .background(
                                if (i < pendingCount.coerceAtMost(3)) colors.pinkDeep else colors.line
                            )
                    )
                }
            }
        }
    }
}
