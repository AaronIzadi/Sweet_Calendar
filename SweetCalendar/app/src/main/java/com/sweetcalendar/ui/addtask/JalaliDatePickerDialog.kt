package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.repository.EventRepository

private const val JALALI_PICKER_MIN_YEAR = 1300

private fun JalaliDate.coerceDayInMonth(year: Int, month: Int): JalaliDate {
    val maxDay = JalaliDate(year, month, 1).daysInMonth()
    return JalaliDate(year, month, day.coerceIn(1, maxDay))
}

@Composable
fun JalaliDatePickerDialog(
    initialDate: JalaliDate,
    eventRepository: EventRepository,
    onDismiss: () -> Unit,
    onConfirm: (JalaliDate) -> Unit,
) {
    var pane by remember { mutableStateOf(DatePickerPane.Day) }
    var visibleMonth by remember(initialDate) { mutableStateOf(initialDate.firstOfMonth()) }
    var selected by remember(initialDate) { mutableStateOf(initialDate) }
    val today = remember { JalaliDate.today() }
    val maxYear = today.year
    var yearPageStart by remember(initialDate) {
        mutableIntStateOf(yearPageStartFor(initialDate.year, maxYear))
    }

    val events by eventRepository
        .observeForMonth(visibleMonth.year, visibleMonth.month)
        .collectAsState(initial = emptyList())
    val holidayKeys = remember(events) { officialHolidayDateKeys(events) }

    LaunchedEffect(visibleMonth.year, visibleMonth.month) {
        eventRepository.ensureMonthCached(visibleMonth.year, visibleMonth.month)
    }

    val yearPageYears = remember(yearPageStart, maxYear) {
        (yearPageStart..minOf(yearPageStart + DATE_PICKER_YEAR_PAGE_SIZE - 1, maxYear)).toList()
    }

    DatePickerDialogShell(
        pane = pane,
        monthName = JalaliDate.MONTH_NAMES_EN[visibleMonth.month - 1],
        year = visibleMonth.year,
        yearPageLabel = "${yearPageYears.first()} – ${yearPageYears.last()}",
        onMonthClick = { pane = DatePickerPane.Month },
        onYearClick = {
            yearPageStart = yearPageStartFor(visibleMonth.year, maxYear)
            pane = DatePickerPane.Year
        },
        onPrev = {
            when (pane) {
                DatePickerPane.Day -> visibleMonth = visibleMonth.plusMonths(-1)
                DatePickerPane.Month -> {
                    val y = (visibleMonth.year - 1).coerceAtLeast(JALALI_PICKER_MIN_YEAR)
                    visibleMonth = JalaliDate(y, visibleMonth.month, 1)
                    selected = selected.coerceDayInMonth(y, selected.month)
                }
                DatePickerPane.Year -> {
                    yearPageStart = (yearPageStart - DATE_PICKER_YEAR_PAGE_SIZE)
                        .coerceAtLeast(JALALI_PICKER_MIN_YEAR)
                }
            }
        },
        onNext = {
            when (pane) {
                DatePickerPane.Day -> visibleMonth = visibleMonth.plusMonths(1)
                DatePickerPane.Month -> {
                    val y = (visibleMonth.year + 1).coerceAtMost(maxYear)
                    visibleMonth = JalaliDate(y, visibleMonth.month, 1)
                    selected = selected.coerceDayInMonth(y, selected.month)
                }
                DatePickerPane.Year -> {
                    val maxStart = maxYear - DATE_PICKER_YEAR_PAGE_SIZE + 1
                    yearPageStart = (yearPageStart + DATE_PICKER_YEAR_PAGE_SIZE)
                        .coerceAtMost(maxStart.coerceAtLeast(JALALI_PICKER_MIN_YEAR))
                }
            }
        },
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected) },
        dayCalendar = {
            val leadingBlanks = visibleMonth.firstOfMonth().weekdayIndex()
            val daysInMonth = visibleMonth.daysInMonth()
            val cells = buildList {
                repeat(leadingBlanks) { add(null) }
                for (d in 1..daysInMonth) {
                    add(JalaliDate(visibleMonth.year, visibleMonth.month, d))
                }
            }
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val dayKey = jalaliEventDateKey(date)
                            DatePickerDayCell(
                                modifier = Modifier.weight(1f),
                                dayLabel = date.day.toString(),
                                isSelected = date == selected,
                                isToday = date == today,
                                isOfficialHoliday = holidayKeys.contains(dayKey),
                                isWeekend = date.weekdayIndex() == 6,
                                onClick = {
                                    selected = date
                                    visibleMonth = date.firstOfMonth()
                                },
                            )
                        }
                    }
                    repeat(7 - week.size) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        },
        monthPicker = {
            DatePickerMonthGrid(
                monthLabels = JalaliDate.MONTH_NAMES_EN,
                selectedMonth = visibleMonth.month,
                onMonthSelected = { month ->
                    visibleMonth = JalaliDate(visibleMonth.year, month, 1)
                    selected = selected.coerceDayInMonth(visibleMonth.year, month)
                    pane = DatePickerPane.Day
                },
            )
        },
        yearPicker = {
            DatePickerYearGrid(
                years = yearPageYears,
                selectedYear = visibleMonth.year,
                onYearSelected = { year ->
                    visibleMonth = JalaliDate(year, visibleMonth.month, 1)
                    selected = selected.coerceDayInMonth(year, selected.month)
                    pane = DatePickerPane.Month
                },
            )
        },
    )
}

private fun yearPageStartFor(selectedYear: Int, maxYear: Int): Int {
    val maxStart = maxOf(JALALI_PICKER_MIN_YEAR, maxYear - DATE_PICKER_YEAR_PAGE_SIZE + 1)
    val centered = selectedYear - DATE_PICKER_YEAR_PAGE_SIZE / 2
    return centered.coerceIn(JALALI_PICKER_MIN_YEAR, maxStart)
}
