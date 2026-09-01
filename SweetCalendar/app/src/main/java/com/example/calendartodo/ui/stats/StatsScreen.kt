package com.example.calendartodo.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun StatsScreen(
    tasks: List<TaskEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val monthPct = remember(tasks) { computeMonthCompletionPercent(tasks) }
    val weekBars = remember(tasks) { computeWeeklyBars(tasks) }
    val heatmap = remember(tasks) { computeHeatmapLevels(tasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "←",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.purpleDeep,
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .clickableNoRipple(onBack)
                    .padding(end = 12.dp)
            )
            Column {
                Text("Your candy jar", style = MaterialTheme.typography.headlineMedium, color = colors.ink)
                Text(
                    "A sweeter look at how you're doing",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.paper)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChocolateIcon(size = 40.dp)
            Spacer(Modifier.height(6.dp))
            Text("$monthPct%", style = MaterialTheme.typography.headlineMedium, color = colors.purpleDeep)
            Text(
                "TASKS COMPLETED THIS MONTH",
                style = MaterialTheme.typography.labelSmall,
                color = colors.muted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        SweetSectionLabel("THIS WEEK", modifier = Modifier.padding(horizontal = 18.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.paper)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                weekBars.forEach { bar ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((90 * bar.ratio.coerceIn(0.05f, 1f)).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (bar.isToday) colors.pinkDeep else colors.mintDeep)
                        )
                        Text(
                            bar.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.muted,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        SweetSectionLabel("STREAK HEATMAP · LAST 4 WEEKS", modifier = Modifier.padding(horizontal = 18.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.paper)
                .padding(14.dp)
        ) {
            val rows = heatmap.chunked(7)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        week.forEach { level ->
                            HeatCell(level, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HeatCell(level: Int, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val cellBg = when (level) {
        0 -> if (colors.isDark) colors.cream else colors.line
        1 -> if (colors.isDark) colors.holidayBg else androidx.compose.ui.graphics.Color(0xFFD9F0E4)
        2 -> if (colors.isDark) androidx.compose.ui.graphics.Color(0xFF245E4C) else androidx.compose.ui.graphics.Color(0xFFA9E6C9)
        3 -> colors.mint
        else -> colors.mintDeep
    }
    Box(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(cellBg)
    )
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return this.then(
        clickable(
            interactionSource = interaction,
            indication = null,
            onClick = onClick
        )
    )
}
