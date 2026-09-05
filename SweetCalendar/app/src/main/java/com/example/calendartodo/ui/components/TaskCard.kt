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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.themeCelebrationMessage
import com.example.calendartodo.ui.theme.themeCelebrationTitle
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
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
    dismissLabel: String = "Keep",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = SweetTheme.colors
    val deleteColor = Color(0xFFD9455E)

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            top = mockupDp(16),
                            bottom = mockupDp(8)
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✕ $dismissLabel".uppercase(),
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                            lineHeight = mockupSp(14f)
                        ),
                        color = colors.purpleDeep,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                    Text(
                        confirmLabel.uppercase(),
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                            lineHeight = mockupSp(14f)
                        ),
                        color = deleteColor,
                        modifier = Modifier.clickable(onClick = onConfirm)
                    )
                }

                Text(
                    title,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(8)),
                    textAlign = TextAlign.Center
                )

                Text(
                    message,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(12f),
                        lineHeight = mockupSp(18f)
                    ),
                    color = colors.muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(24),
                            end = mockupDp(24),
                            bottom = mockupDp(24)
                        )
                )
            }
        }
    }
}

@Composable
fun CompleteCelebrationDialog(onDismiss: () -> Unit) {
    val colors = SweetTheme.colors

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(24),
                        bottom = mockupDp(24)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ThemeCompletedCheckIcon(size = mockupDp(56))
                Spacer(Modifier.height(mockupDp(12)))
                Text(
                    themeCelebrationTitle(),
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(mockupDp(8)))
                Text(
                    themeCelebrationMessage(),
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(12f),
                        lineHeight = mockupSp(18f)
                    ),
                    color = colors.muted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(mockupDp(20)))
                SweetPixelButton(
                    text = "OK",
                    onClick = onDismiss
                )
            }
        }
    }
}
