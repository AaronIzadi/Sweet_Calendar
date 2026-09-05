package com.example.calendartodo.widget

import android.content.Context
import com.example.calendartodo.R
import com.example.calendartodo.data.prefs.AppPreferences
import com.example.calendartodo.ui.theme.ThemeFamily

object WidgetTheme {

    data class Colors(
        val isDark: Boolean,
        val isSpace: Boolean,
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
        val prefs = AppPreferences(context)
        val isDark = prefs.darkMode
        val isSpace = prefs.themeFamily == ThemeFamily.Space
        return when {
            isSpace && isDark -> Colors(
                isDark = true,
                isSpace = true,
                cardBackgroundRes = R.drawable.widget_background_dark,
                ink = 0xFFEDEBFF.toInt(),
                muted = 0xFF9C97D9.toInt(),
                purpleDeep = 0xFF6B4FD6.toInt(),
                choc = 0xFF9A93B8.toInt(),
                addButtonText = 0xFF0B0E24.toInt(),
                progressDrawableRes = R.drawable.widget_progress_bar_dark,
                checkboxUncheckedRes = R.drawable.widget_checkbox_unchecked_dark,
                checkboxCheckedRes = R.drawable.widget_checkbox_checked_dark,
                addButtonRes = R.drawable.widget_add_button_dark,
            )
            isSpace -> Colors(
                isDark = false,
                isSpace = true,
                cardBackgroundRes = R.drawable.widget_background,
                ink = 0xFF2B2450.toInt(),
                muted = 0xFF7A72B0.toInt(),
                purpleDeep = 0xFF6B4FD6.toInt(),
                choc = 0xFF9089B0.toInt(),
                addButtonText = 0xFFFFFFFF.toInt(),
                progressDrawableRes = R.drawable.widget_progress_bar,
                checkboxUncheckedRes = R.drawable.widget_checkbox_unchecked,
                checkboxCheckedRes = R.drawable.widget_checkbox_checked,
                addButtonRes = R.drawable.widget_add_button,
            )
            isDark -> Colors(
                isDark = true,
                isSpace = false,
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
            else -> Colors(
                isDark = false,
                isSpace = false,
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
