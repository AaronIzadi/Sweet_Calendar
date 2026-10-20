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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

private val CheckPixelRows = listOf(
    ".....",
    "....1",
    "...11",
    "..11.",
    ".11..",
    "11...",
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

@Composable
fun CheckPixelIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color,
) {
    PixelIcon(
        rows = CheckPixelRows,
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

@Composable
private fun sheetHeaderBtnStyle(): TextStyle = TextStyle(
    fontFamily = PixelFont,
    fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
    lineHeight = mockupSp(MockupDimens.SHEET_HEADER_BTN_LINE),
)

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
    top = mockupDp(12),
    bottom = mockupDp(16),
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
    val iconSize = mockupDp(MockupDimens.SHEET_HEADER_ICON)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(mockupDp(8)))
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = mockupDp(4)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(6))
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
    textColor: Color = SweetTheme.colors.pinkDeep,
    showCheckIcon: Boolean = true,
    enabled: Boolean = true,
) {
    val iconSize = mockupDp(MockupDimens.SHEET_HEADER_ICON)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(mockupDp(8)))
            .semantics { contentDescription = label }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = mockupDp(4)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(6))
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SheetHeaderCloseAction(
                onClick = onClose,
                enabled = closeEnabled,
            )
        }
    }
}
