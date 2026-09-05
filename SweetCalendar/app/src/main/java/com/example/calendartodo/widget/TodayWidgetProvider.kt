package com.example.calendartodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.widget.RemoteViews
import com.example.calendartodo.MainActivity
import com.example.calendartodo.R
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.TaskCategory

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
            val theme = WidgetTheme.colors(context)
            val data = WidgetDataLoader.loadToday(context)
            val iconPx = (14f * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)

            views.setInt(R.id.widget_root, "setBackgroundResource", theme.cardBackgroundRes)
            views.setTextColor(R.id.widget_title, theme.ink)
            views.setTextViewText(R.id.widget_date, data.dateLabel)
            views.setTextColor(R.id.widget_date, theme.muted)
            views.setProgressBar(R.id.widget_progress, 100, data.progress, false)
            views.setInt(R.id.widget_progress, "setProgressDrawable", theme.progressDrawableRes)
            views.setInt(R.id.widget_add_button, "setBackgroundResource", theme.addButtonRes)
            views.setTextColor(R.id.widget_add_button, theme.addButtonText)

            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_1,
                iconId = R.id.widget_task_1_icon,
                checkboxId = R.id.widget_task_1_checkbox,
                titleId = R.id.widget_task_1_title,
                iconPx = iconPx,
                task = data.displayTasks.getOrNull(0)
            )
            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_2,
                iconId = R.id.widget_task_2_icon,
                checkboxId = R.id.widget_task_2_checkbox,
                titleId = R.id.widget_task_2_title,
                iconPx = iconPx,
                task = data.displayTasks.getOrNull(1)
            )
            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_3,
                iconId = R.id.widget_task_3_icon,
                checkboxId = R.id.widget_task_3_checkbox,
                titleId = R.id.widget_task_3_title,
                iconPx = iconPx,
                task = data.displayTasks.getOrNull(2)
            )

            val openApp = openAppIntent(context, 0)
            views.setOnClickPendingIntent(R.id.widget_root, openApp)
            views.setOnClickPendingIntent(R.id.widget_add_button, openApp)
            return views
        }

        private fun bindTaskRow(
            views: RemoteViews,
            theme: WidgetTheme.Colors,
            rowId: Int,
            iconId: Int,
            checkboxId: Int,
            titleId: Int,
            iconPx: Int,
            task: TaskEntity?
        ) {
            if (task == null) {
                views.setViewVisibility(rowId, View.GONE)
                return
            }

            views.setViewVisibility(rowId, View.VISIBLE)
            val category = TaskCategory.fromString(task.category)
            views.setImageViewBitmap(
                iconId,
                WidgetPixelIcons.categoryBitmap(category, iconPx, theme.isDark)
            )
            views.setImageViewResource(
                checkboxId,
                if (task.isDone) theme.checkboxCheckedRes else theme.checkboxUncheckedRes
            )
            views.setTextViewText(titleId, widgetTaskTitle(task))
            views.setTextColor(
                titleId,
                if (task.isDone) theme.choc else theme.ink
            )
            views.setInt(
                titleId,
                "setPaintFlags",
                if (task.isDone) {
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                } else {
                    Paint.ANTI_ALIAS_FLAG
                }
            )
        }

        private fun openAppIntent(context: Context, requestCode: Int): PendingIntent =
            PendingIntent.getActivity(
                context,
                requestCode,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    }
}
