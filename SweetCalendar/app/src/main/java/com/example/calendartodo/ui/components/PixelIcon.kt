package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun PixelIcon(
    rows: List<String>,
    palette: Map<Char, Color>,
    size: Dp,
    modifier: Modifier = Modifier
) {
    PixelIcon(rows, palette, width = size, height = size, modifier = modifier)
}

@Composable
internal fun PixelIcon(
    rows: List<String>,
    palette: Map<Char, Color>,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.then(Modifier.size(width = width, height = height))) {
        val cols = rows.maxOf { it.length }
        val cell = minOf(this.size.width / cols, this.size.height / rows.size)
        val offsetX = (this.size.width - cell * cols) / 2f
        val offsetY = (this.size.height - cell * rows.size) / 2f
        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, ch ->
                palette[ch]?.let { color ->
                    drawRect(
                        color = color,
                        topLeft = Offset(offsetX + colIndex * cell, offsetY + rowIndex * cell),
                        size = Size(cell, cell)
                    )
                }
            }
        }
    }
}
