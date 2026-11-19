package com.sweetcalendar.ui.habit

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.data.appearsOn
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.AnimatedTaskTitle
import com.sweetcalendar.ui.components.BackArrowMiniIcon
import com.sweetcalendar.ui.components.PenMiniIcon
import com.sweetcalendar.ui.components.SweetIconButton
import com.sweetcalendar.ui.components.TaskCardPriorityMarker
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.TrashIcon
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

private val DeleteLinkColor = Color(0xFFD9455E)
private const val DETAIL_ACTION_BTN_GAP = 8

@Composable
private fun completeButtonShadowColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.shadowDeep else Color(0xFF3C9679)
}

@Composable
fun HabitDetailScreen(
    habit: HabitWithDays,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleEnabled: () -> Unit,
    onDelete: () -> Unit,
    isTodayDone: Boolean = false,
    onToggleTodayComplete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(16))
    val today = remember { JalaliDate.today() }
    val appearsToday = remember(habit, today) { habit.appearsOn(today) }
    val showTodayComplete = appearsToday && onToggleTodayComplete != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(16),
                    bottom = mockupDp(8),
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SweetIconButton(onClick = onBack) {
                BackArrowMiniIcon(size = mockupDp(14), color = colors.purpleDeep)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(mockupDp(DETAIL_ACTION_BTN_GAP)),
            ) {
                SweetIconButton(onClick = onEdit) {
                    PenMiniIcon(
                        size = mockupDp(MockupDimens.SETTINGS_PROFILE_EDIT_ICON),
                        color = colors.purpleDeep,
                    )
                }
                SweetIconButton(onClick = onDelete) {
                    TrashIcon(
                        width = mockupDp(MockupDimens.TRASH_ICON_W),
                        height = mockupDp(MockupDimens.TRASH_ICON_H),
                        color = DeleteLinkColor,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = mockupDp(18))
        ) {
            val category = HabitCategory.fromString(habit.habit.category)
            val priority = TaskPriority.fromString(habit.habit.priority)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(12)),
            ) {
                Box(
                    modifier = Modifier
                        .size(mockupDp(44))
                        .clip(RoundedCornerShape(mockupDp(12)))
                        .background(category.accentColor().copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    HabitCategoryIcon(category = category, size = mockupDp(22))
                }
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedTaskTitle(
                        text = AnnotatedString(habit.name),
                        isDone = isTodayDone && appearsToday,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(24f),
                            lineHeight = mockupSp(30f),
                        ),
                        inkColor = colors.ink,
                        mutedColor = colors.muted,
                        maxLines = 3,
                    )
                    Text(
                        buildString {
                            append(category.label)
                            append(" · ")
                            append(priority.label)
                            append(" · ")
                            append(if (habit.isEnabled) "Enabled · repeats every week" else "Paused")
                        },
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(13f),
                        ),
                        color = if (habit.isEnabled) colors.muted else colors.pinkText,
                        modifier = Modifier.padding(top = mockupDp(4)),
                    )
                }
                TaskCardPriorityMarker(priority = priority)
            }

            Spacer(Modifier.height(mockupDp(20)))
            Text(
                "SCHEDULE",
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.SECTION_LABEL_F),
                ),
                color = colors.purpleDeep,
            )
            Spacer(Modifier.height(mockupDp(10)))

            habit.days.sortedBy { it.weekdayIndex }.forEach { day ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(8))
                ) {
                    Box(
                        Modifier
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
                            .padding(horizontal = mockupDp(14), vertical = mockupDp(14)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            JalaliDate.WEEKDAY_NAMES_EN[day.weekdayIndex],
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(15f),
                            ),
                            color = colors.ink,
                        )
                        Text(
                            day.reminderTime?.let { formatTime12h(it) } ?: "No reminder",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = SecondaryTextWeight,
                                fontSize = mockupSp(13f),
                            ),
                            color = colors.muted,
                        )
                    }
                }
            }

            Spacer(Modifier.height(mockupDp(16)))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(8),
                    bottom = mockupDp(24),
                ),
            verticalArrangement = Arrangement.spacedBy(mockupDp(12)),
        ) {
            if (showTodayComplete) {
                if (!isTodayDone) {
                    HabitCompleteButton(
                        text = "COMPLETE TODAY'S HABIT",
                        onClick = onToggleTodayComplete!!,
                    )
                } else {
                    HabitSecondaryButton(
                        text = "UNDO TODAY'S HABIT",
                        onClick = onToggleTodayComplete!!,
                    )
                }
            }
            HabitSecondaryButton(
                text = if (habit.isEnabled) "PAUSE HABIT" else "ENABLE HABIT",
                onClick = onToggleEnabled,
            )
        }
    }
}

@Composable
private fun HabitCompleteButton(
    text: String,
    onClick: () -> Unit,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.COMPLETE_BTN_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.COMPLETE_BTN_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(completeButtonShadowColor())
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.mintDeep)
                .clickable(onClick = onClick)
                .padding(vertical = mockupDp(MockupDimens.COMPLETE_BTN_PAD_V)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f),
                ),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun HabitSecondaryButton(
    text: String,
    onClick: () -> Unit,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.COMPLETE_BTN_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .clickable(onClick = onClick)
                .padding(vertical = mockupDp(MockupDimens.COMPLETE_BTN_PAD_V)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f),
                ),
                color = colors.purpleDeep,
            )
        }
    }
}
