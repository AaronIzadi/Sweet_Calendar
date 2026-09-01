package com.example.calendartodo.ui.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun NotificationPermissionScreen(
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (colors.isDark) colors.cream else Color(0xFFFFF6EA),
                        if (colors.isDark) colors.paper else Color(0xFFF3E3FB)
                    )
                )
            )
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WrappedCandyIcon(size = 80.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            "Never miss a sweet reminder",
            style = MaterialTheme.typography.titleMedium,
            color = colors.ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Sweet Calendar can notify you a little before each task is due — you can turn this off anytime in Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4f
        )
        Spacer(Modifier.height(26.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.purple)
                .clickable(onClick = onAllow)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("ALLOW NOTIFICATIONS", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Not now",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.muted,
            modifier = Modifier
                .clickable(onClick = onDismiss)
                .padding(12.dp)
        )
    }
}
