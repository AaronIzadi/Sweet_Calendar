package com.example.calendartodo.jalali

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * A simple, dependency-free Jalali (Persian/Shamsi) date, with conversion
 * to/from the Gregorian calendar. The conversion math is ported from the
 * widely-used public-domain "jalali.js" algorithm (979-epoch day counting).
 */
data class JalaliDate(val year: Int, val month: Int, val day: Int) : Comparable<JalaliDate> {

    fun isLeapYear(): Boolean = isJalaliLeap(year)

    fun daysInMonth(): Int = when {
        month in 1..6 -> 31
        month in 7..11 -> 30
        month == 12 -> if (isLeapYear()) 30 else 29
        else -> 30
    }

    fun firstOfMonth(): JalaliDate = JalaliDate(year, month, 1)

    fun plusMonths(delta: Int): JalaliDate {
        var m = month - 1 + delta
        var y = year + m / 12
        m %= 12
        if (m < 0) {
            m += 12
            y -= 1
        }
        return JalaliDate(y, m + 1, 1)
    }

    fun toGregorianCalendar(): Calendar {
        val (gy, gm, gd) = jalaliToGregorian(year, month, day)
        return GregorianCalendar(gy, gm - 1, gd)
    }

    /** 0 = Saturday ... 6 = Friday (start-of-week used by the Iranian calendar). */
    fun weekdayIndex(): Int {
        val cal = toGregorianCalendar()
        val dow = cal.get(Calendar.DAY_OF_WEEK) // Calendar: Sunday=1 ... Saturday=7
        return dow % 7 // Saturday(7)->0, Sunday(1)->1, ... Friday(6)->6
    }

    fun formatIso(): String = "%04d/%02d/%02d".format(year, month, day)

    fun weekdayName(): String = WEEKDAY_NAMES[weekdayIndex()]

    fun plusDays(days: Int): JalaliDate {
        val cal = toGregorianCalendar()
        cal.add(Calendar.DAY_OF_MONTH, days)
        return fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun minusDays(days: Int): JalaliDate = plusDays(-days)

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year - other.year
        if (month != other.month) return month - other.month
        return day - other.day
    }

    companion object {
        val MONTH_NAMES = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )

        // Transliteration in English (user-visible month names)
        val MONTH_NAMES_EN = listOf(
            "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
            "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
        )

        val WEEKDAY_NAMES_SHORT = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

        val WEEKDAY_NAMES_EN_SHORT = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")

        val WEEKDAY_NAMES_EN = listOf(
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        )

        val WEEKDAY_NAMES = listOf(
            "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
        )

        fun weekRange(start: JalaliDate, end: JalaliDate): String {
            val startMonth = MONTH_NAMES_EN[start.month - 1]
            val endMonth = MONTH_NAMES_EN[end.month - 1]
            return if (start.month == end.month && start.year == end.year) {
                "$startMonth ${start.day} – ${end.day}, ${start.year}"
            } else {
                "$startMonth ${start.day} – $endMonth ${end.day}, ${end.year}"
            }
        }

        fun weekContaining(date: JalaliDate, weekStartsOn: Int = 0): List<JalaliDate> {
            val normalizedStart = weekStartsOn.coerceIn(0, 6)
            val startOffset = (date.weekdayIndex() - normalizedStart + 7) % 7
            val start = date.minusDays(startOffset)
            return (0..6).map { start.plusDays(it) }
        }

        fun weekdayLabels(weekStartsOn: Int = 0): List<String> =
            (0..6).map { WEEKDAY_NAMES_EN_SHORT[(weekStartsOn + it) % 7] }

        fun today(): JalaliDate {
            val cal = GregorianCalendar()
            return fromGregorian(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        }

        fun fromGregorian(year: Int, month: Int, day: Int): JalaliDate {
            val (jy, jm, jd) = gregorianToJalali(year, month, day)
            return JalaliDate(jy, jm, jd)
        }

        fun parseIso(s: String): JalaliDate {
            val parts = s.split(Regex("[-/]")).filter { it.isNotBlank() }
            return JalaliDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }

        // --- Leap-year determination (33-year cycle break points) ---
        private val BREAKS = intArrayOf(
            -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
        )

        private fun isJalaliLeap(year: Int): Boolean {
            var jp = BREAKS[0]
            var jump = 0
            for (j in 1 until BREAKS.size) {
                val jm = BREAKS[j]
                jump = jm - jp
                if (year < jm) break
                jp = jm
            }
            var n = year - jp
            if (n < jump) {
                if (jump - n < 6) n += jump - ((jump / 33) * 33)
                var leap = ((n + 1) % 33) % 4
                if (jump % 33 == 4 && jump - n == 4) leap += 1
                return leap == 1
            }
            return false
        }

        // --- Gregorian <-> Jalali, via the classic 979-epoch day-count algorithm ---

        private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
            val gy2 = gy - 1600
            val gm2 = gm - 1
            val gd2 = gd - 1

            val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            var gDayNo = 365L * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
            for (i in 0 until gm2) gDayNo += gDaysInMonth[i]
            if (gm2 > 1 && isGregorianLeap(gy)) gDayNo++
            gDayNo += gd2

            var jDayNo = gDayNo - 79

            val jNp = Math.floorDiv(jDayNo, 12053L)
            jDayNo = Math.floorMod(jDayNo, 12053L)

            var jy = 979L + 33 * jNp + 4 * (jDayNo / 1461)
            jDayNo %= 1461

            if (jDayNo >= 366) {
                jy += (jDayNo - 1) / 365
                jDayNo = (jDayNo - 1) % 365
            }

            val jm: Long
            val jd: Long
            if (jDayNo < 186) {
                jm = 1 + jDayNo / 31
                jd = jDayNo % 31 + 1
            } else {
                jm = 7 + (jDayNo - 186) / 30
                jd = (jDayNo - 186) % 30 + 1
            }
            return Triple(jy.toInt(), jm.toInt(), jd.toInt())
        }

        private fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
            val jy1 = jy - 979
            val jm1 = jm - 1
            val jd1 = jd - 1

            var jDayNo = 365L * jy1 + (jy1 / 33) * 8 + ((jy1 % 33 + 3) / 4)
            for (i in 0 until jm1) jDayNo += if (i < 6) 31 else 30
            jDayNo += jd1

            var gDayNo = jDayNo + 79

            var gy = 1600L + 400 * (gDayNo / 146097)
            gDayNo %= 146097

            var leap = true
            if (gDayNo >= 36525) {
                gDayNo--
                gy += 100 * (gDayNo / 36524)
                gDayNo %= 36524
                if (gDayNo >= 365) gDayNo++ else leap = false
            }

            gy += 4 * (gDayNo / 1461)
            gDayNo %= 1461

            if (gDayNo >= 366) {
                leap = false
                gDayNo--
                gy += gDayNo / 365
                gDayNo %= 365
            }

            val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            var gm = 0
            while (gm < 12) {
                val len = (gDaysInMonth[gm] + if (gm == 1 && leap) 1 else 0).toLong()
                if (gDayNo < len) break
                gDayNo -= len
                gm++
            }
            val gd = gDayNo + 1

            return Triple(gy.toInt(), gm + 1, gd.toInt())
        }

        private fun isGregorianLeap(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    }
}
