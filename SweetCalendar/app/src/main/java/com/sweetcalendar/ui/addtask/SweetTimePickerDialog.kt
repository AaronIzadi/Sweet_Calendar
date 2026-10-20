package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.SweetDialogSurface
import com.sweetcalendar.ui.components.SheetFooterBar
import com.sweetcalendar.ui.components.SheetHeaderConfirmAction
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.components.formatTime12h
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.selectedOnAccentTextColor

@Composable
fun SweetTimePickerDialog(
    reminderTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val colors = SweetTheme.colors
    val (initialHour24, initialMinute) = remember(reminderTime) { parseTime24(reminderTime) }
    val (initialHour12, initialPm) = remember(initialHour24) { hour24To12(initialHour24) }

    var hour12 by remember(reminderTime) { mutableIntStateOf(initialHour12) }
    var minute by remember(reminderTime) { mutableIntStateOf(initialMinute) }
    var isPm by remember(reminderTime) { mutableStateOf(initialPm) }

    val preview24 = remember(hour12, minute, isPm) {
        formatTime24(toHour24(hour12, isPm), minute)
    }

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
                    "Set time",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(sheetDialogTitlePaddingModifier())
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            bottom = mockupDp(8)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(8))
                    ) {
                        ClockMiniIcon(
                            size = mockupDp(MockupDimens.SWATCH_ICON),
                            color = colors.purpleDeep
                        )
                        Text(
                            formatTime12h(preview24),
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(22f),
                                lineHeight = mockupSp(26f)
                            ),
                            color = colors.ink
                        )
                    }

                    Spacer(Modifier.size(mockupDp(16)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(10)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeStepperColumn(
                            label = "HOUR",
                            valueText = hour12.toString(),
                            onIncrement = { hour12 = if (hour12 >= 12) 1 else hour12 + 1 },
                            onDecrement = { hour12 = if (hour12 <= 1) 12 else hour12 - 1 },
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            ":",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(20f)
                            ),
                            color = colors.muted,
                            modifier = Modifier.padding(bottom = mockupDp(20))
                        )
                        TimeStepperColumn(
                            label = "MIN",
                            valueText = "%02d".format(minute),
                            onIncrement = { minute = if (minute >= 59) 0 else minute + 1 },
                            onDecrement = { minute = if (minute <= 0) 59 else minute - 1 },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.size(mockupDp(14)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
                    ) {
                        PeriodChip(
                            label = "AM",
                            selected = !isPm,
                            onClick = { isPm = false },
                            modifier = Modifier.weight(1f)
                        )
                        PeriodChip(
                            label = "PM",
                            selected = isPm,
                            onClick = { isPm = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SheetFooterBar(
                    onClose = onDismiss,
                    trailing = {
                        SheetHeaderConfirmAction(
                            label = "OK",
                            onClick = { onConfirm(preview24) }
                        )
                    }
                )
            }
            }
        }
    }
}

@Composable
private fun TimeStepperColumn(
    label: String,
    valueText: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.FIELD_LABEL),
                letterSpacing = mockupSp(0.3f)
            ),
            color = colors.muted,
            modifier = Modifier.padding(bottom = mockupDp(6))
        )
        StepperChevron(text = "▲", onClick = onIncrement)
        Spacer(Modifier.size(mockupDp(6)))
        TimeValueSurface(valueText = valueText)
        Spacer(Modifier.size(mockupDp(6)))
        StepperChevron(text = "▼", onClick = onDecrement)
    }
}

@Composable
private fun StepperChevron(text: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    Text(
        text,
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(12f),
            lineHeight = mockupSp(12f)
        ),
        color = colors.purpleDeep,
        modifier = Modifier
            .clip(RoundedCornerShape(mockupDp(8)))
            .clickable(onClick = onClick)
            .padding(horizontal = mockupDp(16), vertical = mockupDp(6))
    )
}

@Composable
private fun TimeValueSurface(valueText: String) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(vertical = mockupDp(14)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                valueText,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.FIELD_TEXT_F + 4f),
                    lineHeight = mockupSp(22f)
                ),
                color = colors.ink,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PeriodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    y = if (selected) mockupDp(3) else mockupDp(MockupDimens.FORM_FIELD_SHADOW)
                )
                .clip(RoundedCornerShape(radius))
                .background(if (selected) colors.shadowDeep else colors.shadow)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(if (selected) colors.purple else colors.paper)
                .padding(vertical = mockupDp(12)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F)
                ),
                color = if (selected) selectedOnAccentTextColor() else colors.muted
            )
        }
    }
}

private fun parseTime24(time: String): Pair<Int, Int> {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 14
    val minute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 30
    return hour to minute
}

private fun hour24To12(hour24: Int): Pair<Int, Boolean> {
    val isPm = hour24 >= 12
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    return hour12 to isPm
}

private fun toHour24(hour12: Int, isPm: Boolean): Int = when {
    isPm && hour12 != 12 -> hour12 + 12
    !isPm && hour12 == 12 -> 0
    else -> hour12
}

private fun formatTime24(hour24: Int, minute: Int): String =
    "%02d:%02d".format(hour24, minute)
