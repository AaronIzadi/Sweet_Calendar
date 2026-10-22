package com.sweetcalendar.ui.taskdetail

import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.formatTime12h
import java.util.Calendar

private const val MS_PER_HOUR = 60L * 60L * 1000L
private const val MS_PER_DAY = 24L * MS_PER_HOUR

/** Deadline is 11:59:59.999 PM local time on the task's [JalaliDate] day. */
fun taskDayDeadlineMillis(date: JalaliDate): Long {
    val cal = date.toGregorianCalendar()
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.timeInMillis
}

fun formatRemainingUntilTaskDayEnd(
    date: JalaliDate,
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val remainingMs = taskDayDeadlineMillis(date) - nowMillis
    if (remainingMs <= 0) return "Past due"
    if (remainingMs < MS_PER_HOUR) return "Due in less than an hour"
    if (remainingMs < MS_PER_DAY) {
        val hours = (remainingMs / MS_PER_HOUR).toInt()
        return if (hours == 1) "Due in 1 hour" else "Due in $hours hours"
    }
    val days = (remainingMs / MS_PER_DAY).toInt()
    return if (days == 1) "Due in 1 day" else "Due in $days days"
}

fun formatTaskCompletedAt(completedAtMillis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = completedAtMillis }
    val jalali = JalaliDate.fromGregorian(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH),
    )
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[jalali.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[jalali.month - 1]
    val time24 = "%02d:%02d".format(
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
    )
    return "Completed $weekday, $month ${jalali.day}, ${jalali.year} · ${formatTime12h(time24)}"
}
