package com.sweetcalendar.widget

import android.view.View
import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.sweetcalendar.R

internal object WidgetUi {
    fun applyCardSurface(views: RemoteViews, @IdRes viewId: Int, theme: WidgetTheme.Colors) {
        views.setInt(viewId, "setBackgroundColor", theme.cardBackgroundArgb)
    }

    fun applyAddButton(views: RemoteViews, @IdRes viewId: Int, theme: WidgetTheme.Colors) {
        views.setInt(viewId, "setBackgroundColor", theme.addButtonFaceArgb)
        views.setTextColor(viewId, theme.addButtonText)
    }

    fun applyProgress(views: RemoteViews, progress: Int, theme: WidgetTheme.Colors) {
        if (theme.isDark) {
            views.setViewVisibility(R.id.widget_progress, View.GONE)
            views.setViewVisibility(R.id.widget_progress_dark, View.VISIBLE)
            views.setProgressBar(R.id.widget_progress_dark, 100, progress, false)
        } else {
            views.setViewVisibility(R.id.widget_progress_dark, View.GONE)
            views.setViewVisibility(R.id.widget_progress, View.VISIBLE)
            views.setProgressBar(R.id.widget_progress, 100, progress, false)
        }
    }
}
