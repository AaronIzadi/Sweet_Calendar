package com.example.calendartodo.widget

import android.content.Context
import com.example.calendartodo.R
import com.example.calendartodo.data.prefs.AppPreferences

object WidgetTheme {

    data class Colors(
        val isDark: Boolean,
        val cardBackgroundRes: Int,
        val ink: Int,
        val muted: Int,
        val purpleDeep: Int,
        val choc: Int,
        val addButtonText: Int,
        val progressDrawableRes: Int,
        val checkboxUncheckedRes: Int,
        val checkboxCheckedRes: Int,
        val addButtonRes: Int,
    )

    fun colors(context: Context): Colors {
        val isDark = AppPreferences(context).darkMode
        return if (isDark) {
            Colors(
                isDark = true,
                cardBackgroundRes = R.drawable.widget_background_dark,
                ink = 0xFFF2E9F5.toInt(),
                muted = 0xFFB3A0C9.toInt(),
                purpleDeep = 0xFFD4C2FA.toInt(),
                choc = 0xFFD8B08A.toInt(),
                addButtonText = 0xFFFFFFFF.toInt(),
                progressDrawableRes = R.drawable.widget_progress_bar_dark,
                checkboxUncheckedRes = R.drawable.widget_checkbox_unchecked_dark,
                checkboxCheckedRes = R.drawable.widget_checkbox_checked_dark,
                addButtonRes = R.drawable.widget_add_button_dark,
            )
        } else {
            Colors(
                isDark = false,
                cardBackgroundRes = R.drawable.widget_background,
                ink = 0xFF3A2317.toInt(),
                muted = 0xFF9A8878.toInt(),
                purpleDeep = 0xFF5B3F82.toInt(),
                choc = 0xFF6B4226.toInt(),
                addButtonText = 0xFFFFFFFF.toInt(),
                progressDrawableRes = R.drawable.widget_progress_bar,
                checkboxUncheckedRes = R.drawable.widget_checkbox_unchecked,
                checkboxCheckedRes = R.drawable.widget_checkbox_checked,
                addButtonRes = R.drawable.widget_add_button,
            )
        }
    }
}
