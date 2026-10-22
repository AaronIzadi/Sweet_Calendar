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
        applyThemedProgress(
            views = views,
            progress = progress,
            theme = theme,
            candyLight = R.id.widget_progress_candy_light,
            candyDark = R.id.widget_progress_candy_dark,
            spaceLight = R.id.widget_progress_space_light,
            spaceDark = R.id.widget_progress_space_dark,
        )
    }

    fun applyJarProgress(views: RemoteViews, progress: Int, theme: WidgetTheme.Colors) {
        applyThemedProgress(
            views = views,
            progress = progress,
            theme = theme,
            candyLight = R.id.widget_jar_progress_candy_light,
            candyDark = R.id.widget_jar_progress_candy_dark,
            spaceLight = R.id.widget_jar_progress_space_light,
            spaceDark = R.id.widget_jar_progress_space_dark,
        )
    }

    private fun applyThemedProgress(
        views: RemoteViews,
        progress: Int,
        theme: WidgetTheme.Colors,
        @IdRes candyLight: Int,
        @IdRes candyDark: Int,
        @IdRes spaceLight: Int,
        @IdRes spaceDark: Int,
    ) {
        val active = when {
            theme.isSpace && theme.isDark -> spaceDark
            theme.isSpace -> spaceLight
            theme.isDark -> candyDark
            else -> candyLight
        }
        for (id in listOf(candyLight, candyDark, spaceLight, spaceDark)) {
            views.setViewVisibility(id, if (id == active) View.VISIBLE else View.GONE)
        }
        views.setProgressBar(active, 100, progress.coerceIn(0, 100), false)
    }
}
