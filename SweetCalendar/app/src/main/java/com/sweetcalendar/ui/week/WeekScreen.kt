package com.sweetcalendar.ui.week

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import com.sweetcalendar.data.HabitOccurrence
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.completionPairs
import com.sweetcalendar.data.habitsInWeek
import com.sweetcalendar.data.hasHabitOn
import com.sweetcalendar.data.isCompletedThrough
import com.sweetcalendar.data.local.HabitCompletionEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.tasksOnDate
import com.sweetcalendar.ui.components.CompletedSectionLink
import com.sweetcalendar.ui.components.SweetSectionLabel
import com.sweetcalendar.ui.habit.SweetHabitCard
import com.sweetcalendar.ui.motion.appearOnEnter
import com.sweetcalendar.ui.motion.rememberNewItemTracker
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.selectedOnAccentTextColor
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
private fun weekChipDowColor(selected: Boolean): Color {
    val colors = SweetTheme.colors
    return when {
        selected && colors.isDark -> selectedOnAccentTextColor()
        selected -> Color(0xFFEFE6FA)
        else -> colors.muted
    }
}

@Composable
fun WeekScreen(
    tasks: List<TaskEntity>,
    habits: List<HabitWithDays> = emptyList(),
    habitCompletions: List<HabitCompletionEntity> = emptyList(),
    userBirthDate: JalaliDate? = null,
    userName: String = "Friend",
    weekStartsOn: Int = 0,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onDayClick: (JalaliDate) -> Unit = {},
    onHabitClick: (HabitWithDays) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val today = remember { JalaliDate.today() }
    val weekDays = remember(weekStartsOn) { JalaliDate.weekContaining(today, weekStartsOn) }
    var selectedDay by remember { mutableStateOf(today) }
    val listState = rememberLazyListState()

    val weekStart = weekDays.first()
    val weekEnd = weekDays.last()
    val completedPairs = remember(habitCompletions) { habitCompletions.completionPairs() }
    val weekHabits = remember(habits, weekDays) { habits.habitsInWeek(weekDays) }
    val pendingHabits = remember(weekHabits, weekDays, completedPairs, today) {
        weekHabits.filter { !it.isCompletedThrough(weekDays, completedPairs, today) }
    }
    val doneHabits = remember(weekHabits, weekDays, completedPairs, today) {
        weekHabits.filter { it.isCompletedThrough(weekDays, completedPairs, today) }
    }
    val isNewHabit = rememberNewItemTracker(weekHabits.map { it.id })

    val dayPendingHints = remember(tasks, habits, weekDays, userBirthDate, userName) {
        weekDays.associateWith { day ->
            val taskPending = tasks.tasksOnDate(day, userBirthDate, userName).count { !it.isDone }
            val habitPending = if (habits.hasHabitOn(day)) 1 else 0
            (taskPending + habitPending).coerceAtMost(3)
        }
    }

    LaunchedEffect(selectedDay) {
        listState.animateScrollToItem(0)
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
                    fontWeight = SecondaryTextWeight,
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
                DayChip(
                    day = day,
                    calendarSystem = calendarSystem,
                    pendingCount = dayPendingHints[day] ?: 0,
                    selected = day == selectedDay,
                    onClick = {
                        selectedDay = day
                        onDayClick(day)
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
            if (weekHabits.isEmpty()) {
                item {
                    Text(
                        "No habits this week",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = mockupDp(12))
                    )
                }
            } else {
                item { SweetSectionLabel("HABITS") }
                items(pendingHabits, key = { "habit-pending:${it.id}" }) { habit ->
                    SweetHabitCard(
                        habit = habit,
                        onClick = { onHabitClick(habit) },
                        modifier = Modifier.appearOnEnter(isNewHabit(habit.id)),
                    )
                }
                if (doneHabits.isNotEmpty()) {
                    item {
                        CompletedSectionLink(
                            count = doneHabits.size,
                            suffix = "this week",
                        )
                    }
                    items(doneHabits, key = { "habit-done:${it.id}" }) { habit ->
                        SweetHabitCard(
                            habit = habit,
                            occurrence = HabitOccurrence(
                                habit = habit,
                                date = today,
                                reminderTime = null,
                                isDone = true,
                            ),
                            onClick = { onHabitClick(habit) },
                            modifier = Modifier.appearOnEnter(isNewHabit(habit.id)),
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(mockupDp(90))) }
        }
    }
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
                .background(if (selected) colors.purpleDeep else colors.shadow)
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
                    lineHeight = mockupSp(14f)
                ),
                color = weekChipDowColor(selected),
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
                color = if (selected) selectedOnAccentTextColor() else colors.ink,
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
