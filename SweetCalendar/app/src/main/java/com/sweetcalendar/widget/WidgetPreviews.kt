package com.sweetcalendar.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build

object WidgetPreviews {

    fun sync(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return
        val appContext = context.applicationContext
        try {
            val manager = AppWidgetManager.getInstance(appContext)
            manager.setWidgetPreview(
                ComponentName(appContext, TodayWidgetProvider::class.java),
                AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                TodayWidgetProvider.buildRemoteViews(appContext, WidgetPreviewData.todaySample(appContext)),
            )
            manager.setWidgetPreview(
                ComponentName(appContext, JarWidgetProvider::class.java),
                AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
                JarWidgetProvider.buildRemoteViews(appContext),
            )
        } catch (_: Exception) {
            // Preview is best-effort; never block widgets if the picker API rejects the RemoteViews.
        }
    }
}
