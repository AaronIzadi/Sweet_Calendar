package com.sweetcalendar.ui.taskdetail

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.recurrence.repeatSummary
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.CalMiniIcon
import com.sweetcalendar.ui.components.BackArrowMiniIcon
import com.sweetcalendar.ui.components.PenMiniIcon
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.RepeatMiniIcon
import com.sweetcalendar.ui.components.TrashIcon
import com.sweetcalendar.ui.components.ThemeSparkleIcon
import com.sweetcalendar.ui.components.TaskCategory
import com.sweetcalendar.ui.components.TaskBriefcaseIcon
import com.sweetcalendar.ui.components.TaskHeartIcon
import com.sweetcalendar.ui.components.TaskHomeIcon
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.SweetIconButton
import com.sweetcalendar.ui.components.RichNotesText
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

private val DeleteLinkColor = Color(0xFFD9455E)
private val WorkHeroBgLight = Color(0xFFEDE8F7)
private const val DETAIL_ACTION_BTN_GAP = 8
private const val DETAIL_PRIORITY_SPARKLE_TRACK_W = 22
private const val DETAIL_PRIORITY_SPARKLE_SIZE = 6

@Composable
private fun completeButtonShadowColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.shadowDeep else Color(0xFF3C9679)
}

@Composable
private fun detailMetaTextColor(): Color = SweetTheme.colors.muted

@Composable
private fun detailFieldLabelColor(): Color = SweetTheme.colors.muted

@Composable
private fun detailCategoryHeroBackground(category: TaskCategory): Color {
    val colors = SweetTheme.colors
    return when (category) {
        TaskCategory.Personal -> colors.navActiveBg
        TaskCategory.Home -> colors.holidayBg
        TaskCategory.Work -> if (colors.isDark) colors.streakBg else WorkHeroBgLight
    }
}

@Composable
private fun detailCategoryAccent(category: TaskCategory): Color {
    val colors = SweetTheme.colors
    return when (category) {
        TaskCategory.Personal -> colors.pinkDeep
        TaskCategory.Home -> colors.mintDeep
        TaskCategory.Work -> colors.purpleDeep
    }
}

@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val colors = SweetTheme.colors
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    val category = TaskCategory.fromString(task.category)
    val priority = TaskPriority.fromString(task.priority)
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]
    val miniIconSize = mockupDp(MockupDimens.MINI_FIELD_ICON)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        TaskDetailHero(
            title = task.title,
            isDone = task.isDone,
            category = category,
            priority = priority,
            onBack = onBack,
            onEdit = onEdit,
            onDelete = onDelete
        )

        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(16),
                        bottom = mockupDp(12)
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(mockupDp(8)),
                    modifier = Modifier.padding(bottom = mockupDp(18))
                ) {
                    MetaRow(
                        icon = {
                            CalMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                        },
                        text = "$weekday, $month ${date.day}, ${date.year}"
                    )
                    task.reminderTime?.let { time ->
                        MetaRow(
                            icon = {
                                ClockMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                            },
                            text = formatTime12h(time)
                        )
                    }
                    if (task.isDone) {
                        task.completedAt?.let { completedAt ->
                            MetaRow(
                                icon = {
                                    ClockMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                                },
                                text = formatTaskCompletedAt(completedAt),
                            )
                        }
                    } else {
                        val remaining = remember(task.jalaliDate) {
                            formatRemainingUntilTaskDayEnd(date)
                        }
                        MetaRow(
                            icon = {
                                ClockMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                            },
                            text = remaining,
                        )
                    }
                    task.repeatSummary()?.let { repeatLabel ->
                        MetaRow(
                            icon = {
                                RepeatMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                            },
                            text = repeatLabel
                        )
                    }
                }

                if (task.notes.isNotBlank()) {
                    DetailFieldLabel("NOTES")
                    NotesBox(text = task.notes)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(8),
                        bottom = mockupDp(24)
                    )
            ) {
                if (!task.isDone) {
                    CompleteButton(text = "MARK COMPLETE", onClick = onComplete)
                } else {
                    DetailSecondaryButton(text = "MARK INCOMPLETE", onClick = onComplete)
                }
            }
        }
    }
}

