package com.example.calendartodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.calendartodo.MainActivity
import com.example.calendartodo.R

class JarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildViews(context))
        }
    }

    companion object {
        fun updateInstances(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, JarWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return
            val views = buildViews(context)
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun buildViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_jar_small)
            val theme = WidgetTheme.colors(context)
            val data = WidgetDataLoader.loadToday(context)
            val iconPx = (16f * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)

            views.setInt(R.id.widget_jar_root, "setBackgroundResource", theme.cardBackgroundRes)
            views.setImageViewBitmap(
                R.id.widget_jar_icon,
                WidgetPixelIcons.chocolateBitmap(iconPx, theme.isDark)
            )
            views.setTextViewText(R.id.widget_jar_big_num, "${data.done}/${data.total}")
            views.setTextColor(R.id.widget_jar_big_num, theme.purpleDeep)
            views.setTextColor(R.id.widget_jar_label, theme.muted)

            val openApp = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_jar_root, openApp)
            return views
        }
    }
}
