package com.sweetcalendar.widget

import android.content.Context
import androidx.annotation.DrawableRes
import com.sweetcalendar.R
import com.sweetcalendar.data.prefs.AppPreferences
import com.sweetcalendar.ui.theme.ThemeFamily
import com.sweetcalendar.ui.theme.resolveSweetColors
import com.sweetcalendar.ui.theme.toArgbInt

object WidgetTheme {

    data class Colors(
        val isDark: Boolean,
        val isSpace: Boolean,
        @DrawableRes val cardBackgroundRes: Int,
        @DrawableRes val addButtonRes: Int,
        val shadowArgb: Int,
        val lineArgb: Int,
        val ink: Int,
        val muted: Int,
        val purpleDeep: Int,
        val purple: Int,
        val pink: Int,
        val pinkDeep: Int,
        val pinkText: Int,
        val mint: Int,
        val mintDeep: Int,
        val lemonDeep: Int,
        val choc: Int,
        val chocDeep: Int,
        val paperFill: Int,
        val addButtonText: Int,
        @DrawableRes val checkboxUncheckedRes: Int,
        @DrawableRes val checkboxCheckedRes: Int,
    )

    fun colors(context: Context): Colors {
        val prefs = AppPreferences(context)
        val isDark = prefs.darkMode
        val isSpace = prefs.themeFamily == ThemeFamily.Space
        val palette = resolveSweetColors(prefs.themeFamily, isDark)
        val addButtonText = if (isDark) palette.ink.toArgbInt() else 0xFFFFFFFF.toInt()

        return Colors(
            isDark = isDark,
            isSpace = isSpace,
            cardBackgroundRes = when {
                isSpace && isDark -> R.drawable.widget_card_space_dark
                isSpace -> R.drawable.widget_card_space_light
                isDark -> R.drawable.widget_card_candy_dark
                else -> R.drawable.widget_card_candy_light
            },
            addButtonRes = when {
                isSpace && isDark -> R.drawable.widget_add_button_space_dark
                isSpace -> R.drawable.widget_add_button_space_light
                isDark -> R.drawable.widget_add_button_candy_dark
                else -> R.drawable.widget_add_button_candy_light
            },
            shadowArgb = palette.shadow.toArgbInt(),
            lineArgb = palette.line.toArgbInt(),
            ink = palette.ink.toArgbInt(),
            muted = palette.muted.toArgbInt(),
            purpleDeep = palette.purpleDeep.toArgbInt(),
            purple = palette.purple.toArgbInt(),
            pink = palette.pink.toArgbInt(),
            pinkDeep = palette.pinkDeep.toArgbInt(),
            pinkText = palette.pinkText.toArgbInt(),
            mint = palette.mint.toArgbInt(),
            mintDeep = palette.mintDeep.toArgbInt(),
            lemonDeep = palette.lemonDeep.toArgbInt(),
            choc = palette.choc.toArgbInt(),
            chocDeep = palette.chocDeep.toArgbInt(),
            paperFill = palette.cream.toArgbInt(),
            addButtonText = addButtonText,
            checkboxUncheckedRes = when {
                isSpace && isDark -> R.drawable.widget_checkbox_unchecked_space_dark
                isSpace -> R.drawable.widget_checkbox_unchecked_space
                isDark -> R.drawable.widget_checkbox_unchecked_dark
                else -> R.drawable.widget_checkbox_unchecked
            },
            checkboxCheckedRes = when {
                isSpace && isDark -> R.drawable.widget_checkbox_checked_space_dark
                isSpace -> R.drawable.widget_checkbox_checked_space
                isDark -> R.drawable.widget_checkbox_checked_dark
                else -> R.drawable.widget_checkbox_checked
            },
        )
    }
}
