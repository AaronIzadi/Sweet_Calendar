package com.sweetcalendar.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.sweetcalendar.data.HabitOccurrence
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.weekDayLabels
import com.sweetcalendar.ui.components.AnimatedTaskTitle
import com.sweetcalendar.ui.components.TaskCardPriorityMarker
import com.sweetcalendar.ui.components.TaskCompleteCheckIcon
import com.sweetcalendar.ui.components.TaskCompletionPop
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.motion.pressScale
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MintGreen
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
fun SweetHabitCard(
    habit: HabitWithDays,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** When set, shows that day's reminder time instead of the weekly day labels. */
    occurrence: HabitOccurrence? = null,
    onToggleComplete: (() -> Unit)? = null,
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(14)
    val category = HabitCategory.fromString(habit.habit.category)
    val priority = TaskPriority.fromString(habit.habit.priority)
    val accent = category.accentColor()
    val interaction = remember { MutableInteractionSource() }
    val isDone = occurrence?.isDone == true
    val alpha = when {
        isDone -> 0.58f
        habit.isEnabled -> 1f
        else -> 0.58f
    }
    val meta = when {
        occurrence != null -> occurrence.reminderTime?.let { formatTime12h(it) }
        else -> habit.weekDayLabels()
    }
    val metaLine = buildList {
        meta?.takeIf { it.isNotBlank() }?.let { add(it) }
        add(category.label)
    }.joinToString(" · ")
    val taskIcon = mockupDp(MockupDimens.TASK_ICON)
    val completionKey = occurrence?.let { it.id xor it.date.formatIso().hashCode().toLong() } ?: habit.id
    var wasDone by remember(completionKey) { mutableStateOf(isDone) }
    val popCheck = isDone && (!wasDone || TaskCompletionPop.consumePop(completionKey))
    LaunchedEffect(isDone) { wasDone = isDone }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = mockupDp(10))
            .alpha(alpha)
    ) {
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
                .clickable(
                    onClick = onClick,
                    interactionSource = interaction,
                    indication = null,
                )
                .pressScale(interaction),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = mockupDp(MockupDimens.TASK_ACCENT_W), height = mockupDp(56))
                    .background(accent)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = mockupDp(0))
                    .padding(
                        horizontal = mockupDp(MockupDimens.TASK_CARD_PAD_H),
                        vertical = mockupDp(MockupDimens.TASK_CARD_PAD_V),
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
            ) {
                when {
                    isDone && onToggleComplete != null -> {
                        TaskCompleteCheckIcon(
                            size = taskIcon,
                            bgColor = if (colors.isDark) colors.mint else MintGreen,
                            pop = popCheck,
                            onClick = onToggleComplete,
                        )
                    }
                    onToggleComplete != null -> {
                        Box(
                            modifier = Modifier.clickable(
                                onClick = {
                                    TaskCompletionPop.schedulePop(completionKey)
                                    onToggleComplete()
                                },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ),
                        ) {
                            HabitCategoryIcon(category = category, size = taskIcon)
                        }
                    }
                    else -> {
                        HabitCategoryIcon(category = category, size = taskIcon)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(mockupDp(2)),
                ) {
                    AnimatedTaskTitle(
                        text = AnnotatedString(habit.name),
                        isDone = isDone,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(15f),
                            lineHeight = mockupSp(19f),
                        ),
                        inkColor = colors.ink,
                        mutedColor = colors.muted,
                        maxLines = 2,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            metaLine,
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = SecondaryTextWeight,
                                fontSize = mockupSp(12f),
                            ),
                            color = colors.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (!habit.isEnabled) {
                            Text(
                                "Paused",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(11f),
                                ),
                                color = colors.pinkText,
                            )
                        }
                    }
                }
                if (!isDone) {
                    TaskCardPriorityMarker(priority = priority)
                }
            }
        }
    }
}
