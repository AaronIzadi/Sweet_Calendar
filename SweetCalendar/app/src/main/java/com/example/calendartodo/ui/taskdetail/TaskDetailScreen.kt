package com.example.calendartodo.ui.taskdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.components.TaskPriority
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.components.formatTime12h
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    val category = TaskCategory.fromString(task.category)
    val priority = TaskPriority.fromString(task.priority)
    val badgeColor = when (category) {
        TaskCategory.Personal -> colors.pinkDeep
        TaskCategory.Home -> colors.mintDeep
        TaskCategory.Work -> colors.purpleDeep
    }
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(label = "←", onClick = onBack)
            IconButton(label = "✎", onClick = onEdit)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                when (category) {
                    TaskCategory.Personal -> PeppermintCandyIcon(size = 12.dp)
                    TaskCategory.Home -> ChocolateIcon(size = 12.dp)
                    TaskCategory.Work -> WrappedCandyIcon(size = 12.dp)
                }
                Text(
                    category.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                task.title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.ink,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )

            Spacer(Modifier.height(14.dp))
            MetaRow(icon = { PeppermintCandyIcon(size = 14.dp) }, text = "$weekday, $month ${date.day}, ${date.year}")
            task.reminderTime?.let { time ->
                MetaRow(icon = { ChocolateIcon(size = 14.dp) }, text = formatTime12h(time))
            }
            MetaRow(icon = { Text("✦", color = colors.lemonDeep) }, text = "${priority.label} priority")
            if (task.repeatWeekly) {
                MetaRow(icon = { Text("↻", color = colors.purpleDeep) }, text = "Repeats weekly")
            }

            if (task.notes.isNotBlank()) {
                Spacer(Modifier.height(18.dp))
                Text(
                    "NOTES",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.paper)
                        .padding(12.dp, 14.dp)
                ) {
                    Text(task.notes, style = MaterialTheme.typography.bodyMedium, color = colors.muted)
                }
            }

            Spacer(Modifier.height(18.dp))
            if (!task.isDone) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.mintDeep)
                        .clickable(onClick = onComplete)
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MARK COMPLETE", style = MaterialTheme.typography.labelLarge, color = Color.White)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.paper)
                        .clickable(onClick = onComplete)
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MARK INCOMPLETE", style = MaterialTheme.typography.labelLarge, color = colors.purpleDeep)
                }
            }

            Text(
                "Delete task",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD9455E),
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .clickable(onClick = onDelete)
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun IconButton(label: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(colors.paper)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.purpleDeep)
    }
}

@Composable
private fun MetaRow(icon: @Composable () -> Unit, text: String) {
    val colors = SweetTheme.colors
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        icon()
        Text(text, style = MaterialTheme.typography.bodyMedium, color = colors.muted)
    }
}
