package com.example.calendartodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.PixelShadow

fun Modifier.pixelBorder(
  borderColor: Color = PixelBorder,
  borderWidth: Dp = 3.dp,
  shadowColor: Color = PixelShadow,
  shadowOffset: Dp = 3.dp
): Modifier = this
  .drawBehind {
    val offset = shadowOffset.toPx()
    val stroke = borderWidth.toPx()
    drawRect(
      color = shadowColor,
      topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
      size = androidx.compose.ui.geometry.Size(size.width, size.height)
    )
    drawRect(
      color = borderColor,
      size = androidx.compose.ui.geometry.Size(size.width - offset, size.height - offset),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
    )
  }

@Composable
fun PixelPanel(
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  borderColor: Color = PixelBorder,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .pixelBorder(borderColor = borderColor)
      .background(backgroundColor)
      .padding(8.dp),
    content = content
  )
}

@Composable
fun PixelButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  borderColor: Color = PixelBorder,
  contentDescription: String? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val semanticsModifier = if (contentDescription != null) {
    modifier.semantics { this.contentDescription = contentDescription }
  } else {
    modifier
  }
  Box(
    modifier = semanticsModifier
      .pixelBorder(borderColor = borderColor)
      .clip(RoundedCornerShape(0.dp))
      .background(backgroundColor)
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 8.dp),
    content = content
  )
}

@Composable
fun PixelFab(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  backgroundColor: Color,
  contentDescription: String? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val semanticsModifier = if (contentDescription != null) {
    modifier.semantics { this.contentDescription = contentDescription }
  } else {
    modifier
  }
  Box(
    modifier = semanticsModifier
      .pixelBorder()
      .background(backgroundColor)
      .clickable(onClick = onClick)
      .padding(14.dp),
    content = content
  )
}

fun Modifier.pixelCell(
  selected: Boolean,
  selectedColor: Color,
  defaultColor: Color,
  borderColor: Color = PixelBorder
): Modifier = this
  .border(2.dp, borderColor)
  .background(if (selected) selectedColor else defaultColor)
