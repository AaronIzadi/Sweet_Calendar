package com.sweetcalendar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sweetcalendar.data.TaskListScope
import com.sweetcalendar.ui.archive.ArchiveFilterChip
import com.sweetcalendar.ui.theme.mockupDp

@Composable
fun ActivityTypeFilterRow(
    selected: TaskListScope,
    onSelect: (TaskListScope) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = mockupDp(4)),
        horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
    ) {
        ArchiveFilterChip(
            label = "Tasks",
            selected = selected == TaskListScope.Scheduled,
            onClick = { onSelect(TaskListScope.Scheduled) },
        )
        ArchiveFilterChip(
            label = "Habits",
            selected = selected == TaskListScope.Habit,
            onClick = { onSelect(TaskListScope.Habit) },
        )
        ArchiveFilterChip(
            label = "To-dos",
            selected = selected == TaskListScope.Todo,
            onClick = { onSelect(TaskListScope.Todo) },
        )
    }
}
