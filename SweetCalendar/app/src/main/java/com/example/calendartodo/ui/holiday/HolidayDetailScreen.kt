package com.example.calendartodo.ui.holiday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.SweetIconButton
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp

@Composable
fun HolidayDetailScreen(
    date: JalaliDate,
    title: String,
    description: String,
    isHoliday: Boolean = true,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            if (colors.isDark) colors.holidayBg else Color(0xFFDFF7EC),
                            if (colors.isDark) colors.paper else Color(0xFFF3E3FB)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            SweetIconButton(
                label = "←",
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(mockupDp(16))
            )
            PeppermintCandyIcon(size = 72.dp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                if (isHoliday) "OFFICIAL HOLIDAY" else "CALENDAR OCCASION",
                style = MaterialTheme.typography.bodySmall,
                color = colors.mintDeep,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (colors.isDark) colors.holidayBg else Color(if (isHoliday) 0xFFE3F7EE else 0xFFFFF5D6))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = colors.ink)
            Text(
                "$month ${date.day}, ${date.year} · $weekday",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.muted,
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.paper)
                    .padding(12.dp, 14.dp)
            ) {
                Text(
                    description.ifBlank {
                        if (isHoliday) {
                            "A local holiday synced from the Iranian calendar. Keep your to-do list light and enjoy the day."
                        } else {
                            "A calendar occasion synced from time.ir."
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.muted
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Synced from time.ir",
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted
            )
        }
    }
}
