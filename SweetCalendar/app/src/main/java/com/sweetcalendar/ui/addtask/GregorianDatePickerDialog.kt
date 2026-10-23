package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.repository.EventRepository

@Composable
fun GregorianDatePickerDialog(
    initialDate: JalaliDate,
    eventRepository: EventRepository,
    onDismiss: () -> Unit,
    onConfirm: (JalaliDate) -> Unit,
) {
    val initialGregorian = remember(initialDate) { GregorianDate.fromJalali(initialDate) }
    var visibleMonth by remember(initialGregorian) { mutableStateOf(initialGregorian.firstOfMonth()) }
    var selectedGregorian by remember(initialGregorian) { mutableStateOf(initialGregorian) }
    val todayGregorian = remember { GregorianDate.fromJalali(JalaliDate.today()) }

    val events by eventRepository
        .observeForGregorianMonth(visibleMonth.year, visibleMonth.month)
        .collectAsState(initial = emptyList())
    val holidayKeys = remember(events) { officialHolidayDateKeys(events) }

    LaunchedEffect(visibleMonth.year, visibleMonth.month) {
        eventRepository.ensureGregorianMonthCached(visibleMonth.year, visibleMonth.month)
    }

    DatePickerDialogShell(
        monthLabel = "${GregorianDate.MONTH_NAMES_EN[visibleMonth.month - 1]} ${visibleMonth.year}",
        onPrevMonth = { visibleMonth = visibleMonth.plusMonths(-1) },
        onNextMonth = { visibleMonth = visibleMonth.plusMonths(1) },
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selectedGregorian.toJalali()) },
    ) {
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
    }
}
