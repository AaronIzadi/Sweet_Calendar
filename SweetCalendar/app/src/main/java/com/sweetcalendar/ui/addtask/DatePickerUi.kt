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
import androidx.compose.foundation.layout.heightIn
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

internal enum class DatePickerPane {
    Day,
    Month,
    Year,
}

internal const val DATE_PICKER_YEAR_PAGE_SIZE = 12

@Composable
internal fun DatePickerChoiceCell(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    Box(
        modifier = modifier
            .heightIn(min = mockupDp(40))
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colors.purple else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = mockupDp(8), horizontal = mockupDp(4)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) selectedOnAccentTextColor() else colors.ink,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
internal fun DatePickerMonthGrid(
    monthLabels: List<String>,
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    monthLabels.chunked(3).forEachIndexed { rowIndex, rowMonths ->
        Row(modifier = modifier.fillMaxWidth()) {
            rowMonths.forEachIndexed { colIndex, label ->
                val monthIndex = rowIndex * 3 + colIndex + 1
                DatePickerChoiceCell(
                    label = label,
                    isSelected = monthIndex == selectedMonth,
                    onClick = { onMonthSelected(monthIndex) },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(3 - rowMonths.size) {
                Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
internal fun DatePickerYearGrid(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    years.chunked(4).forEach { rowYears ->
        Row(modifier = modifier.fillMaxWidth()) {
            rowYears.forEach { year ->
                DatePickerChoiceCell(
                    label = year.toString(),
                    isSelected = year == selectedYear,
                    onClick = { onYearSelected(year) },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(4 - rowYears.size) {
                Box(Modifier.weight(1f))
            }
        }
    }
}

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
    pane: DatePickerPane,
    monthName: String,
    year: Int,
    yearPageLabel: String,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dayCalendar: @Composable ColumnScope.() -> Unit,
    monthPicker: @Composable ColumnScope.() -> Unit,
    yearPicker: @Composable ColumnScope.() -> Unit,
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
                                onClick = onPrev,
                            )
                            when (pane) {
                                DatePickerPane.Day -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(mockupDp(6)),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            monthName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = colors.ink,
                                            modifier = Modifier.clickable(onClick = onMonthClick),
                                        )
                                        Text(
                                            year.toString(),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = colors.ink,
                                            modifier = Modifier.clickable(onClick = onYearClick),
                                        )
                                    }
                                }
                                DatePickerPane.Month -> {
                                    Text(
                                        year.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.ink,
                                        modifier = Modifier.clickable(onClick = onYearClick),
                                    )
                                }
                                DatePickerPane.Year -> {
                                    Text(
                                        yearPageLabel,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.ink,
                                    )
                                }
                            }
                            MonthNavChevron(
                                pointingLeft = false,
                                onClick = onNext,
                            )
                        }

                        when (pane) {
                            DatePickerPane.Day -> {
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
                                dayCalendar()
                            }
                            DatePickerPane.Month -> {
                                Column(Modifier.padding(top = mockupDp(10))) {
                                    monthPicker()
                                }
                            }
                            DatePickerPane.Year -> {
                                Column(Modifier.padding(top = mockupDp(10))) {
                                    yearPicker()
                                }
                            }
                        }
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