@Composable
private fun TaskDetailHero(
    title: String,
    isDone: Boolean,
    category: TaskCategory,
    priority: TaskPriority,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = SweetTheme.colors
    val heroBg = detailCategoryHeroBackground(category)
    val accent = detailCategoryAccent(category)
    val heroIconSize = mockupDp(44)
    val bottomRadius = mockupDp(20)
    val iconBtnSize = mockupDp(MockupDimens.DETAIL_ICON_BTN)
    val actionClusterWidth = iconBtnSize * 2 + mockupDp(DETAIL_ACTION_BTN_GAP)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = bottomRadius,
                    bottomEnd = bottomRadius
                )
            )
            .background(heroBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = mockupDp(22))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(16),
                        bottom = mockupDp(4)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                SweetIconButton(onClick = onBack) {
                    BackArrowMiniIcon(
                        size = mockupDp(14),
                        color = colors.purpleDeep
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(mockupDp(DETAIL_ACTION_BTN_GAP))
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(mockupDp(DETAIL_ACTION_BTN_GAP))) {
                        SweetIconButton(onClick = onEdit) {
                            PenMiniIcon(
                                size = mockupDp(MockupDimens.SETTINGS_PROFILE_EDIT_ICON),
                                color = colors.purpleDeep
                            )
                        }
                        SweetIconButton(onClick = onDelete) {
                            TrashIcon(
                                width = mockupDp(MockupDimens.TRASH_ICON_W),
                                height = mockupDp(MockupDimens.TRASH_ICON_H),
                                color = DeleteLinkColor
                            )
                        }
                    }
                    DetailPriorityBadge(
                        priority = priority,
                        modifier = Modifier.width(actionClusterWidth)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = mockupDp(24)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (category) {
                    TaskCategory.Personal -> TaskHeartIcon(size = heroIconSize)
                    TaskCategory.Home -> TaskHomeIcon(size = heroIconSize)
                    TaskCategory.Work -> TaskBriefcaseIcon(size = heroIconSize)
                }
                Spacer(Modifier.height(mockupDp(8)))
                Text(
                    category.label.uppercase(),
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.DETAIL_BADGE_TEXT_F),
                        lineHeight = mockupSp(13f),
                        letterSpacing = mockupSp(0.6f)
                    ),
                    color = accent
                )
                Spacer(Modifier.height(mockupDp(10)))
                Text(
                    title,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.DETAIL_TITLE),
                        lineHeight = mockupSp(26f)
                    ),
                    color = colors.ink,
                    textAlign = TextAlign.Center,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null
                )
            }
        }
    }
}

@Composable
private fun DetailPriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.DETAIL_ICON_BTN_RADIUS)
    val height = mockupDp(MockupDimens.DETAIL_ICON_BTN)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow)
        )
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(4)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DetailPrioritySparkles(priority = priority)
            Text(
                priority.label.uppercase(),
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(9f),
                    lineHeight = mockupSp(11f),
                    letterSpacing = mockupSp(0.2f)
                ),
                color = colors.purpleDeep,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = mockupDp(0))
            )
        }
    }
}

@Composable
private fun DetailPrioritySparkles(priority: TaskPriority) {
    val sparkleSize = mockupDp(DETAIL_PRIORITY_SPARKLE_SIZE)
    val gap = mockupDp(1)
    Box(
        modifier = Modifier.width(mockupDp(DETAIL_PRIORITY_SPARKLE_TRACK_W)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(priority.sparkleCount) {
                ThemeSparkleIcon(size = sparkleSize)
            }
        }
    }
}

@Composable
private fun DetailFieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = BodyFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            letterSpacing = mockupSp(0.3f),
            lineHeight = mockupSp(14f)
        ),
        color = detailFieldLabelColor(),
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6))
    )
}

@Composable
private fun NotesBox(text: String) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow)
        )
        RichNotesText(
            text = text,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = SecondaryTextWeight,
                fontSize = mockupSp(MockupDimens.DETAIL_NOTES_F),
                lineHeight = mockupSp(20f)
            ),
            color = detailMetaTextColor(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12))
        )
    }
}

@Composable
private fun CompleteButton(text: String, onClick: () -> Unit) {
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
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f)
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun DetailSecondaryButton(text: String, onClick: () -> Unit) {
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
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f)
                ),
                color = colors.purpleDeep
            )
        }
    }
}

@Composable
private fun MetaRow(icon: @Composable () -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
    ) {
        icon()
        Text(
            text,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = SecondaryTextWeight,
                fontSize = mockupSp(MockupDimens.DETAIL_META_F),
                lineHeight = mockupSp(17f)
            ),
            color = detailMetaTextColor()
        )
    }
}
