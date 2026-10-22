package com.sweetcalendar.widget

import android.content.Context
import com.sweetcalendar.R
import com.sweetcalendar.data.local.TaskEntity

object WidgetPreviewData {

    fun todaySample(context: Context): TodayWidgetData {
        val res = context.resources
        return TodayWidgetData(
            dateLabel = res.getString(R.string.widget_preview_date),
            done = 2,
            total = 3,
            progress = 67,
            displayTasks = listOf(
                TaskEntity(
                    title = res.getString(R.string.widget_preview_task_1),
                    jalaliDate = "1400-01-01",
                    category = "Personal",
                ),
                TaskEntity(
                    title = res.getString(R.string.widget_preview_task_2),
                    jalaliDate = "1400-01-01",
                    category = "Work",
                ),
                TaskEntity(
                    title = res.getString(R.string.widget_preview_task_3),
                    jalaliDate = "1400-01-01",
                    category = "Home",
                    isDone = true,
                ),
            ),
        )
    }
}
