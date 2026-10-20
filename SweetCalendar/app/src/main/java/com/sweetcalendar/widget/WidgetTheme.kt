package com.sweetcalendar.widget

import android.content.Context
import com.sweetcalendar.R
import com.sweetcalendar.data.prefs.AppPreferences
import com.sweetcalendar.ui.theme.ThemeFamily
import com.sweetcalendar.ui.theme.resolveSweetColors
import com.sweetcalendar.ui.theme.toArgbInt

object WidgetTheme {

    data class Colors(
        val isDark: Boolean,
        val isSpace: Boolean,
        val cardBackgroundArgb: Int,
        val shadowArgb: Int,
        val lineArgb: Int,
        val ink: Int,
        val muted: Int,
        val purpleDeep: Int,
        val purple: Int,
        val pink: Int,
        val choc: Int,
        val addButtonText: Int,
        val addButtonFaceArgb: Int,
        val addButtonShadowArgb: Int,
        val checkboxUncheckedRes: Int,
        val checkboxCheckedRes: Int,
    )

    fun colors(context: Context): Colors {
        val prefs = AppPreferences(context)
        val isDark = prefs.darkMode
        val isSpace = prefs.themeFamily == ThemeFamily.Space
        val palette = resolveSweetColors(prefs.themeFamily, isDark)
        val addButtonShadow = if (isDark) palette.shadowDeep else palette.purpleDeep
        val addButtonFace = palette.purple
        val addButtonText = if (isDark) palette.ink.toArgbInt() else 0xFFFFFFFF.toInt()

        return Colors(
            isDark = isDark,
            isSpace = isSpace,
            cardBackgroundArgb = palette.paper.toArgbInt(alpha = 0.92f),
            shadowArgb = palette.shadow.toArgbInt(),
            lineArgb = palette.line.toArgbInt(),
            ink = palette.ink.toArgbInt(),
            muted = palette.muted.toArgbInt(),
            purpleDeep = palette.purpleDeep.toArgbInt(),
            purple = palette.purple.toArgbInt(),
            pink = palette.pink.toArgbInt(),
            choc = palette.choc.toArgbInt(),
            addButtonText = addButtonText,
            addButtonFaceArgb = addButtonFace.toArgbInt(),
            addButtonShadowArgb = addButtonShadow.toArgbInt(),
            checkboxUncheckedRes = when {
                isDark -> R.drawable.widget_checkbox_unchecked_dark
                else -> R.drawable.widget_checkbox_unchecked
            },
            checkboxCheckedRes = when {
                isDark -> R.drawable.widget_checkbox_checked_dark
                else -> R.drawable.widget_checkbox_checked
            },
        )
    }
}
