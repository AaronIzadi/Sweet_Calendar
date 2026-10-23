package com.sweetcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp

@Composable
fun MonthNavChevron(
    pointingLeft: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(6))
    val iconSize = mockupDp(MockupDimens.CHEVRON_ICON)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(1.5f))
                .clip(shape)
                .background(colors.shadow)
        )
        Box(
            modifier = Modifier
                .size(mockupDp(MockupDimens.CHEVRON_SIZE))
                .clip(shape)
                .background(colors.paper)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (pointingLeft) {
                BackArrowMiniIcon(size = iconSize, color = colors.purpleDeep)
            } else {
                ForwardArrowMiniIcon(size = iconSize, color = colors.purpleDeep)
            }
        }
    }
}
