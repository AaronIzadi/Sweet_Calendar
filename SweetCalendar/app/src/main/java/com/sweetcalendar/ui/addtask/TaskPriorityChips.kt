package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.sweetcalendar.ui.components.TaskPriority
import com.sweetcalendar.ui.components.ThemeSparkleIcon
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.formChipFillColor
import com.sweetcalendar.ui.theme.formChipShadowColor
import com.sweetcalendar.ui.theme.formChipShadowLayer
import com.sweetcalendar.ui.theme.formChipTextColor
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
fun TaskPriorityChip(
    priority: TaskPriority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    val iconSize = mockupDp(MockupDimens.SWATCH_ICON)
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    y = if (selected) mockupDp(3) else mockupDp(MockupDimens.FORM_FIELD_SHADOW)
                )
                .formChipShadowLayer(formChipShadowColor(selected), radius)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .clickable(onClick = onClick)
                .background(formChipFillColor(selected))
                .padding(vertical = mockupDp(12), horizontal = mockupDp(6)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mockupDp(6))
        ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                TaskPrioritySparkleRow(priority = priority)
            }
            Text(
                priority.label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SWATCH_TEXT_F)
                ),
                color = if (selected) formChipTextColor(selected) else colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TaskPrioritySparkleRow(priority: TaskPriority) {
    val count = priority.sparkleCount
    val sparkleSize = when (count) {
        1 -> mockupDp(MockupDimens.PRIORITY_SPARKLE_SLOT)
        2 -> mockupDp(8)
        else -> mockupDp(6)
    }
    val gap = if (count == 1) mockupDp(0) else mockupDp(1)
    Row(
        horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) {
            ThemeSparkleIcon(size = sparkleSize)
        }
    }
}
