package com.example.calendartodo.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.example.calendartodo.ui.components.TaskCategory

object WidgetPixelIcons {

    fun categoryBitmap(category: TaskCategory, sizePx: Int): Bitmap {
        val rows = when (category) {
            TaskCategory.Work -> GEM_ROWS
            TaskCategory.Personal -> HEART_ROWS
            TaskCategory.Home -> LEAF_ROWS
        }
        val color = when (category) {
            TaskCategory.Work -> 0xFF5B3F82.toInt()
            TaskCategory.Personal -> 0xFFE0487F.toInt()
            TaskCategory.Home -> 0xFF4FB894.toInt()
        }
        return render(rows, mapOf('1' to color), sizePx)
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

    private val GEM_ROWS = listOf(
        "...1...",
        "..111..",
        ".11111.",
        "1111111",
        ".11111.",
        "..111..",
        "...1..."
    )

    private val HEART_ROWS = listOf(
        ".11.11.",
        "1111111",
        "1111111",
        ".11111.",
        "..111..",
        "...1..."
    )

    private val LEAF_ROWS = listOf(
        "..1....",
        ".111...",
        "11111..",
        ".11111.",
        "..1111.",
        "...111.",
        "....11."
    )
}
