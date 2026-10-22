package com.sweetcalendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sweetcalendar.MainActivity
import com.sweetcalendar.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class JarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        widgetScope.launch {
            try {
                appWidgetIds.forEach { id ->
                    val views = try {
                        buildRemoteViews(appContext)
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
            val component = ComponentName(context, JarWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return
            val views = try {
                buildRemoteViews(context)
            } catch (_: Exception) {
                safeFallbackViews(context)
            }
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        fun buildRemoteViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_jar_small)
            val theme = WidgetTheme.colors(context)
            val data = WidgetDataLoader.loadToday(context)
            bindJarContent(views, context, theme, data.done, data.total, data.progress)
            attachOpenAppIntent(views, context)
            return views
        }

        private fun safeFallbackViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_jar_small)
            val theme = WidgetTheme.colors(context)
            bindJarContent(views, context, theme, done = 0, total = 0, progress = 0)
            attachOpenAppIntent(views, context)
            return views
        }

        private fun bindJarContent(
            views: RemoteViews,
            context: Context,
            theme: WidgetTheme.Colors,
            done: Int,
            total: Int,
            progress: Int,
        ) {
            val iconPx = (22f * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)
            WidgetUi.applyCardSurface(views, R.id.widget_jar_root, theme)
            views.setImageViewBitmap(
                R.id.widget_jar_icon,
                WidgetPixelIcons.brandBitmap(iconPx, theme),
            )
            views.setTextViewText(R.id.widget_jar_label, WidgetCopy.todayJarLabel(theme.isSpace))
            views.setTextViewText(R.id.widget_jar_big_num, WidgetCopy.jarCount(done, total))
            views.setTextColor(R.id.widget_jar_label, theme.ink)
            views.setTextColor(R.id.widget_jar_big_num, theme.ink)
            WidgetUi.applyJarProgress(views, progress, theme)
        }

        private fun attachOpenAppIntent(views: RemoteViews, context: Context) {
            val openApp = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_jar_root, openApp)
        }
    }
}
