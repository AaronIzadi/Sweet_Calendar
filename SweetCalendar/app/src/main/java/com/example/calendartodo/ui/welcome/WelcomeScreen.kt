package com.example.calendartodo.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (colors.isDark) colors.cream else androidx.compose.ui.graphics.Color(0xFFFFE1EE),
                        if (colors.isDark) colors.paper else androidx.compose.ui.graphics.Color(0xFFF3E3FB),
                        if (colors.isDark) colors.holidayBg else androidx.compose.ui.graphics.Color(0xFFE3F7EE),
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp, 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LollipopIcon(size = 64.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                "SWEET",
                style = TextStyle(fontFamily = PixelFont, fontSize = 17.sp, lineHeight = 28.sp),
                color = colors.pinkDeep,
                textAlign = TextAlign.Center
            )
            Text(
                "CALENDAR",
                style = TextStyle(fontFamily = PixelFont, fontSize = 17.sp, lineHeight = 28.sp),
                color = colors.purpleDeep,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "A Persian calendar to-do list, decorated one candy at a time.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureChip("Persian calendar") { PeppermintCandyIcon(size = 12.dp) }
                FeatureChip("Daily tasks") { WrappedCandyIcon(size = 12.dp) }
                FeatureChip("Local holidays") { ChocolateIcon(size = 12.dp) }
            }
            Spacer(Modifier.height(26.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.purple)
                    .clickable(onClick = onStart)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    "START PLANNING",
                    style = MaterialTheme.typography.labelMedium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Skip intro",
                style = MaterialTheme.typography.bodySmall,
                color = colors.purpleDeep,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onSkip)
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(colors.pinkDeep))
                Box(Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(colors.line))
                Box(Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(colors.line))
            }
        }
    }
}

@Composable
private fun FeatureChip(label: String, icon: @Composable () -> Unit) {
    val colors = SweetTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.paper)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(label, style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
            fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.85f
        ), color = colors.ink)
    }
}
