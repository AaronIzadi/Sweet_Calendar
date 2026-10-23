package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.sweetcalendar.ui.components.ClockMiniIcon
import com.sweetcalendar.ui.components.DialogKeyboardInsets
import com.sweetcalendar.ui.components.SweetDialog
import com.sweetcalendar.ui.components.showKeyboardOnFocus
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
import com.sweetcalendar.ui.theme.formChipFillColor
import com.sweetcalendar.ui.theme.formChipShadowColor
import com.sweetcalendar.ui.theme.formChipTextColor

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
    var hourInput by remember(reminderTime) { mutableStateOf(initialHour12.toString()) }
    var minuteInput by remember(reminderTime) { mutableStateOf("%02d".format(initialMinute)) }
    var isPm by remember(reminderTime) { mutableStateOf(initialPm) }

    fun resolvedHour12(): Int =
        hourInput.filter(Char::isDigit).toIntOrNull()?.coerceIn(1, 12) ?: hour12

    fun resolvedMinute(): Int =
        minuteInput.filter(Char::isDigit).toIntOrNull()?.coerceIn(0, 59) ?: minute

    val preview24 = remember(hourInput, minuteInput, isPm, hour12, minute) {
        formatTime24(toHour24(resolvedHour12(), isPm), resolvedMinute())
    }

    fun confirmTime() {
        val h = resolvedHour12()
        val m = resolvedMinute()
        hour12 = h
        minute = m
        hourInput = h.toString()
        minuteInput = "%02d".format(m)
        onConfirm(formatTime24(toHour24(h, isPm), m))
    }

    SweetDialog(onDismissRequest = onDismiss) {
        DialogKeyboardInsets()
        ProvideMockupScale {
            SweetDialogSurface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
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
                        TimeEditableColumn(
                            label = "HOUR",
                            value = hourInput,
                            onValueChange = { raw ->
                                val digits = raw.filter(Char::isDigit).take(2)
                                hourInput = digits
                                digits.toIntOrNull()?.let { hour12 = it.coerceIn(1, 12) }
                            },
                            onIncrement = {
                                hour12 = if (hour12 >= 12) 1 else hour12 + 1
                                hourInput = hour12.toString()
                            },
                            onDecrement = {
                                hour12 = if (hour12 <= 1) 12 else hour12 - 1
                                hourInput = hour12.toString()
                            },
                            maxLength = 2,
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
                        TimeEditableColumn(
                            label = "MIN",
                            value = minuteInput,
                            onValueChange = { raw ->
                                val digits = raw.filter(Char::isDigit).take(2)
                                minuteInput = digits
                                digits.toIntOrNull()?.let { minute = it.coerceIn(0, 59) }
                            },
                            onIncrement = {
                                minute = if (minute >= 59) 0 else minute + 1
                                minuteInput = "%02d".format(minute)
                            },
                            onDecrement = {
                                minute = if (minute <= 0) 59 else minute - 1
                                minuteInput = "%02d".format(minute)
                            },
                            maxLength = 2,
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
                    closeLabel = "DISCARD",
                    trailing = {
                        SheetHeaderConfirmAction(
                            label = "OK",
                            onClick = { confirmTime() }
                        )
                    }
                )
            }
            }
        }
    }
}

@Composable
private fun TimeEditableColumn(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val fieldStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F + 4f),
        lineHeight = mockupSp(22f),
        textAlign = TextAlign.Center,
    )
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
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
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                    .clip(RoundedCornerShape(radius))
                    .background(colors.shadow)
            )
            BasicTextField(
                value = value,
                onValueChange = { raw ->
                    onValueChange(raw.filter(Char::isDigit).take(maxLength))
                },
                singleLine = true,
                textStyle = fieldStyle.copy(color = colors.ink),
                cursorBrush = SolidColor(colors.pinkText),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .showKeyboardOnFocus()
                    .clip(RoundedCornerShape(radius))
                    .background(colors.paper)
                    .padding(vertical = mockupDp(14), horizontal = mockupDp(8)),
            )
        }
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
private fun PeriodChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                .background(formChipShadowColor(selected))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(formChipFillColor(selected))
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
                color = formChipTextColor(selected)
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
