package com.example.calendartodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.calendartodo.MainActivity
import com.example.calendartodo.R
import com.example.calendartodo.data.local.TaskEntity

class TodayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildViews(context))
        }
    }

    companion object {
        fun updateInstances(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, TodayWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return
            val views = buildViews(context)
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun buildViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_today)
            val data = WidgetDataLoader.loadToday(context)

            views.setTextViewText(R.id.widget_date, data.dateLabel)
            views.setTextViewText(R.id.widget_jar_count, "${data.done}/${data.total}")
            views.setProgressBar(R.id.widget_progress, 100, data.progress, false)

            bindTaskLine(views, R.id.widget_task_1, data.displayTasks.getOrNull(0))
            bindTaskLine(views, R.id.widget_task_2, data.displayTasks.getOrNull(1))
            bindTaskLine(views, R.id.widget_task_3, data.displayTasks.getOrNull(2))

            val openApp = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openApp)
            return views
        }

        private fun bindTaskLine(views: RemoteViews, viewId: Int, task: TaskEntity?) {
            if (task == null) {
                views.setViewVisibility(viewId, View.GONE)
                return
            }
            views.setViewVisibility(viewId, View.VISIBLE)
            views.setTextViewText(viewId, taskLineText(task))
            val color = if (task.isDone) 0xFFB7A493.toInt() else 0xFF3A2317.toInt()
            views.setTextColor(viewId, color)
        }
    }
}
