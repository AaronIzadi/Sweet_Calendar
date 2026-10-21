package com.sweetcalendar.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.themeCelebrationMessage
import com.sweetcalendar.ui.motion.confettiBurst
import com.sweetcalendar.ui.theme.themeCelebrationTitle
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit = {},
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    var menuOpen by remember { mutableStateOf(false) }
    val panelBg = if (task.isDone) colors.paper.copy(alpha = 0.75f) else colors.paper

    PixelPanel(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        backgroundColor = panelBg,
        borderColor = colors.choc,
        shadowColor = colors.shadowDeep
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
                    color = colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    date.day.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.pink
                )
                Text(
                    JalaliDate.MONTH_NAMES_EN[date.month - 1],
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(colors.line.copy(alpha = 0.35f))
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
                        color = if (task.isDone) colors.muted else colors.ink,
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
                                tint = colors.ink,
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
                        color = colors.muted,
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
                        color = colors.purpleDeep,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(
                        modifier = Modifier
                            .themePixelBorder(
                                borderWidth = 2.dp,
                                shadowOffset = 2.dp,
                                borderColor = colors.choc,
                                shadowColor = colors.shadowDeep
                            )
                            .background(if (task.isDone) colors.mint else colors.lemon)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            if (task.isDone) "COMPLETED" else "UPCOMING",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (task.isDone) colors.ink else colors.paper
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
            SweetDialogSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
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
                        .then(sheetDialogTitlePaddingModifier()),
                    textAlign = TextAlign.Center
                )

                Text(
                    message,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = SecondaryTextWeight,
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
                            bottom = mockupDp(16)
                        )
                )

                SheetFooterBar(
                    onClose = onDismiss,
                    closeLabel = dismissLabel,
                    trailing = {
                        SheetHeaderConfirmAction(
                            label = confirmLabel,
                            onClick = onConfirm,
                            textColor = deleteColor,
                            showCheckIcon = false
                        )
                    }
                )
            }
            }
        }
    }
}

@Composable
fun CompleteCelebrationDialog(onDismiss: () -> Unit) {
    val colors = SweetTheme.colors

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            SweetDialogSurface {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
                    .confettiBurst(
                        colors = listOf(
                            colors.pink,
                            colors.lemon,
                            colors.mint,
                            colors.purple,
                            colors.pinkDeep,
                        ),
                    ),
            ) {
                CelebrationBurstBackdrop(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(mockupDp(16))),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            top = mockupDp(24),
                            bottom = mockupDp(24)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                CelebrationJarHero()
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
                        fontWeight = SecondaryTextWeight,
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
    }
}
