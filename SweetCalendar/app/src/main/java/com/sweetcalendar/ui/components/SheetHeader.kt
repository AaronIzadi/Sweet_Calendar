package com.sweetcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

private val ClosePixelRows = listOf(
    "x.....x",
    ".x...x.",
    "..x.x..",
    "...x...",
    "..x.x..",
    ".x...x.",
    "x.....x",
)

@Composable
fun ClosePixelIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color,
) {
    PixelIcon(
        rows = ClosePixelRows,
        palette = mapOf('x' to color),
        size = size,
        modifier = modifier
    )
}

/** Hand-drawn check stroke for SAVE / confirm (not a text glyph). */
@Composable
fun CheckPixelIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color,
) {
    Canvas(modifier.then(Modifier.size(size))) {
        val w = this.size.width
        val h = this.size.height
        val stroke = (minOf(w, h) * 0.22f).coerceAtLeast(1.5f)
        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.52f)
            lineTo(w * 0.38f, h * 0.78f)
            lineTo(w * 0.88f, h * 0.22f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Square,
                join = StrokeJoin.Miter,
                miter = 4f,
            ),
        )
    }
}

@Composable
private fun sheetHeaderBtnStyle(): TextStyle = TextStyle(
    fontFamily = PixelFont,
    fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
    lineHeight = mockupSp(MockupDimens.SHEET_HEADER_BTN_LINE),
)

/** Icon box for header actions; slightly wider than cap height so the check reads clearly. */
@Composable
private fun sheetHeaderIconSize(): Dp = mockupDp(MockupDimens.SHEET_HEADER_ICON)

/** Title area at top of sheets / dialogs (Android: actions live at the bottom). */
@Composable
fun sheetDialogTitlePaddingModifier(): Modifier = Modifier.padding(
    start = mockupDp(18),
    end = mockupDp(18),
    top = mockupDp(16),
    bottom = mockupDp(8),
)

@Composable
fun sheetFooterPaddingModifier(): Modifier = Modifier.padding(
    start = mockupDp(18),
    end = mockupDp(18),
    top = mockupDp(10),
    bottom = mockupDp(12),
)

@Composable
private fun SheetFooterDivider(modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(mockupDp(1))
            .background(colors.line.copy(alpha = 0.45f))
    )
}

@Composable
fun SheetHeaderCloseAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "CLOSE",
    enabled: Boolean = true,
) {
    val colors = SweetTheme.colors
    val iconSize = sheetHeaderIconSize()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(mockupDp(MockupDimens.SHEET_HEADER_BTN_RADIUS)))
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = mockupDp(MockupDimens.SHEET_HEADER_BTN_V_PAD)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.SHEET_HEADER_BTN_GAP))
    ) {
        ClosePixelIcon(size = iconSize, color = colors.purpleDeep)
        Text(
            label.uppercase(),
            style = sheetHeaderBtnStyle(),
            color = colors.purpleDeep,
            maxLines = 1,
        )
    }
}

@Composable
fun SheetHeaderConfirmAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = SweetTheme.colors.confirmAccent,
    showCheckIcon: Boolean = true,
    enabled: Boolean = true,
) {
    val iconSize = sheetHeaderIconSize()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(mockupDp(MockupDimens.SHEET_HEADER_BTN_RADIUS)))
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = mockupDp(MockupDimens.SHEET_HEADER_BTN_V_PAD)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.SHEET_HEADER_BTN_GAP))
    ) {
        if (showCheckIcon) {
            CheckPixelIcon(size = iconSize, color = textColor)
        }
        Text(
            label.uppercase(),
            style = sheetHeaderBtnStyle(),
            color = textColor,
            maxLines = 1,
        )
    }
}

/** Bottom action row: dismiss on the start, confirm on the end (Material / Android). */
@Composable
fun SheetFooterBar(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    closeEnabled: Boolean = true,
    closeLabel: String = "CLOSE",
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SheetFooterDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(sheetFooterPaddingModifier()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SheetHeaderCloseAction(
                onClick = onClose,
                label = closeLabel,
                enabled = closeEnabled,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                content = trailing,
            )
        }
    }
}

@Composable
fun SheetFooterCloseOnly(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    closeEnabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SheetFooterDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(sheetFooterPaddingModifier()),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SheetHeaderCloseAction(
                onClick = onClose,
                enabled = closeEnabled,
            )
        }
    }
}
