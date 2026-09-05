package com.example.calendartodo.ui.stats

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.StatsJarChocolateIcon
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val StatsSubLight = Color(0xFF8A7867)
private val StatsFieldLabelLight = Color(0xFF9A8878)
private val StatsBarDayLight = Color(0xFFB39D89)
private val HeatLevel1Light = Color(0xFFD9F0E4)
private val HeatLevel2Light = Color(0xFFA9E6C9)
private val HeatLevel3Dark = Color(0xFF2F8A6A)

@Composable
private fun statsSubColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else StatsSubLight
}

@Composable
private fun statsFieldLabelColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else StatsFieldLabelLight
}

@Composable
private fun statsBarDayColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else StatsBarDayLight
}

@Composable
fun StatsScreen(
    tasks: List<TaskEntity>,
    weekStartsOn: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val normalizedWeekStart = weekStartsOn.coerceIn(0, 6)
    val monthPct = remember(tasks) { computeMonthCompletionPercent(tasks) }
    val weekBars = remember(tasks, normalizedWeekStart) {
        computeWeeklyBars(tasks, normalizedWeekStart)
    }
    val heatmap = remember(tasks) { computeHeatmapLevels(tasks) }
    val heatmapColumnLabels = remember { computeHeatmapColumnLabels() }

    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
            .padding(
                start = mockupDp(18),
                end = mockupDp(18),
                top = mockupDp(6),
                bottom = mockupDp(24)
            )
    ) {
        Text(
            "Your candy jar",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.STATS_TITLE),
                lineHeight = mockupSp(22f)
            ),
            color = colors.ink,
            modifier = Modifier.padding(top = mockupDp(16), bottom = mockupDp(4))
        )
        Text(
            "A sweeter look at how you're doing",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.STATS_SUB)
            ),
            color = statsSubColor(),
            modifier = Modifier.padding(bottom = mockupDp(14))
        )

        StatsBigJarCard(monthPct)

        StatsFieldLabel("THIS WEEK")
        StatsJarCard {
            val chartHeight = MockupDimens.STATS_BAR_HEIGHT
            val labelReserve = 14f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mockupDp(chartHeight)),
                horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                verticalAlignment = Alignment.Bottom
            ) {
                weekBars.forEach { bar ->
                    val heightRatio = if (bar.ratio <= 0f) 0.08f else bar.ratio.coerceIn(0.08f, 1f)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(mockupDp(chartHeight)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(mockupDp((chartHeight - labelReserve) * heightRatio))
                                .clip(
                                    RoundedCornerShape(
                                        topStart = mockupDp(MockupDimens.STATS_BAR_RADIUS),
                                        topEnd = mockupDp(MockupDimens.STATS_BAR_RADIUS)
                                    )
                                )
                                .background(if (bar.isToday) colors.pinkDeep else colors.mintDeep)
                        )
                        Text(
                            bar.label,
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.STATS_BAR_DAY)
                            ),
                            color = statsBarDayColor(),
                            modifier = Modifier.padding(top = mockupDp(6))
                        )
                    }
                }
            }
        }

        StatsFieldLabel("STREAK HEATMAP · LAST 4 WEEKS")
        StatsJarCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = mockupDp(10)),
                verticalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.STATS_HEAT_GAP))
            ) {
                heatmap.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.STATS_HEAT_GAP))
                    ) {
                        week.forEach { level ->
                            HeatCell(level, Modifier.weight(1f))
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = mockupDp(6)),
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(MockupDimens.STATS_HEAT_GAP))
                ) {
                    heatmapColumnLabels.forEach { label ->
                        Text(
                            label,
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.STATS_BAR_DAY)
                            ),
                            color = statsBarDayColor(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsBigJarCard(monthPct: Int) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.STATS_BIG_CARD_RADIUS))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = mockupDp(16))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.STATS_BIG_CARD_SHADOW))
                .clip(shape)
                .background(colors.line)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .padding(mockupDp(18)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatsJarChocolateIcon(size = mockupDp(MockupDimens.STATS_JAR_ICON))
            Spacer(Modifier.height(mockupDp(6)))
            Text(
                "$monthPct%",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.STATS_BIG_NUM),
                    lineHeight = mockupSp(MockupDimens.STATS_BIG_NUM)
                ),
                color = colors.purpleDeep
            )
            Text(
                "TASKS COMPLETED THIS MONTH",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.STATS_BIG_LBL)
                ),
                color = statsFieldLabelColor(),
                modifier = Modifier.padding(top = mockupDp(4))
            )
        }
    }
}

@Composable
private fun StatsJarCard(content: @Composable () -> Unit) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.STATS_JAR_CARD_RADIUS))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = mockupDp(16))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.STATS_JAR_CARD_SHADOW))
                .clip(shape)
                .background(colors.line)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .padding(horizontal = mockupDp(16), vertical = mockupDp(14))
        ) {
            content()
        }
    }
}

@Composable
private fun StatsFieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = BodyFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            letterSpacing = mockupSp(0.3f)
        ),
        color = statsFieldLabelColor(),
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6))
    )
}

@Composable
private fun HeatCell(level: Int, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val cellBg = when (level) {
        0 -> if (colors.isDark) colors.cream else colors.line
        1 -> if (colors.isDark) colors.holidayBg else HeatLevel1Light
        2 -> if (colors.isDark) Color(0xFF245E4C) else HeatLevel2Light
        3 -> if (colors.isDark) HeatLevel3Dark else colors.mint
        else -> colors.mintDeep
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(mockupDp(MockupDimens.STATS_HEAT_RADIUS)))
            .background(cellBg)
    )
}
