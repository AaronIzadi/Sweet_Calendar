package com.sweetcalendar.ui.daydetail

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
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
import com.sweetcalendar.data.HabitOccurrence
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.aggregateHabitJarCounts
import com.sweetcalendar.data.aggregateJarCounts
import com.sweetcalendar.data.effectiveIsDone
import com.sweetcalendar.data.forTask
import com.sweetcalendar.data.groupedByParent
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.data.taskProgressSlice
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.recurrence.occurrenceKey
import com.sweetcalendar.ui.calendar.DayEvent
import com.sweetcalendar.ui.components.BackArrowMiniIcon
import com.sweetcalendar.ui.components.CompletedSectionLink
import com.sweetcalendar.ui.components.JarProgressCard
import com.sweetcalendar.ui.components.SweetFab
import com.sweetcalendar.ui.components.SweetIconButton
import com.sweetcalendar.ui.components.SweetSectionLabel
import com.sweetcalendar.ui.components.SweetTaskCard
import com.sweetcalendar.ui.components.TaskCardStyle
import com.sweetcalendar.ui.components.ThemeHolidayHeroIcon
import com.sweetcalendar.ui.components.formatAlternateCalendarLine
import com.sweetcalendar.ui.components.sweetTaskItemMotion
import com.sweetcalendar.ui.habit.SweetHabitCard
import com.sweetcalendar.ui.motion.appearOnEnter
import com.sweetcalendar.ui.motion.rememberNewItemTracker
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
private fun dayDetailSubColor(): Color = SweetTheme.colors.muted

@Composable
private fun dayEventMetaColor(): Color = SweetTheme.colors.muted

