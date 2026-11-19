package com.sweetcalendar.ui.habit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.addtask.TaskPriorityChip
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.ClosePixelIcon
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.formChipFillColor
import com.sweetcalendar.ui.theme.formChipShadowColor
import com.sweetcalendar.ui.theme.formChipShadowLayer
import com.sweetcalendar.ui.theme.formChipTextColor
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.rememberReducedMotion

/**
 * Habit-only fields rendered inside the shared activity sheet.
 * [titleField] / [reminderField] are provided by the host so chrome matches Task/To-do.
 */
@Composable
fun HabitSheetFields(
    category: HabitCategory,
    onCategoryChange: (HabitCategory) -> Unit,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    dayReminders: Map<Int, String?>,
    onDayRemindersChange: (Map<Int, String?>) -> Unit,
    weekStartsOn: Int,
    showDaysError: Boolean,
    titleField: @Composable () -> Unit,
    reminderField: @Composable (
        value: String,
        onClick: () -> Unit,
        trailing: (@Composable () -> Unit)?,
    ) -> Unit,
    onPickReminderTime: (weekdayIndex: Int) -> Unit,
    fieldLabel: @Composable (String) -> Unit,
    kindChip: @Composable (
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier,
    ) -> Unit,
) {
    val colors = SweetTheme.colors
    val reducedMotion = rememberReducedMotion()

    Column(modifier = Modifier.fillMaxWidth()) {
        titleField()

        fieldLabel("CATEGORY")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
        ) {
            HabitCategory.entries.forEach { cat ->
                HabitCategorySwatch(
                    category = cat,
                    selected = category == cat,
                    onClick = { onCategoryChange(cat) },
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(min = mockupDp(0)),
                )
            }
        }

        fieldLabel("PRIORITY")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
        ) {
            TaskPriority.entries.forEach { p ->
                TaskPriorityChip(
                    priority = p,
                    selected = priority == p,
                    onClick = { onPriorityChange(p) },
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(min = mockupDp(0)),
                )
            }
        }

        fieldLabel("DAYS · EVERY WEEK")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(mockupDp(6)),
        ) {
            val ordered = (0..6).map { (weekStartsOn + it) % 7 }
            ordered.forEach { dayIndex ->
                kindChip(
                    JalaliDate.WEEKDAY_NAMES_EN_SHORT[dayIndex],
                    dayIndex in dayReminders,
                    {
                        onDayRemindersChange(
                            if (dayIndex in dayReminders) {
                                dayReminders - dayIndex
                            } else {
                                dayReminders + (dayIndex to "08:00")
                            }
                        )
                    },
                    Modifier
                        .weight(1f)
                        .widthIn(min = mockupDp(0)),
                )
            }
        }
        AnimatedVisibility(
            visible = showDaysError,
            enter = fadeIn(SweetMotion.shortTween()) + expandVertically(SweetMotion.shortTween()),
            exit = fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween()),
        ) {
            Text(
                "Pick at least one day",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = SecondaryTextWeight,
                    fontSize = mockupSp(12f),
                ),
                color = colors.pinkText,
                modifier = Modifier.padding(top = mockupDp(4)),
            )
        }

        AnimatedVisibility(
            visible = dayReminders.isNotEmpty(),
            enter = if (reducedMotion) {
                fadeIn(SweetMotion.shortTween())
            } else {
                fadeIn(SweetMotion.mediumTween()) + expandVertically(SweetMotion.mediumTween())
            },
            exit = if (reducedMotion) {
                fadeOut(SweetMotion.shortTween())
            } else {
                fadeOut(SweetMotion.shortTween()) + shrinkVertically(SweetMotion.shortTween())
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = if (reducedMotion) {
                            SweetMotion.shortTween()
                        } else {
                            spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            )
                        },
                    ),
            ) {
                fieldLabel("REMINDER")
                val ordered = (0..6).map { (weekStartsOn + it) % 7 }
                    .filter { it in dayReminders }
                ordered.forEach { weekday ->
                    key(weekday) {
                        val time = dayReminders[weekday]
                        val enabled = time != null
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = mockupDp(8)),
                        ) {
                            Text(
                                JalaliDate.WEEKDAY_NAMES_EN[weekday],
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(13f),
                                ),
                                color = colors.ink,
                                modifier = Modifier.padding(bottom = mockupDp(6)),
                            )
                            reminderField(
                                if (enabled) formatTime12h(time!!) else "Add a reminder",
                                {
                                    if (enabled) onPickReminderTime(weekday)
                                    else onDayRemindersChange(dayReminders + (weekday to "08:00"))
                                },
                                if (enabled) {
                                    {
                                        ClosePixelIcon(
                                            size = mockupDp(MockupDimens.SHEET_HEADER_ICON),
                                            color = colors.muted,
                                            modifier = Modifier.clickable {
                                                onDayRemindersChange(dayReminders + (weekday to null))
                                            },
                                        )
                                    }
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCategorySwatch(
    category: HabitCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconSize = mockupDp(MockupDimens.SWATCH_ICON)
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    y = if (selected) mockupDp(3) else mockupDp(MockupDimens.FORM_FIELD_SHADOW)
                )
                .formChipShadowLayer(formChipShadowColor(selected), radius)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .clickable(onClick = onClick)
                .background(formChipFillColor(selected))
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mockupDp(6)),
        ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                HabitCategoryIcon(category = category, size = iconSize)
            }
            Text(
                category.label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F),
                ),
                color = if (selected) formChipTextColor(selected) else SweetTheme.colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
