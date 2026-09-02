package com.example.calendartodo.ui.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.SweetRecoverSnackbar
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.components.formatDisplayWithWeekday
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import kotlinx.coroutines.delay
import java.util.Calendar

private val HintColor = Color(0xFF9A8878)

private fun timeOfDayGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}

@Composable
fun RecoverSnackbarScreen(
    userName: String,
    calendarSystem: CalendarSystem,
    deletedTasks: List<TaskEntity>,
    onRestore: (TaskEntity) -> Unit,
    onPermanentDelete: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val today = remember { JalaliDate.today() }
    var pendingRecover by remember { mutableStateOf<PendingRecover?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mockupDp(18))
        ) {
            item {
                Spacer(Modifier.height(mockupDp(6)))
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.GREET_DATE_F),
                            lineHeight = mockupSp(16f)
                        ),
                        color = colors.muted,
                        modifier = Modifier.padding(top = mockupDp(2))
                    )
                }
            }
            item { SweetSectionLabel("DELETED") }
            if (deletedTasks.isEmpty()) {
                item {
                    Text(
                        "No deleted tasks right now",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(12f),
                            lineHeight = mockupSp(16f)
                        ),
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = mockupDp(24))
                    )
                }
            } else {
                items(deletedTasks, key = { it.id }) { task ->
                    SweetTaskCard(
                        task = task,
                        style = TaskCardStyle.Mockup,
                        showDate = true,
                        onRecover = { onRestore(task) },
                        onDelete = { pendingRecover = PendingRecover.PermanentDelete(task) }
                    )
                }
            }
            item {
                Text(
                    "Tap RECOVER to return a task to your lists. Deleted tasks are removed permanently after 30 days.",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(MockupDimens.DEMO_HINT_F),
                        lineHeight = mockupSp(15f)
                    ),
                    color = HintColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = mockupDp(10),
                            bottom = mockupDp(MockupDimens.SNACKBAR_SCROLL_PAD_BOTTOM)
                        )
                )
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
