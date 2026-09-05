package com.example.calendartodo.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.selectedOnAccentTextColor

@Composable
fun JalaliDatePickerDialog(
    initialDate: JalaliDate,
    onDismiss: () -> Unit,
    onConfirm: (JalaliDate) -> Unit
) {
    val colors = SweetTheme.colors
    var visibleMonth by remember(initialDate) { mutableStateOf(initialDate.firstOfMonth()) }
    var selected by remember(initialDate) { mutableStateOf(initialDate) }
    val today = remember { JalaliDate.today() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.paper,
        titleContentColor = colors.ink,
        textContentColor = colors.ink,
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("OK", color = colors.pinkDeep)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.muted)
            }
        },
        title = {
            Text("Pick a date", color = colors.ink)
        },
        text = {
            ProvideMockupScale {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "‹",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.purpleDeep,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { visibleMonth = visibleMonth.plusMonths(-1) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Text(
                        "${JalaliDate.MONTH_NAMES_EN[visibleMonth.month - 1]} ${visibleMonth.year}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.ink
                    )
                    Text(
                        "›",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.purpleDeep,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { visibleMonth = visibleMonth.plusMonths(1) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp)
                ) {
                    JalaliDate.WEEKDAY_NAMES_EN_SHORT.forEach { label ->
                        Text(
                            label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.muted,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                val leadingBlanks = visibleMonth.firstOfMonth().weekdayIndex()
                val daysInMonth = visibleMonth.daysInMonth()
                val cells = buildList {
                    repeat(leadingBlanks) { add(null) }
                    for (d in 1..daysInMonth) {
                        add(JalaliDate(visibleMonth.year, visibleMonth.month, d))
                    }
                }
                cells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            if (date == null) {
                                Box(Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val isSelected = date == selected
                                val isToday = date == today
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isSelected -> colors.purple
                                                isToday -> colors.pink.copy(alpha = 0.35f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            selected = date
                                            visibleMonth = date.firstOfMonth()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        date.day.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) {
                                            selectedOnAccentTextColor()
                                        } else {
                                            colors.ink
                                        }
                                    )
                                }
                            }
                        }
                        repeat(7 - week.size) {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
            }
        }
    )
}
