package com.example.calendartodo.jalali

import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

data class GregorianDate(val year: Int, val month: Int, val day: Int) : Comparable<GregorianDate> {

    fun daysInMonth(): Int {
        val cal = GregorianCalendar(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun firstOfMonth(): GregorianDate = GregorianDate(year, month, 1)

    fun plusMonths(delta: Int): GregorianDate {
        val cal = GregorianCalendar(year, month - 1, 1)
        cal.add(Calendar.MONTH, delta)
        return GregorianDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1)
    }

    fun toJalali(): JalaliDate = JalaliDate.fromGregorian(year, month, day)

    /** 0 = Saturday … 6 = Friday (same week model as [JalaliDate]). */
    fun weekdayIndex(): Int = toJalali().weekdayIndex()

    override fun compareTo(other: GregorianDate): Int {
        if (year != other.year) return year - other.year
        if (month != other.month) return month - other.month
        return day - other.day
    }

    companion object {
        val MONTH_NAMES_EN: List<String> = DateFormatSymbols(Locale.ENGLISH).months.take(12)

        fun today(): GregorianDate = fromJalali(JalaliDate.today())

        fun fromJalali(date: JalaliDate): GregorianDate {
            val cal = date.toGregorianCalendar()
            return GregorianDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun weekRange(start: GregorianDate, end: GregorianDate): String {
            val startMonth = MONTH_NAMES_EN[start.month - 1]
            val endMonth = MONTH_NAMES_EN[end.month - 1]
            return if (start.month == end.month && start.year == end.year) {
                "$startMonth ${start.day} – ${end.day}, ${start.year}"
            } else {
                "$startMonth ${start.day} – $endMonth ${end.day}, ${end.year}"
            }
        }

        fun weekContaining(date: GregorianDate, weekStartsOn: Int = 0): List<GregorianDate> =
            JalaliDate.weekContaining(date.toJalali(), weekStartsOn).map { fromJalali(it) }

        fun jalaliMonthsOverlapping(year: Int, month: Int): List<Pair<Int, Int>> {
            val daysInMonth = GregorianDate(year, month, 1).daysInMonth()
            val months = linkedSetOf<Pair<Int, Int>>()
            for (day in 1..daysInMonth) {
                val jalali = GregorianDate(year, month, day).toJalali()
                months.add(jalali.year to jalali.month)
            }
            return months.toList()
        }
    }
}
