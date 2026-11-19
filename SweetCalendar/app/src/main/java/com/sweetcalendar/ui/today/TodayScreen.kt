package com.sweetcalendar.ui.today

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
import androidx.compose.foundation.layout.heightIn
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
import com.sweetcalendar.data.HabitOccurrence
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.aggregateHabitJarCounts
import com.sweetcalendar.data.aggregateJarCounts
import com.sweetcalendar.data.effectiveIsDone
import com.sweetcalendar.data.forTask
import com.sweetcalendar.data.groupedByParent
import com.sweetcalendar.data.habitIdsCompletedOn
import com.sweetcalendar.data.habitsOnDate
import com.sweetcalendar.data.isToDo
import com.sweetcalendar.data.local.HabitCompletionEntity
import com.sweetcalendar.data.local.SubtaskEntity
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.profile.isBirthdayTask
import com.sweetcalendar.recurrence.tasksOnDate
import com.sweetcalendar.ui.components.CompletedSectionLink
import com.sweetcalendar.ui.components.EmptyStateContent
import com.sweetcalendar.ui.components.JarProgressCard
import com.sweetcalendar.ui.components.StreakPill
import com.sweetcalendar.ui.components.SweetSectionLabel
import com.sweetcalendar.ui.components.scheduledAndTodoTaskSections
import com.sweetcalendar.ui.habit.SweetHabitCard
import com.sweetcalendar.ui.motion.appearOnEnter
import com.sweetcalendar.ui.motion.rememberNewItemTracker
import com.sweetcalendar.ui.components.formatDisplayWithWeekday
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.themeTodayJarLabel
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
    habits: List<HabitWithDays> = emptyList(),
    habitCompletions: List<HabitCompletionEntity> = emptyList(),
    subtasks: List<SubtaskEntity> = emptyList(),
    userName: String,
    userBirthDate: JalaliDate? = null,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onEditTask: (TaskEntity) -> Unit,
    onHabitClick: (HabitWithDays) -> Unit = {},
    onToggleHabitComplete: (HabitOccurrence) -> Unit = {},
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
    val subtasksByParent = remember(subtasks) { subtasks.groupedByParent() }
    val completedHabitIds = remember(habitCompletions, today) {
        habitCompletions.habitIdsCompletedOn(today)
    }
    val todayHabits = remember(habits, today, completedHabitIds) {
        habits.habitsOnDate(today, completedHabitIds)
    }
    val pendingHabits = remember(todayHabits) { todayHabits.filter { !it.isDone } }
    val doneHabits = remember(todayHabits) { todayHabits.filter { it.isDone } }
    val isNewHabit = rememberNewItemTracker(todayHabits.map { it.id })
    val todayTasks = remember(tasks, today, userBirthDate, userName) {
        tasks.tasksOnDate(today, userBirthDate, userName).sortedBy { it.createdAt }
    }
    val actionableTasks = remember(todayTasks) { todayTasks.filter { !it.isBirthdayTask() } }
    val pendingScheduled = remember(todayTasks, subtasksByParent) {
        todayTasks.filter { task ->
            !task.isToDo() && !task.effectiveIsDone(subtasksByParent.forTask(task))
        }
    }
    val doneScheduled = remember(actionableTasks, subtasksByParent) {
        actionableTasks.filter { task ->
            !task.isToDo() && task.effectiveIsDone(subtasksByParent.forTask(task))
        }
    }
    val pendingTodos = remember(todayTasks, subtasksByParent) {
        todayTasks.filter { task ->
            task.isToDo() && !task.effectiveIsDone(subtasksByParent.forTask(task))
        }
    }
    val doneTodos = remember(actionableTasks, subtasksByParent) {
        actionableTasks.filter { task ->
            task.isToDo() && task.effectiveIsDone(subtasksByParent.forTask(task))
        }
    }
    val doneTasks = remember(doneScheduled, doneTodos) { doneScheduled + doneTodos }
    val (taskWeightedDone, taskWeightedTotal) = remember(actionableTasks, subtasksByParent) {
        aggregateJarCounts(actionableTasks, subtasksByParent)
    }
    val habitDoneCount = doneHabits.size
    val habitTotalCount = todayHabits.size
    val jarCompleted = doneTasks.size + habitDoneCount
    val jarTotal = actionableTasks.size + habitTotalCount
    val (habitWeightedDone, habitWeightedTotal) = remember(todayHabits) {
        aggregateHabitJarCounts(
            todayHabits.map { it.habit.habit.priority to it.isDone },
        )
    }
    val weightedDone = taskWeightedDone + habitWeightedDone
    val weightedTotal = taskWeightedTotal + habitWeightedTotal
    val streak = remember(tasks) { computeStreak(tasks) }
    val isNewTask = rememberNewItemTracker(todayTasks.map { it.id })
    val hasContent = todayTasks.isNotEmpty() || todayHabits.isNotEmpty()

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
            Spacer(Modifier.height(12.dp))
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
                            fontWeight = SecondaryTextWeight,
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
                label = themeTodayJarLabel(),
                completed = jarCompleted,
                total = jarTotal,
                weightedCompleted = weightedDone,
                weightedTotal = weightedTotal,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 18.dp)
        ) {
            if (!hasContent) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = mockupDp(248))
                            .padding(start = 14.dp, end = 14.dp, top = 38.dp, bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateContent(onAddTask = onAddTask)
                    }
                }
            } else {
                if (
                    pendingScheduled.isNotEmpty() ||
                    doneScheduled.isNotEmpty() ||
                    pendingTodos.isNotEmpty() ||
                    doneTodos.isNotEmpty()
                ) {
                    scheduledAndTodoTaskSections(
                        scheduled = pendingScheduled,
                        todos = pendingTodos,
                        subtasksByParent = subtasksByParent,
                        isNewTask = isNewTask,
                        onTaskClick = onEditTask,
                        onCompleteTask = onCompleteTask,
                        onDeleteTask = onDeleteTask,
                        doneScheduled = doneScheduled,
                        doneTodos = doneTodos,
                        scheduledCompletedHeader = {
                            CompletedSectionLink(
                                count = doneScheduled.size,
                                suffix = "today",
                                onClick = onOpenArchive,
                            )
                        },
                        todoCompletedHeader = {
                            CompletedSectionLink(
                                count = doneTodos.size,
                                suffix = "today",
                                onClick = onOpenArchive,
                            )
                        },
                    )
                }
                if (pendingHabits.isNotEmpty() || doneHabits.isNotEmpty()) {
                    item { SweetSectionLabel("HABITS") }
                    items(pendingHabits, key = { "habit-pending:${it.key}" }) { occurrence ->
                        SweetHabitCard(
                            habit = occurrence.habit,
                            occurrence = occurrence,
                            onClick = { onHabitClick(occurrence.habit) },
                            onToggleComplete = { onToggleHabitComplete(occurrence) },
                            modifier = Modifier.appearOnEnter(isNewHabit(occurrence.id)),
                        )
                    }
                    if (doneHabits.isNotEmpty()) {
                        item {
                            CompletedSectionLink(
                                count = doneHabits.size,
                                suffix = "today",
                            )
                        }
                        items(doneHabits, key = { "habit-done:${it.key}" }) { occurrence ->
                            SweetHabitCard(
                                habit = occurrence.habit,
                                occurrence = occurrence,
                                onClick = { onHabitClick(occurrence.habit) },
                                onToggleComplete = { onToggleHabitComplete(occurrence) },
                                modifier = Modifier.appearOnEnter(isNewHabit(occurrence.id)),
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(90.dp)) }
        }
    }
}
