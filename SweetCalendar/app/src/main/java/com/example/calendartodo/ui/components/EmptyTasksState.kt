package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.SkyBlue

@Composable
fun EmptyTasksIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(220.dp)) {
            val s = size.minDimension
            drawRoundRect(
                SkyBlue.copy(alpha = 0.35f),
                Offset(s * 0.1f, s * 0.15f),
                Size(s * 0.8f, s * 0.7f),
                androidx.compose.ui.geometry.CornerRadius(s * 0.06f)
            )
            drawRect(LemonYellow, Offset(s * 0.22f, s * 0.28f), Size(s * 0.56f, s * 0.08f))
            drawRect(MintGreen, Offset(s * 0.22f, s * 0.42f), Size(s * 0.4f, s * 0.08f))
            drawRect(GrapePurple.copy(alpha = 0.5f), Offset(s * 0.22f, s * 0.56f), Size(s * 0.48f, s * 0.08f))
            drawCircle(BubblegumPink, s * 0.14f, Offset(s * 0.78f, s * 0.22f))
            drawRect(ChocolateBrown, Offset(s * 0.76f, s * 0.3f), Size(s * 0.04f, s * 0.18f))
        }
    }
}

@Composable
fun EmptyTasksState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyTasksIllustration()
        Spacer(Modifier.height(16.dp))
        Text(
            "No sweets yet!",
            style = MaterialTheme.typography.titleSmall,
            color = ChocolateBrown
        )
        Text(
            "Tap Add Task below to create your first one",
            style = MaterialTheme.typography.bodySmall,
            color = ChocolateBrown.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AlarmBellIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier.size(180.dp)) {
        val s = size.minDimension
        drawCircle(LemonYellow.copy(alpha = 0.4f), s * 0.45f, Offset(s * 0.5f, s * 0.48f))
        drawCircle(BubblegumPink, s * 0.28f, Offset(s * 0.5f, s * 0.42f))
        drawRect(ChocolateBrown, Offset(s * 0.47f, s * 0.62f), Size(s * 0.06f, s * 0.12f))
        drawRect(CreamFrosting, Offset(s * 0.42f, s * 0.74f), Size(s * 0.16f, s * 0.06f))
        drawRect(ChocolateBrown, Offset(s * 0.35f, s * 0.3f), Size(s * 0.3f, s * 0.06f))
    }
}
