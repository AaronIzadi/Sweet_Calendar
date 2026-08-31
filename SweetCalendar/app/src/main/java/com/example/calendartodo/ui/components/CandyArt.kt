package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
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
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.SprinklesBlue
import com.example.calendartodo.ui.theme.SprinklesGreen
import com.example.calendartodo.ui.theme.SprinklesRed

@Composable
private fun PixelArtImage(
    resId: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LollipopIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_lollipop_swirl,
        size = size,
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
