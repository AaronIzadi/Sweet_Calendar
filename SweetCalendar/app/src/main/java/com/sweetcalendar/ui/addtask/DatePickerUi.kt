package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sweetcalendar.data.local.EventCacheEntity
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.MonthNavChevron
import com.sweetcalendar.ui.components.SheetFooterBar
import com.sweetcalendar.ui.components.SheetHeaderConfirmAction
import com.sweetcalendar.ui.components.SweetDialog
import com.sweetcalendar.ui.components.SweetDialogSurface
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.SweetColors
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.theme.selectedOnAccentTextColor

/** Canonical Jalali day key for event cache / task storage (yyyy-MM-dd). */
internal fun jalaliEventDateKey(date: JalaliDate): String =
    "%04d-%02d-%02d".format(date.year, date.month, date.day)

internal fun normalizeEventDateKey(raw: String): String {
    val parsed = JalaliDate.parseIso(raw)
    return jalaliEventDateKey(parsed)
}

internal fun officialHolidayDateKeys(events: List<EventCacheEntity>): Set<String> =
    events
        .asSequence()
        .filter { it.isHoliday && it.description.isNotBlank() }
        .map { normalizeEventDateKey(it.jalaliDate) }
        .toSet()

internal fun datePickerCellBackground(
    colors: SweetColors,
    isSelected: Boolean,
    isToday: Boolean,
    isOfficialHoliday: Boolean,
    isWeekend: Boolean,
): Color = when {
    isSelected -> colors.purple
    isToday -> colors.pink.copy(alpha = 0.35f)
    isOfficialHoliday -> colors.holidayBg
    isWeekend -> colors.weekendBg
    else -> Color.Transparent
}

@Composable
internal fun DatePickerDialogShell(
    monthLabel: String,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    calendar: @Composable ColumnScope.() -> Unit,
) {
    val colors = SweetTheme.colors
    SweetDialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            SweetDialogSurface(
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(mockupDp(16)))
                        .background(colors.paper)
                ) {
                    Text(
                        "Pick a date",
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
                                bottom = mockupDp(12),
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonthNavChevron(
                                pointingLeft = true,
                                onClick = onPrevMonth,
                            )
                            Text(
                                monthLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = colors.ink
                            )
                            MonthNavChevron(
                                pointingLeft = false,
                                onClick = onNextMonth,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 4.dp)
                        ) {
                            JalaliDate.WEEKDAY_NAMES_EN_SHORT.forEach { label ->
                                Text(
                                    label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.muted,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        calendar()
                    }

                    SheetFooterBar(
                        onClose = onDismiss,
                        closeLabel = "Close",
                        trailing = {
                            SheetHeaderConfirmAction(
                                label = "OK",
                                onClick = onConfirm,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun DatePickerDayCell(
    dayLabel: String,
    isSelected: Boolean,
    isToday: Boolean,
    isOfficialHoliday: Boolean,
    isWeekend: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                datePickerCellBackground(
                    colors = colors,
                    isSelected = isSelected,
                    isToday = isToday,
                    isOfficialHoliday = isOfficialHoliday,
                    isWeekend = isWeekend,
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            dayLabel,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                selectedOnAccentTextColor()
            } else {
                colors.ink
            }
        )
    }
}
