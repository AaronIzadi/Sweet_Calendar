package com.example.calendartodo.ui.snackbar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.archive.ArchiveDayLabel
import com.example.calendartodo.ui.archive.ArchiveFilterChip
import com.example.calendartodo.ui.archive.ArchiveStatCard
import com.example.calendartodo.ui.components.SweetRecoverSnackbar
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.stats.ArchiveFilter
import com.example.calendartodo.ui.stats.countDeletedThisWeek
import com.example.calendartodo.ui.stats.countExpiringDeletedTasks
import com.example.calendartodo.ui.stats.filterDeletedTasks
import com.example.calendartodo.ui.stats.groupDeletedTasksByDay
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import kotlinx.coroutines.delay

@Composable
fun RecoverSnackbarScreen(
    deletedTasks: List<TaskEntity>,
    onBack: () -> Unit,
    onRestore: (TaskEntity) -> Unit,
    onPermanentDelete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    var filter by remember { mutableStateOf(ArchiveFilter.ThisWeek) }
    var pendingRecover by remember { mutableStateOf<PendingRecover?>(null) }
    val filtered = remember(deletedTasks, filter) { filterDeletedTasks(deletedTasks, filter) }
    val grouped = remember(filtered) { groupDeletedTasksByDay(filtered) }
    val deletedThisWeek = remember(deletedTasks) { countDeletedThisWeek(deletedTasks) }
    val expiringSoon = remember(deletedTasks) { countExpiringDeletedTasks(deletedTasks) }

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Recover snackbar",
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
                ArchiveStatCard("${deletedTasks.size}", "IN TRASH", modifier = Modifier.weight(1f))
                ArchiveStatCard("$deletedThisWeek", "THIS WEEK", modifier = Modifier.weight(1f))
                ArchiveStatCard("$expiringSoon", "EXPIRING", modifier = Modifier.weight(1f))
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
                            "No deleted tasks in this period",
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
                        items(dayTasks, key = { it.id }) { task ->
                            SweetTaskCard(
                                task = task,
                                style = TaskCardStyle.Mockup,
                                showDate = true,
                                onRecover = { onRestore(task) },
                                onDelete = { pendingRecover = PendingRecover.PermanentDelete(task) }
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(mockupDp(24))) }
            }
        }

        pendingRecover?.let { pending ->
            SweetRecoverSnackbar(
                message = pending.message,
                visible = true,
                onRecover = { pendingRecover = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = mockupDp(MockupDimens.SNACKBAR_BOTTOM))
            )
            LaunchedEffect(pending) {
                delay(4000)
                if (pendingRecover == pending) {
                    when (pending) {
                        is PendingRecover.PermanentDelete -> onPermanentDelete(pending.task)
                    }
                    pendingRecover = null
                }
            }
        }
    }
}

private sealed class PendingRecover {
    abstract val message: String

    data class PermanentDelete(val task: TaskEntity) : PendingRecover() {
        override val message: String = "\"${task.title}\" deleted"
    }
}
