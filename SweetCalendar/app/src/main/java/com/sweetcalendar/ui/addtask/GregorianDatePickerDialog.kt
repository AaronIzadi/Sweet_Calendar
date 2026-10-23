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
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.repository.EventRepository

private const val GREGORIAN_PICKER_MIN_YEAR = 1900

private fun GregorianDate.coerceDayInMonth(year: Int, month: Int): GregorianDate {
    val maxDay = GregorianDate(year, month, 1).daysInMonth()
    return GregorianDate(year, month, day.coerceIn(1, maxDay))
}

@Composable
fun GregorianDatePickerDialog(
    initialDate: JalaliDate,
    eventRepository: EventRepository,
    onDismiss: () -> Unit,
    onConfirm: (JalaliDate) -> Unit,
) {
    val initialGregorian = remember(initialDate) { GregorianDate.fromJalali(initialDate) }
    var pane by remember { mutableStateOf(DatePickerPane.Day) }
    var visibleMonth by remember(initialGregorian) { mutableStateOf(initialGregorian.firstOfMonth()) }
    var selectedGregorian by remember(initialGregorian) { mutableStateOf(initialGregorian) }
    val todayGregorian = remember { GregorianDate.fromJalali(JalaliDate.today()) }
    val maxYear = todayGregorian.year
    var yearPageStart by remember(initialGregorian) {
        mutableIntStateOf(yearPageStartFor(initialGregorian.year, maxYear))
    }

    val events by eventRepository
        .observeForGregorianMonth(visibleMonth.year, visibleMonth.month)
        .collectAsState(initial = emptyList())
    val holidayKeys = remember(events) { officialHolidayDateKeys(events) }

    LaunchedEffect(visibleMonth.year, visibleMonth.month) {
        eventRepository.ensureGregorianMonthCached(visibleMonth.year, visibleMonth.month)
    }

    val yearPageYears = remember(yearPageStart, maxYear) {
        (yearPageStart..minOf(yearPageStart + DATE_PICKER_YEAR_PAGE_SIZE - 1, maxYear)).toList()
    }

    DatePickerDialogShell(
        pane = pane,
        monthName = GregorianDate.MONTH_NAMES_EN[visibleMonth.month - 1],
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
                    val y = (visibleMonth.year - 1).coerceAtLeast(GREGORIAN_PICKER_MIN_YEAR)
                    visibleMonth = GregorianDate(y, visibleMonth.month, 1)
                    selectedGregorian = selectedGregorian.coerceDayInMonth(y, selectedGregorian.month)
                }
                DatePickerPane.Year -> {
                    yearPageStart = (yearPageStart - DATE_PICKER_YEAR_PAGE_SIZE)
                        .coerceAtLeast(GREGORIAN_PICKER_MIN_YEAR)
                }
            }
        },
        onNext = {
            when (pane) {
                DatePickerPane.Day -> visibleMonth = visibleMonth.plusMonths(1)
                DatePickerPane.Month -> {
                    val y = (visibleMonth.year + 1).coerceAtMost(maxYear)
                    visibleMonth = GregorianDate(y, visibleMonth.month, 1)
                    selectedGregorian = selectedGregorian.coerceDayInMonth(y, selectedGregorian.month)
                }
                DatePickerPane.Year -> {
                    val maxStart = maxYear - DATE_PICKER_YEAR_PAGE_SIZE + 1
                    yearPageStart = (yearPageStart + DATE_PICKER_YEAR_PAGE_SIZE)
                        .coerceAtMost(maxStart.coerceAtLeast(GREGORIAN_PICKER_MIN_YEAR))
                }
            }
        },
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selectedGregorian.toJalali()) },
        dayCalendar = {
            val leadingBlanks = visibleMonth.firstOfMonth().weekdayIndex()
            val daysInMonth = visibleMonth.daysInMonth()
            val cells = buildList {
                repeat(leadingBlanks) { add(null) }
                for (d in 1..daysInMonth) {
                    add(GregorianDate(visibleMonth.year, visibleMonth.month, d))
                }
            }
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val jalali = date.toJalali()
                            val dayKey = jalaliEventDateKey(jalali)
                            DatePickerDayCell(
                                modifier = Modifier.weight(1f),
                                dayLabel = date.day.toString(),
                                isSelected = date == selectedGregorian,
                                isToday = date == todayGregorian,
                                isOfficialHoliday = holidayKeys.contains(dayKey),
                                isWeekend = date.weekdayIndex() == 6,
                                onClick = {
                                    selectedGregorian = date
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
                monthLabels = GregorianDate.MONTH_NAMES_EN,
                selectedMonth = visibleMonth.month,
                onMonthSelected = { month ->
                    visibleMonth = GregorianDate(visibleMonth.year, month, 1)
                    selectedGregorian = selectedGregorian.coerceDayInMonth(visibleMonth.year, month)
                    pane = DatePickerPane.Day
                },
            )
        },
        yearPicker = {
            DatePickerYearGrid(
                years = yearPageYears,
                selectedYear = visibleMonth.year,
                onYearSelected = { year ->
                    visibleMonth = GregorianDate(year, visibleMonth.month, 1)
                    selectedGregorian = selectedGregorian.coerceDayInMonth(year, selectedGregorian.month)
                    pane = DatePickerPane.Month
                },
            )
        },
    )
}

private fun yearPageStartFor(selectedYear: Int, maxYear: Int): Int {
    val maxStart = maxOf(GREGORIAN_PICKER_MIN_YEAR, maxYear - DATE_PICKER_YEAR_PAGE_SIZE + 1)
    val centered = selectedYear - DATE_PICKER_YEAR_PAGE_SIZE / 2
    return centered.coerceIn(GREGORIAN_PICKER_MIN_YEAR, maxStart)
}
