package com.example.calendartodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.theme.ChocolateBrown

@Composable
fun EmptyTasksState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IceCreamIcon(size = 160.dp)
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
    PinkLollipopIcon(modifier = modifier, size = 140.dp)
}
