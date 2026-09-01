package com.example.calendartodo.ui.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun OfflineScreen(
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFFF1DC))
                .padding(10.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠", modifier = Modifier.padding(end = 8.dp))
            Text(
                "Couldn't load holidays",
                style = MaterialTheme.typography.bodySmall,
                color = colors.chocDeep,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "← Back",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.purpleDeep,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable(onClick = onBack)
                    .padding(bottom = 24.dp)
            )
            WrappedCandyIcon(size = 100.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                "Holidays are offline for now",
                style = MaterialTheme.typography.titleMedium,
                color = colors.ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "time.ir isn't responding, so this month's occasions may be out of date. Your own tasks are saved locally and unaffected.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
            )
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.purple)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("TRY AGAIN", style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
        }
    }
}
