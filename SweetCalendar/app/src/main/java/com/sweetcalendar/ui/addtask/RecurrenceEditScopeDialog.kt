package com.sweetcalendar.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.sweetcalendar.recurrence.RecurrenceEditScope
import com.sweetcalendar.ui.components.SheetFooterCloseOnly
import com.sweetcalendar.ui.components.SweetModalOverlay
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

enum class RecurrenceScopeMode {
    EDIT,
    DELETE,
}

@Composable
fun RecurrenceEditScopeDialog(
    mode: RecurrenceScopeMode,
    onDismiss: () -> Unit,
    onSelect: (RecurrenceEditScope) -> Unit,
) {
    val colors = SweetTheme.colors
    val title = when (mode) {
        RecurrenceScopeMode.EDIT -> "Apply changes to"
        RecurrenceScopeMode.DELETE -> "Delete recurring task"
    }

    SweetModalOverlay(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
                Text(
                    title,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(sheetDialogTitlePaddingModifier()),
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier.padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        bottom = mockupDp(8)
                    )
                ) {
                    ScopeOption(
                        label = "This task only",
                        onClick = { onSelect(RecurrenceEditScope.THIS_INSTANCE) }
                    )
                    ScopeOption(
                        label = "This and following tasks",
                        onClick = { onSelect(RecurrenceEditScope.THIS_AND_FOLLOWING) }
                    )
                    ScopeOption(
                        label = "All tasks in series",
                        onClick = { onSelect(RecurrenceEditScope.ENTIRE_SERIES) }
                    )
                }

                SheetFooterCloseOnly(onClose = onDismiss)
            }
        }
    }
}

@Composable
private fun ScopeOption(
    label: String,
    onClick: () -> Unit,
) {
    val colors = SweetTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(mockupDp(10)))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = mockupDp(12), horizontal = mockupDp(10)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = SecondaryTextWeight,
                fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
                lineHeight = mockupSp(18f)
            ),
            color = colors.ink
        )
    }
}
