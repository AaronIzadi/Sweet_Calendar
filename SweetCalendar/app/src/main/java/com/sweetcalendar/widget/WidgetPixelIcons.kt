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

    fun categoryBitmap(
        category: TaskCategory,
        sizePx: Int,
        theme: WidgetTheme.Colors,
        forTodo: Boolean = false,
    ): Bitmap {
        val rows = when (category) {
            TaskCategory.Work -> BRIEFCASE_ROWS
            TaskCategory.Personal -> HEART_ROWS
            TaskCategory.Home -> HOME_ROWS
            TaskCategory.School -> if (forTodo) PEN_ROWS else BOOK_ROWS
        }
        if (category == TaskCategory.School && !forTodo) {
            return render(
                BOOK_ROWS,
                mapOf(
                    '1' to markerColor(category, theme, forTodo = false),
                    '2' to theme.choc,
                    '3' to theme.paperFill,
                ),
                sizePx,
            )
        }
        if (category == TaskCategory.School && forTodo) {
            val pen = markerColor(category, theme, forTodo = true)
            return render(
                PEN_ROWS,
                mapOf('1' to pen, '2' to pen, 'e' to blendAlpha(pen, 0.65f)),
                sizePx,
            )
        }
        return render(rows, mapOf('1' to markerColor(category, theme, forTodo)), sizePx)
    }

    private fun markerColor(category: TaskCategory, theme: WidgetTheme.Colors, forTodo: Boolean): Int {
        if (forTodo) {
            return when (category) {
                TaskCategory.Personal -> theme.purpleDeep
                TaskCategory.Work -> theme.mintDeep
                TaskCategory.School -> theme.pinkDeep
                TaskCategory.Home -> theme.mintDeep
            }
        }
        return when (category) {
            TaskCategory.Work -> if (theme.isDark) 0xFFC3AEEF.toInt() else theme.purpleDeep
            TaskCategory.Personal -> if (theme.isDark) 0xFFFF8FBB.toInt() else theme.pinkDeep
            TaskCategory.Home -> theme.mintDeep
            TaskCategory.School -> theme.choc
        }
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

    private fun blendAlpha(color: Int, alpha: Float): Int {
        val a = (alpha * ((color shr 24) and 0xFF)).toInt().coerceIn(0, 255)
        return (color and 0x00FFFFFF) or (a shl 24)
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
                        paint,
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

    private val BOOK_ROWS = listOf(
        "....1....",
        "...122...",
        "..123321.",
        ".12333321",
        ".12333321",
        "..123321.",
        "...122...",
    )

    private val PEN_ROWS = listOf(
        ".ee....",
        ".e11...",
        "..111..",
        "...111.",
        "....111",
        ".....11",
        "......12",
    )
}
