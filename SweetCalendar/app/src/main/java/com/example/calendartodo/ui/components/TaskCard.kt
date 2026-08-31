package com.example.calendartodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.CaramelOrange
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.components.pixelBorder

@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit = {},
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    var menuOpen by remember { mutableStateOf(false) }

    PixelPanel(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        backgroundColor = if (task.isDone) CreamFrosting.copy(alpha = 0.75f) else CreamFrosting
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    date.weekdayName(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    date.day.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = BubblegumPink
                )
                Text(
                    JalaliDate.MONTH_NAMES_EN[date.month - 1],
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(PixelBorder.copy(alpha = 0.35f))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (task.isDone) ChocolateBrown.copy(alpha = 0.5f) else ChocolateBrown,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f)
                    )
                    Box {
                        IconButton(
                            onClick = { menuOpen = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = ChocolateBrown,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { menuOpen = false; onEdit() }
                            )
                            if (!task.isDone) {
                                DropdownMenuItem(
                                    text = { Text("Mark complete") },
                                    onClick = { menuOpen = false; onComplete() }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Mark incomplete") },
                                    onClick = { menuOpen = false; onComplete() }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { menuOpen = false; onDelete() }
                            )
                        }
                    }
                }

                if (task.notes.isNotBlank()) {
                    Text(
                        task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val meta = buildList {
                    task.reminderTime?.let { add(it) }
                    if (task.category.isNotBlank()) add(task.category)
                }
                if (meta.isNotEmpty()) {
                    Text(
                        meta.joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall,
                        color = GrapePurple,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(
                        modifier = Modifier
                            .pixelBorder(borderWidth = 2.dp, shadowOffset = 2.dp)
                            .background(if (task.isDone) MintGreen else CaramelOrange)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            if (task.isDone) "COMPLETED" else "UPCOMING",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (task.isDone) ChocolateBrown else CreamFrosting
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PixelConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String = "No",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        PixelPanel(modifier = Modifier.fillMaxWidth(), backgroundColor = LemonYellow.copy(alpha = 0.95f)) {
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = ChocolateBrown)
                Spacer(Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodySmall, color = ChocolateBrown)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    PixelButton(onClick = onDismiss, backgroundColor = CreamFrosting, contentDescription = dismissLabel) {
                        Text(dismissLabel, style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
                    }
                    Spacer(Modifier.size(8.dp))
                    PixelButton(onClick = onConfirm, backgroundColor = BubblegumPink, contentDescription = confirmLabel) {
                        Text(confirmLabel, style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
                    }
                }
            }
        }
    }
}

@Composable
fun CompleteCelebrationDialog(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        PixelPanel(modifier = Modifier.fillMaxWidth(), backgroundColor = MintGreen.copy(alpha = 0.95f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ChocolateIcon(size = 72.dp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Sweet job!",
                    style = MaterialTheme.typography.titleMedium,
                    color = ChocolateBrown
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You completed the task successfully!",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChocolateBrown,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.height(20.dp))
                PixelButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = BubblegumPink,
                    contentDescription = "Done"
                ) {
                    Text("Done", style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
                }
            }
        }
    }
}