@Composable
fun DayDetailScreen(
    date: JalaliDate,
    tasks: List<TaskEntity>,
    habits: List<HabitOccurrence> = emptyList(),
    subtasks: List<SubtaskEntity> = emptyList(),
    events: List<DayEvent>,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onHabitClick: (HabitWithDays) -> Unit = {},
    onToggleHabitComplete: (HabitOccurrence) -> Unit = {},
    onCompleteTask: (TaskEntity) -> Unit = {},
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
            val g = com.sweetcalendar.jalali.GregorianDate.fromJalali(date)
            "${com.sweetcalendar.jalali.GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}"
        }
    }
    val secondaryLine = "${weekday} · ${date.formatAlternateCalendarLine(calendarSystem)}"
    val subtasksByParent = remember(subtasks) { subtasks.groupedByParent() }
    val doneTasks = tasks.count { it.effectiveIsDone(subtasksByParent.forTask(it)) }
    val doneHabits = habits.count { it.isDone }
    val (taskWeightedDone, taskWeightedTotal) = remember(tasks, subtasksByParent) {
        aggregateJarCounts(tasks, subtasksByParent)
    }
    val (habitWeightedDone, habitWeightedTotal) = remember(habits) {
        aggregateHabitJarCounts(
            habits.map { it.habit.habit.priority to it.isDone },
        )
    }
    val done = doneTasks + doneHabits
    val total = tasks.size + habits.size
    val weightedDone = taskWeightedDone + habitWeightedDone
    val weightedTotal = taskWeightedTotal + habitWeightedTotal
    val holidays = events.filter { it.isHoliday && it.description.isNotBlank() }
    val occasions = events.filter { !it.isHoliday && it.description.isNotBlank() }
    val eventIconSize = mockupDp(MockupDimens.HOLIDAY_EVENT_ICON)
    val isNewTask = rememberNewItemTracker(tasks.map { it.id })
    val isNewHabit = rememberNewItemTracker(habits.map { it.id })

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
                SweetIconButton(onClick = onBack) {
                    BackArrowMiniIcon(
                        size = mockupDp(14),
                        color = colors.purpleDeep
                    )
                }
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
                            fontWeight = SecondaryTextWeight,
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
                        total = total.coerceAtLeast(done),
                        weightedCompleted = weightedDone,
                        weightedTotal = weightedTotal,
                    )
                }
                val pendingScheduled = tasks.filter {
                    !it.isToDo() && !it.effectiveIsDone(subtasksByParent.forTask(it))
                }
                val doneScheduled = tasks.filter {
                    !it.isToDo() && it.effectiveIsDone(subtasksByParent.forTask(it))
                }
                val pendingTodos = tasks.filter {
                    it.isToDo() && !it.effectiveIsDone(subtasksByParent.forTask(it))
                }
                val doneTodos = tasks.filter {
                    it.isToDo() && it.effectiveIsDone(subtasksByParent.forTask(it))
                }
                val pendingHabits = habits.filter { !it.isDone }
                val completedHabits = habits.filter { it.isDone }

                dayDetailActivitySection(
                    label = "TASKS",
                    pending = pendingScheduled,
                    completed = doneScheduled,
                    pendingKey = { it.occurrenceKey() },
                    completedKey = { "done:${it.occurrenceKey()}" },
                ) { task ->
                    dayDetailTaskCard(
                        task = task,
                        subtasks = subtasksByParent.forTask(task),
                        isNew = isNewTask(task.id),
                        onTaskClick = onTaskClick,
                        onCompleteTask = onCompleteTask,
                    )
                }
                dayDetailActivitySection(
                    label = "HABITS",
                    pending = pendingHabits,
                    completed = completedHabits,
                    pendingKey = { it.key },
                    completedKey = { "done:${it.key}" },
                ) { occurrence ->
                    SweetHabitCard(
                        habit = occurrence.habit,
                        occurrence = occurrence,
                        onClick = { onHabitClick(occurrence.habit) },
                        onToggleComplete = { onToggleHabitComplete(occurrence) },
                        modifier = Modifier.appearOnEnter(isNewHabit(occurrence.id)),
                    )
                }
                dayDetailActivitySection(
                    label = "TO DO",
                    pending = pendingTodos,
                    completed = doneTodos,
                    pendingKey = { it.occurrenceKey() },
                    completedKey = { "done:${it.occurrenceKey()}" },
                ) { task ->
                    dayDetailTaskCard(
                        task = task,
                        subtasks = subtasksByParent.forTask(task),
                        isNew = isNewTask(task.id),
                        onTaskClick = onTaskClick,
                        onCompleteTask = onCompleteTask,
                    )
                }
                if (occasions.isNotEmpty()) {
                    item { SweetSectionLabel("OCCASIONS") }
                    items(occasions.size) { index ->
                        val event = occasions[index]
                        DayEventCard(
                            title = event.description,
                            meta = "Calendar occasion",
                            accentColor = colors.purpleDeep,
                            icon = { ThemeHolidayHeroIcon(size = eventIconSize) },
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
                            meta = "Official holiday",
                            accentColor = colors.mintDeep,
                            icon = { ThemeHolidayHeroIcon(size = eventIconSize) },
                            onClick = { onHolidayClick(event) }
                        )
                    }
                }
                if (tasks.isEmpty() && habits.isEmpty() && holidays.isEmpty() && occasions.isEmpty()) {
                    item {
                        Text(
                            "Nothing scheduled for this day",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = SecondaryTextWeight,
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

private fun <T> LazyListScope.dayDetailActivitySection(
    label: String,
    pending: List<T>,
    completed: List<T>,
    pendingKey: (T) -> Any,
    completedKey: (T) -> Any,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (pending.isEmpty() && completed.isEmpty()) return
    item { SweetSectionLabel(label) }
    items(pending, key = pendingKey, itemContent = itemContent)
    if (completed.isNotEmpty()) {
        item {
            CompletedSectionLink(
                count = completed.size,
                suffix = "",
            )
        }
        items(completed, key = completedKey, itemContent = itemContent)
    }
}

@Composable
private fun LazyItemScope.dayDetailTaskCard(
    task: TaskEntity,
    subtasks: List<SubtaskEntity>,
    isNew: Boolean,
    onTaskClick: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
) {
    val ratio = taskProgressSlice(task, subtasks).ratio.takeIf { subtasks.isNotEmpty() }
    SweetTaskCard(
        task = task,
        modifier = sweetTaskItemMotion().appearOnEnter(isNew),
        onClick = { onTaskClick(task) },
        onToggleComplete = { onCompleteTask(task) },
        style = TaskCardStyle.Mockup,
        subtaskProgressRatio = ratio,
    )
}

@Composable
fun DayEventCard(
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
                .background(colors.shadow)
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
                            fontWeight = SecondaryTextWeight,
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
