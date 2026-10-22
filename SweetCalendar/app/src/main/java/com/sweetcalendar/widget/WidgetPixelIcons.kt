package com.sweetcalendar.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.sweetcalendar.ui.components.TaskCategory
import com.sweetcalendar.ui.components.navRocketPixelRows

object WidgetPixelIcons {

    /** Bubblegum: lollipop. Orbit: rocket (jar + today widget header). */
    fun brandBitmap(sizePx: Int, theme: WidgetTheme.Colors): Bitmap {
        if (theme.isSpace) {
            return render(navRocketPixelRows, spaceRocketPalette(theme), sizePx)
        }
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val headPx = (sizePx * 0.58f).toInt().coerceAtLeast(1)
        val head = render(NAV_LOLLIPOP_HEAD_ROWS, lollipopHeadPalette(theme), headPx)
        val stickPx = (sizePx * 0.42f).toInt().coerceAtLeast(1)
        val stick = render(NAV_LOLLIPOP_STICK_ROWS, mapOf('t' to theme.purple), stickPx)
        canvas.drawBitmap(head, (sizePx - headPx) / 2f, 0f, null)
        canvas.drawBitmap(stick, (sizePx - stickPx) / 2f, headPx * 0.92f, null)
        return bitmap
    }

    fun categoryBitmap(category: TaskCategory, sizePx: Int, theme: WidgetTheme.Colors): Bitmap {
        val rows = when (category) {
            TaskCategory.Work -> BRIEFCASE_ROWS
            TaskCategory.Personal -> HEART_ROWS
            TaskCategory.Home -> HOME_ROWS
        }
        val color = when (category) {
            TaskCategory.Work -> if (theme.isDark) 0xFFC3AEEF.toInt() else theme.purpleDeep
            TaskCategory.Personal -> if (theme.isDark) 0xFFFF8FBB.toInt() else theme.pinkDeep
            TaskCategory.Home -> theme.mintDeep
        }
        return render(rows, mapOf('1' to color), sizePx)
    }

    private fun lollipopHeadPalette(theme: WidgetTheme.Colors): Map<Char, Int> {
        val head = if (theme.isDark) theme.pink else theme.pinkDeep
        return mapOf('a' to head, 'b' to 0xFFFFFFFF.toInt())
    }

    private fun spaceRocketPalette(theme: WidgetTheme.Colors): Map<Char, Int> =
        if (theme.isDark) {
            mapOf(
                '1' to 0xFFECEFF7.toInt(),
                'o' to 0xFF5FE6E0.toInt(),
                '2' to 0xFFFF6AC8.toInt(),
                'f' to 0xFFFFD466.toInt(),
            )
        } else {
            mapOf(
                '1' to 0xFFC9BFF2.toInt(),
                'o' to 0xFF1F9E98.toInt(),
                '2' to 0xFFD63F9E.toInt(),
                'f' to 0xFFDE9F14.toInt(),
            )
        }

    private fun render(rows: List<String>, palette: Map<Char, Int>, sizePx: Int): Bitmap {
        val cols = rows.maxOf { it.length }
        val cell = sizePx / maxOf(cols, rows.size).coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val offsetX = (sizePx - cell * cols) / 2f
        val offsetY = (sizePx - cell * rows.size) / 2f
        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, ch ->
                palette[ch]?.let { color ->
                    paint.color = color
                    canvas.drawRect(
                        offsetX + colIndex * cell,
                        offsetY + rowIndex * cell,
                        offsetX + (colIndex + 1) * cell,
                        offsetY + (rowIndex + 1) * cell,
                        paint
                    )
                }
            }
        }
        return bitmap
    }

    private val NAV_LOLLIPOP_HEAD_ROWS = listOf(
        "..aaaa..",
        ".aabaaa.",
        "aabaabaa",
        "aabaabaa",
        "aabaabaa",
        "aabaabaa",
        ".aabaaa.",
        "..aaaa.."
    )

    private val NAV_LOLLIPOP_STICK_ROWS = listOf(
        "...tt...",
        "...tt...",
        "...tt...",
        "...tt...",
        "...tt..."
    )

    private val BRIEFCASE_ROWS = listOf(
        "..111..",
        ".11111.",
        "1.....1",
        "1.111.1",
        "1111111"
    )

    private val HEART_ROWS = listOf(
        ".11.11.",
        "1111111",
        "1111111",
        ".11111.",
        "..111..",
        "...1..."
    )

    private val HOME_ROWS = listOf(
        "...1...",
        "..111..",
        ".1...1.",
        ".1.1.1.",
        ".11111."
    )
}
