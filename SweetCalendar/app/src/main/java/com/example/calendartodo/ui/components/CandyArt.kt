package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.CaramelOrange
import com.example.calendartodo.ui.theme.CherryRed
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.SkyBlue
import com.example.calendartodo.ui.theme.SprinklesBlue
import com.example.calendartodo.ui.theme.SprinklesGreen
import com.example.calendartodo.ui.theme.SprinklesRed

@Composable
fun LollipopIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
  Canvas(modifier.size(size)) {
    val s = this.size.minDimension
    val border = s * 0.08f
    drawRect(ChocolateBrown, Offset(s * 0.42f, s * 0.55f), Size(s * 0.16f, s * 0.4f))
    drawCircle(BubblegumPink, s * 0.35f, Offset(s * 0.5f, s * 0.35f))
    drawCircle(
      CherryRed,
      s * 0.35f,
      Offset(s * 0.5f, s * 0.35f),
      style = Stroke(width = border)
    )
    drawRect(LemonYellow, Offset(s * 0.35f, s * 0.25f), Size(s * 0.08f, s * 0.08f))
    drawRect(SkyBlue, Offset(s * 0.55f, s * 0.4f), Size(s * 0.08f, s * 0.08f))
  }
}

@Composable
fun WrappedCandyIcon(modifier: Modifier = Modifier, size: Dp = 20.dp) {
  Canvas(modifier.size(size)) {
    val s = this.size.minDimension
    val b = s * 0.08f
    drawRoundRect(
      GrapePurple,
      Offset(s * 0.2f, s * 0.3f),
      Size(s * 0.6f, s * 0.4f),
      CornerRadius(s * 0.08f)
    )
    drawRoundRect(
      PixelBorder,
      Offset(s * 0.2f, s * 0.3f),
      Size(s * 0.6f, s * 0.4f),
      CornerRadius(s * 0.08f),
      style = Stroke(width = b)
    )
    drawRect(MintGreen, Offset(s * 0.05f, s * 0.38f), Size(s * 0.18f, s * 0.24f))
    drawRect(CaramelOrange, Offset(s * 0.77f, s * 0.38f), Size(s * 0.18f, s * 0.24f))
  }
}

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
