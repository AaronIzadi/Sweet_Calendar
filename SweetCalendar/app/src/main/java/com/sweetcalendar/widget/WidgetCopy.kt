package com.sweetcalendar.widget

object WidgetCopy {
    fun todayJarLabel(isSpace: Boolean): String =
        if (isSpace) "Mission status" else "Today's jar"

    fun jarCount(done: Int, total: Int): String = "$done / $total"
}
