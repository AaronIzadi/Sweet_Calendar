package com.example.calendartodo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

/** Centered empty-state block matching mockup `.empty-wrap`. */
@Composable
fun EmptyStateContent(
    modifier: Modifier = Modifier,
    onAddTask: (() -> Unit)? = null,
    title: String = "Nothing on the menu today",
    subtitle: String = "Your jar is empty — add a task and watch it fill up with candy as you check things off."
) {
    val colors = SweetTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IceCreamIcon(size = mockupDp(MockupDimens.EMPTY_ICON))
        Spacer(Modifier.height(mockupDp(18)))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.EMPTY_TITLE),
                lineHeight = mockupSp(20f)
            ),
            color = colors.ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(mockupDp(8)))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.EMPTY_SUB),
                lineHeight = mockupSp(MockupDimens.EMPTY_SUB * 1.6f)
            ),
            color = colors.muted,
            textAlign = TextAlign.Center
        )
        if (onAddTask != null) {
            Spacer(Modifier.height(mockupDp(20)))
            SweetPixelButton(text = "ADD A TASK", onClick = onAddTask)
        }
    }
}

@Composable
fun EmptyTasksState(modifier: Modifier = Modifier) {
    EmptyStateContent(modifier = modifier)
}

@Composable
fun AlarmBellIllustration(modifier: Modifier = Modifier) {
    PinkLollipopIcon(modifier = modifier, size = mockupDp(140))
}
