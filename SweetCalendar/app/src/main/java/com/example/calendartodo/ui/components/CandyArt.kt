package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendartodo.R
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintDeep
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PinkDeep
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.SprinklesBlue
import com.example.calendartodo.ui.theme.SprinklesGreen
import com.example.calendartodo.ui.theme.SprinklesRed

@Composable
private fun PixelArtImage(
    resId: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = modifier.size(width = size, height = height ?: size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LollipopIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    height: Dp? = null
) {
    PixelArtImage(
        resId = R.drawable.pixel_lollipop_swirl,
        size = size,
        height = height,
        modifier = modifier,
        contentDescription = "Lollipop"
    )
}

@Composable
fun WrappedCandyIcon(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_wrapped_candy,
        size = size,
        modifier = modifier,
        contentDescription = "Candy"
    )
}

@Composable
fun PinkLollipopIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_lollipop,
        size = size,
        modifier = modifier,
        contentDescription = "Lollipop"
    )
}

@Composable
fun PeppermintCandyIcon(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_peppermint,
        size = size,
        modifier = modifier,
        contentDescription = "Peppermint candy"
    )
}

@Composable
fun IceCreamIcon(modifier: Modifier = Modifier, size: Dp = 120.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_ice_cream,
        size = size,
        modifier = modifier,
        contentDescription = "Ice cream"
    )
}

@Composable
fun ChocolateIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_chocolate,
        size = size,
        modifier = modifier,
        contentDescription = "Chocolate"
    )
}

/** Small colored marker used on the calendar grid where tinting is needed. */
@Composable
fun GummyIcon(modifier: Modifier = Modifier, size: Dp = 16.dp, color: Color = LemonYellow) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val b = s * 0.1f
        val body = Path().apply {
            moveTo(s * 0.3f, s * 0.35f)
            lineTo(s * 0.7f, s * 0.35f)
            lineTo(s * 0.75f, s * 0.65f)
            lineTo(s * 0.25f, s * 0.65f)
            close()
        }
        drawPath(body, color)
        drawPath(body, PixelBorder, style = Stroke(width = b))
        drawCircle(color, s * 0.1f, Offset(s * 0.25f, s * 0.2f))
        drawCircle(color, s * 0.1f, Offset(s * 0.75f, s * 0.2f))
    }
}

/** Work category marker — purple gem from mockup `buildGem`. */
@Composable
fun TaskGemIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            "...1...",
            "..111..",
            ".11111.",
            "1111111",
            ".11111.",
            "..111..",
            "...1..."
        ),
        palette = mapOf('1' to GrapePurple),
        size = size,
        modifier = modifier
    )
}

/** Personal category marker — pink heart from mockup `buildHeart`. */
@Composable
fun TaskHeartIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            ".11.11.",
            "1111111",
            "1111111",
            ".11111.",
            "..111..",
            "...1..."
        ),
        palette = mapOf('1' to PinkDeep),
        size = size,
        modifier = modifier
    )
}

/** Home category marker — mint leaf from mockup `buildLeaf`. */
@Composable
fun TaskLeafIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            "..1....",
            ".111...",
            "11111..",
            ".11111.",
            "..1111.",
            "...111.",
            "....11."
        ),
        palette = mapOf('1' to MintDeep),
        size = size,
        modifier = modifier
    )
}

/** Completed-task candy from mockup `buildCheckCandy`. */
@Composable
fun CheckCandyIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            "..mmm..",
            ".mmmmm.",
            "mmmmmmm",
            "mwmwmmm",
            "mmwmwmm",
            ".mmwmm.",
            "..mmm.."
        ),
        palette = mapOf('m' to MintGreen, 'w' to Color.White),
        size = size,
        modifier = modifier
    )
}

/** High-priority sparkle from mockup `buildSparkle`. */
@Composable
fun SparkleIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    PixelIcon(
        rows = listOf(
            "..y..",
            "..y..",
            "yyYyy",
            "..y..",
            "..y.."
        ),
        palette = mapOf('y' to Color(0xFFE8B32A), 'Y' to LemonYellow),
        size = size,
        modifier = modifier
    )
}

@Composable
private fun PixelIcon(
    rows: List<String>,
    palette: Map<Char, Color>,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(size)) {
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

@Composable
fun CandySprinklesBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val sprinkleColors = listOf(SprinklesRed, SprinklesBlue, SprinklesGreen, LemonYellow, BubblegumPink)
        val step = 28.dp.toPx()
        var row = 0
        var y = 0f
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else step / 2
            var col = 0
            while (x < size.width) {
                val color = sprinkleColors[(row + col) % sprinkleColors.size]
                if ((row + col) % 3 != 0) {
                    drawRect(color, Offset(x, y), Size(4f, 4f))
                }
                x += step
                col++
            }
            y += step
            row++
        }
    }
}
