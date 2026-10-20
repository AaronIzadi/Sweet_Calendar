package com.sweetcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
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
import com.sweetcalendar.ui.theme.SweetTheme

fun Modifier.pixelBorder(
    borderColor: Color,
    borderWidth: Dp = 3.dp,
    shadowColor: Color,
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
fun Modifier.themePixelBorder(
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 3.dp,
    borderColor: Color = SweetTheme.colors.choc,
    shadowColor: Color = SweetTheme.colors.shadowDeep,
): Modifier = pixelBorder(
    borderColor = borderColor,
    borderWidth = borderWidth,
    shadowColor = shadowColor,
    shadowOffset = shadowOffset
)

@Composable
fun PixelPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SweetTheme.colors.paper,
    borderColor: Color = SweetTheme.colors.choc,
    shadowColor: Color = SweetTheme.colors.shadowDeep,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .themePixelBorder(borderColor = borderColor, shadowColor = shadowColor)
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
    borderColor: Color = SweetTheme.colors.choc,
    shadowColor: Color = SweetTheme.colors.shadowDeep,
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
            .themePixelBorder(borderColor = borderColor, shadowColor = shadowColor)
            .clip(RoundedCornerShape(0.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        content = content
    )
}

@Composable
fun PixelFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color = SweetTheme.colors.choc,
    shadowColor: Color = SweetTheme.colors.shadowDeep,
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
            .themePixelBorder(borderColor = borderColor, shadowColor = shadowColor)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
            .padding(14.dp),
        content = content
    )
}

@Composable
fun Modifier.pixelCell(
    selected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    borderColor: Color = SweetTheme.colors.line,
): Modifier = this
    .border(2.dp, borderColor)
    .background(if (selected) selectedColor else defaultColor)
