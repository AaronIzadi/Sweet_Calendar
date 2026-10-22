package com.sweetcalendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.RemoteViews
import com.sweetcalendar.MainActivity
import com.sweetcalendar.R
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.ui.components.TaskCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TodayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        widgetScope.launch {
            try {
                appWidgetIds.forEach { id ->
                    val views = try {
                        buildRemoteViews(appContext, WidgetDataLoader.loadToday(appContext))
                    } catch (_: Exception) {
                        safeFallbackViews(appContext)
                    }
                    appWidgetManager.updateAppWidget(id, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        fun updateInstances(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, TodayWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return
            val views = try {
                buildRemoteViews(context, WidgetDataLoader.loadToday(context))
            } catch (_: Exception) {
                safeFallbackViews(context)
            }
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        fun buildRemoteViews(context: Context, data: TodayWidgetData): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_today)
            val theme = WidgetTheme.colors(context)
            val density = context.resources.displayMetrics.density
            val headerPx = (16f * density).toInt().coerceAtLeast(1)
            val taskIconPx = (14f * density).toInt().coerceAtLeast(1)

            WidgetUi.applyCardSurface(views, R.id.widget_root, theme)
            views.setImageViewBitmap(R.id.widget_header_icon, WidgetPixelIcons.brandBitmap(headerPx, theme))
            views.setTextColor(R.id.widget_title, theme.ink)
            views.setTextViewText(R.id.widget_title, context.getString(R.string.app_name))
            views.setTextViewText(R.id.widget_date, data.dateLabel)
            views.setTextColor(R.id.widget_date, theme.muted)
            views.setTextViewText(R.id.widget_jar_label, WidgetCopy.todayJarLabel(theme.isSpace))
            views.setTextViewText(R.id.widget_jar_count, WidgetCopy.jarCount(data.done, data.total))
            views.setTextColor(R.id.widget_jar_label, theme.ink)
            views.setTextColor(R.id.widget_jar_count, theme.ink)
            WidgetUi.applyProgress(views, data.progress, theme)
            WidgetUi.applyAddButton(views, R.id.widget_add_button, theme)

            val hasTasks = data.displayTasks.isNotEmpty()
            views.setViewVisibility(R.id.widget_empty_state, if (hasTasks) View.GONE else View.VISIBLE)
            views.setTextColor(R.id.widget_empty_state, theme.muted)
            if (!hasTasks) {
                views.setTextViewText(R.id.widget_empty_state, "No tasks today")
            }

            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_1,
                iconId = R.id.widget_task_1_icon,
                checkboxId = R.id.widget_task_1_checkbox,
                titleId = R.id.widget_task_1_title,
                iconPx = taskIconPx,
                task = data.displayTasks.getOrNull(0)
            )
            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_2,
                iconId = R.id.widget_task_2_icon,
                checkboxId = R.id.widget_task_2_checkbox,
                titleId = R.id.widget_task_2_title,
                iconPx = taskIconPx,
                task = data.displayTasks.getOrNull(1)
            )
            bindTaskRow(
                views = views,
                theme = theme,
                rowId = R.id.widget_task_row_3,
                iconId = R.id.widget_task_3_icon,
                checkboxId = R.id.widget_task_3_checkbox,
                titleId = R.id.widget_task_3_title,
                iconPx = taskIconPx,
                task = data.displayTasks.getOrNull(2)
            )

            val openApp = openAppIntent(context, 0)
            views.setOnClickPendingIntent(R.id.widget_root, openApp)
            views.setOnClickPendingIntent(R.id.widget_add_button, openApp)
            return views
        }

        private fun safeFallbackViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_today)
            val theme = WidgetTheme.colors(context)
            val headerPx = (16f * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)
            WidgetUi.applyCardSurface(views, R.id.widget_root, theme)
            views.setImageViewBitmap(R.id.widget_header_icon, WidgetPixelIcons.brandBitmap(headerPx, theme))
            views.setTextColor(R.id.widget_title, theme.ink)
            views.setTextViewText(R.id.widget_title, context.getString(R.string.app_name))
            views.setTextViewText(R.id.widget_date, "Today")
            views.setTextColor(R.id.widget_date, theme.muted)
            views.setTextViewText(R.id.widget_jar_label, WidgetCopy.todayJarLabel(theme.isSpace))
            views.setTextViewText(R.id.widget_jar_count, WidgetCopy.jarCount(0, 0))
            views.setTextColor(R.id.widget_jar_label, theme.ink)
            views.setTextColor(R.id.widget_jar_count, theme.ink)
            WidgetUi.applyProgress(views, 0, theme)
            WidgetUi.applyAddButton(views, R.id.widget_add_button, theme)
            views.setViewVisibility(R.id.widget_empty_state, View.VISIBLE)
            views.setTextViewText(R.id.widget_empty_state, "No tasks today")
            views.setTextColor(R.id.widget_empty_state, theme.muted)
            listOf(R.id.widget_task_row_1, R.id.widget_task_row_2, R.id.widget_task_row_3).forEach {
                views.setViewVisibility(it, View.GONE)
            }
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
                WidgetPixelIcons.categoryBitmap(category, iconPx, theme)
            )
            views.setImageViewResource(
                checkboxId,
                if (task.isDone) theme.checkboxCheckedRes else theme.checkboxUncheckedRes
            )
            val title = widgetTaskTitle(task)
            views.setTextViewText(titleId, taskTitleForRemoteViews(title, task.isDone))
            views.setTextColor(
                titleId,
                if (task.isDone) theme.muted else theme.ink
            )
        }

        private fun taskTitleForRemoteViews(title: String, isDone: Boolean): CharSequence {
            if (!isDone) return title
            return SpannableString(title).apply {
                setSpan(StrikethroughSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
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
