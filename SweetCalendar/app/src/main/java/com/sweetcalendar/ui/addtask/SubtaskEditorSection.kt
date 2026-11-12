package com.sweetcalendar.ui.addtask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.CalMiniIcon
import com.sweetcalendar.ui.components.ClosePixelIcon
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.formatDisplayShort
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.formPlaceholderColor
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlinx.coroutines.delay

@Composable
fun SubtaskEditorSection(
    subtasks: List<SubtaskFormItem>,
    onChange: (List<SubtaskFormItem>) -> Unit,
    modifier: Modifier = Modifier,
    todoSubtaskDeadlines: Boolean = false,
    parentDeadline: JalaliDate = JalaliDate.today(),
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onPickSubtaskDeadline: (index: Int) -> Unit = {},
) {
    val colors = SweetTheme.colors
    val reducedMotion = rememberReducedMotion()
    Column(
        modifier = modifier
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
        Text(
            "SUBTASKS",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.FIELD_LABEL),
                letterSpacing = mockupSp(0.3f),
                lineHeight = mockupSp(14f),
            ),
            color = colors.muted,
            modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6)),
        )
        subtasks.forEach { item ->
            key(item.stableKey()) {
                AnimatedFormListItem(
                    reducedMotion = reducedMotion,
                    onRemoved = {
                        onChange(subtasks.filter { it.stableKey() != item.stableKey() })
                    },
                ) { requestRemove ->
                    SubtaskDashedFrame {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                        ) {
                            SubtaskTitleField(
                                value = item.title,
                                onValueChange = { text ->
                                    val next = subtasks.map {
                                        if (it.stableKey() == item.stableKey()) it.copy(title = text) else it
                                    }
                                    onChange(next)
                                },
                                placeholder = "Subtask title",
                                modifier = Modifier.weight(1f),
                            )
                            ClosePixelIcon(
                                size = mockupDp(MockupDimens.SHEET_HEADER_ICON),
                                color = colors.muted,
                                modifier = Modifier
                                    .clickable(onClick = requestRemove)
                                    .padding(mockupDp(4)),
                            )
                        }
                        if (todoSubtaskDeadlines) {
                            Text(
                                "DEADLINE",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(MockupDimens.FIELD_LABEL),
                                    letterSpacing = mockupSp(0.3f),
                                    lineHeight = mockupSp(14f),
                                ),
                                color = colors.muted,
                                modifier = Modifier.padding(top = mockupDp(10), bottom = mockupDp(6)),
                            )
                            SubtaskDeadlineField(
                                label = item.displayDeadline(parentDeadline).formatDisplayShort(calendarSystem),
                                onClick = {
                                    onPickSubtaskDeadline(
                                        subtasks.indexOfFirst { it.stableKey() == item.stableKey() },
                                    )
                                },
                            )
                        }
                        Text(
                            "PRIORITY",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.FIELD_LABEL),
                                letterSpacing = mockupSp(0.3f),
                                lineHeight = mockupSp(14f),
                            ),
                            color = colors.muted,
                            modifier = Modifier.padding(top = mockupDp(10), bottom = mockupDp(6)),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                        ) {
                            TaskPriority.entries.forEach { priority ->
                                TaskPriorityChip(
                                    priority = priority,
                                    selected = TaskPriority.fromString(item.priority) == priority,
                                    onClick = {
                                        val next = subtasks.map {
                                            if (it.stableKey() == item.stableKey()) {
                                                it.copy(priority = priority.label)
                                            } else {
                                                it
                                            }
                                        }
                                        onChange(next)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .widthIn(min = mockupDp(0)),
                                )
                            }
                        }
                    }
                }
            }
        }
        Text(
            "+ Add subtask",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
            ),
            color = colors.purpleDeep,
            modifier = Modifier
                .clickable {
                    onChange(
                        subtasks + SubtaskFormItem(
                            title = "",
                            priority = TaskPriority.Medium.label,
                            sortOrder = subtasks.size,
                            jalaliDate = if (todoSubtaskDeadlines) {
                                parentDeadline.formatIso()
                            } else {
                                null
                            },
                            clientKey = System.nanoTime(),
                        ),
                    )
                }
                .padding(top = mockupDp(2)),
        )
    }
}

@Composable
private fun SubtaskDashedFrame(
    content: @Composable () -> Unit,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val stroke = mockupDp(1)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = mockupDp(10))
            .drawBehind {
                val strokePx = stroke.toPx()
                val half = strokePx / 2f
                drawRoundRect(
                    color = colors.fieldOutline,
                    topLeft = Offset(half, half),
                    size = Size(size.width - strokePx, size.height - strokePx),
                    cornerRadius = CornerRadius(radius.toPx()),
                    style = Stroke(
                        width = strokePx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f),
                    ),
                )
            }
            .padding(mockupDp(12)),
    ) {
        content()
    }
}

@Composable
private fun SubtaskDeadlineField(
    label: String,
    onClick: () -> Unit,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val fieldTextStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f),
        color = colors.ink,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .border(mockupDp(1), colors.fieldOutline, RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
        ) {
            CalMiniIcon(
                size = mockupDp(MockupDimens.MINI_FIELD_ICON),
                color = colors.purpleDeep,
            )
            Text(label, style = fieldTextStyle)
        }
    }
}

@Composable
private fun SubtaskTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val placeholderColor = formPlaceholderColor()
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    val fieldTextStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
        lineHeight = mockupSp(18f),
        color = colors.ink,
    )
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .border(mockupDp(1), colors.fieldOutline, RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = fieldTextStyle,
                cursorBrush = SolidColor(colors.pinkText),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            style = fieldTextStyle.copy(color = placeholderColor),
                        )
                    }
                    inner()
                },
            )
        }
    }
}
