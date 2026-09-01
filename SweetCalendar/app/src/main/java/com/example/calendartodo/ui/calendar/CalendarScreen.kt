package com.example.calendartodo.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import java.text.DateFormatSymbols
import java.util.Locale

private val WeekdayHeaderColor = Color(0xFFB39D89)
private val LegendTextColor = Color(0xFF7A6A5C)

@Composable
fun MonthScreen(
    viewModel: CalendarViewModel,
    onDayClick: (JalaliDate) -> Unit,
    onOfflineClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = SweetTheme.colors
    val today = JalaliDate.today()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
    ) {
        if (state.eventsLoadFailed && state.showHolidays) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFFF1DC))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOfflineClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "Couldn't load holidays",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.chocDeep
                    )
                }
                Text(
                    "Retry",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.purpleDeep,
                    modifier = Modifier.clickable { viewModel.retryEventsLoad() }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonthChevron("‹", onClick = viewModel::goToPreviousMonth)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (state.calendarSystem) {
                    CalendarSystem.PERSIAN -> {
                        Text(
                            "${JalaliDate.MONTH_NAMES_EN[state.visibleMonth.month - 1]} ${state.visibleMonth.year}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.MONTH_TITLE),
                                lineHeight = mockupSp(21f)
                            ),
                            color = colors.ink
                        )
                        val gregorianMonth = state.visibleMonth.toGregorianCalendar()
                        val monthName = DateFormatSymbols(Locale.ENGLISH).months[gregorianMonth.get(java.util.Calendar.MONTH)]
                        Text(
                            "$monthName ${gregorianMonth.get(java.util.Calendar.YEAR)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = mockupSp(MockupDimens.MONTH_SUB),
                                lineHeight = mockupSp(15f)
                            ),
                            color = colors.muted
                        )
                    }
                    CalendarSystem.GREGORIAN -> {
                        val gMonth = state.visibleGregorianMonth
                        Text(
                            "${GregorianDate.MONTH_NAMES_EN[gMonth.month - 1]} ${gMonth.year}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.MONTH_TITLE),
                                lineHeight = mockupSp(21f)
                            ),
                            color = colors.ink
                        )
                        val jalali = gMonth.toJalali()
                        Text(
                            "${JalaliDate.MONTH_NAMES_EN[jalali.month - 1]} ${jalali.year}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = mockupSp(MockupDimens.MONTH_SUB),
                                lineHeight = mockupSp(15f)
                            ),
                            color = colors.muted
                        )
                    }
                }
            }
            MonthChevron("›", onClick = viewModel::goToNextMonth)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 2.dp)
        ) {
            JalaliDate.weekdayLabels(state.weekStartsOn).forEach { name ->
                Text(
                    name.take(2).uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = PixelFont,
                        fontSize = mockupSp(MockupDimens.WEEKDAY_HEADER_F),
                        lineHeight = mockupSp(12f),
                        fontWeight = FontWeight.Bold
                    ),
                    color = WeekdayHeaderColor
                )
            }
        }

        val weeks = state.monthCells.chunked(7)
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    week.forEach { cell ->
                        MonthDayCell(
                            cell = cell,
                            isToday = cell?.date == today,
                            modifier = Modifier.weight(1f),
                            onClick = { date ->
                                viewModel.selectDate(date)
                                onDayClick(date)
                            }
                        )
                    }
                    repeat(7 - week.size) {
                        MonthDayCell(
                            cell = null,
                            isToday = false,
                            modifier = Modifier.weight(1f),
                            onClick = {}
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LegendItem(color = colors.pinkDeep, shape = DotShape.Circle, label = "Personal")
            LegendItem(color = colors.purpleDeep, shape = DotShape.Diamond, label = "Work")
            LegendItem(color = colors.mintDeep, shape = DotShape.Leaf, label = "Home")
            if (state.showHolidays) {
                LegendItem(color = colors.mintDeep, shape = DotShape.Ring, label = "Holiday")
            }
        }
        Spacer(Modifier.height(90.dp))
    }
}

private enum class DotShape { Circle, Diamond, Leaf, Ring, Square }

@Composable
private fun MonthChevron(label: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(8.dp)
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 2.dp)
                .clip(shape)
                .background(colors.line)
        )
        Box(
            modifier = Modifier
                .size(mockupDp(MockupDimens.CHEVRON_SIZE))
                .clip(shape)
                .background(colors.paper)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.CHEVRON_FONT)
                ),
                color = colors.purpleDeep
            )
        }
    }
}

@Composable
private fun MonthDayCell(
    cell: DayCellInfo?,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: (JalaliDate) -> Unit
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(9.dp)

    if (cell == null) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .alpha(0.35f)
        )
        return
    }

    val isFriday = cell.date.weekdayIndex() == 6
    val bg = when {
        isToday -> colors.pink
        cell.isHoliday -> colors.holidayBg
        isFriday -> colors.weekendBg
        else -> colors.paper
    }
    val textColor = if (isToday) Color.White else colors.ink
    val shadowColor = when {
        isToday -> colors.pinkDeep
        else -> colors.line
    }
    val shadowOffset = if (isToday) 3.dp else 2.dp

    Box(modifier = modifier.aspectRatio(1f)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowOffset)
                .clip(shape)
                .background(shadowColor)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(bg)
                .clickable { onClick(cell.date) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                cell.displayDay.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.MONTH_DAY_NUM_F),
                    lineHeight = mockupSp(14f)
                ),
                color = textColor
            )
            val hasMarkers = cell.hasTask || cell.isHoliday || cell.hasOccasion
            if (hasMarkers) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    cell.taskCategories.forEach { category ->
                        val (color, dotShape) = categoryDotStyleComposable(category)
                        CategoryDot(color, dotShape, isToday)
                    }
                    if (cell.hasOccasion) {
                        CategoryDot(colors.lemonDeep, DotShape.Square, isToday)
                    }
                    if (cell.isHoliday) {
                        CategoryDot(colors.mintDeep, DotShape.Ring, isToday)
                    }
                }
            }
        }
    }
}

@Composable
private fun categoryDotStyleComposable(category: TaskCategory): Pair<Color, DotShape> {
    val colors = SweetTheme.colors
    return when (category) {
        TaskCategory.Personal -> colors.pinkDeep to DotShape.Circle
        TaskCategory.Work -> colors.purpleDeep to DotShape.Diamond
        TaskCategory.Home -> colors.mintDeep to DotShape.Leaf
    }
}

@Composable
private fun CategoryDot(color: Color, shape: DotShape, onToday: Boolean) {
    val dotColor = if (onToday) Color.White.copy(alpha = 0.9f) else color
    val cellDot = mockupDp(MockupDimens.MONTH_CELL_DOT)
    when (shape) {
        DotShape.Circle -> Box(Modifier.size(cellDot).clip(CircleShape).background(dotColor))
        DotShape.Diamond -> Box(
            Modifier
                .size(cellDot)
                .graphicsLayer { rotationZ = 45f }
                .clip(RoundedCornerShape(1.dp))
                .background(dotColor)
        )
        DotShape.Leaf -> Box(
            Modifier
                .size(cellDot)
                .clip(
                    RoundedCornerShape(
                        topStartPercent = 0,
                        topEndPercent = 50,
                        bottomEndPercent = 0,
                        bottomStartPercent = 50
                    )
                )
                .background(dotColor)
        )
        DotShape.Square -> Box(Modifier.size(cellDot).clip(RoundedCornerShape(mockupDp(1))).background(dotColor))
        DotShape.Ring -> Box(Modifier.size(cellDot).border(mockupDp(1.5f), dotColor, CircleShape))
    }
}

@Composable
private fun LegendItem(color: Color, shape: DotShape, label: String) {
    val legendDot = mockupDp(MockupDimens.MONTH_LEGEND_DOT)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(5))
    ) {
        Box(Modifier.size(legendDot), contentAlignment = Alignment.Center) {
            when (shape) {
                DotShape.Circle -> Box(Modifier.size(legendDot).clip(CircleShape).background(color))
                DotShape.Diamond -> Box(
                    Modifier
                        .size(mockupDp(MockupDimens.MONTH_LEGEND_DOT - 2))
                        .graphicsLayer { rotationZ = 45f }
                        .clip(RoundedCornerShape(1.dp))
                        .background(color)
                )
                DotShape.Leaf -> Box(
                    Modifier
                        .size(legendDot)
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 0,
                                topEndPercent = 50,
                                bottomEndPercent = 0,
                                bottomStartPercent = 50
                            )
                        )
                        .background(color)
                )
                DotShape.Ring -> Box(Modifier.size(legendDot).border(mockupDp(1.5f), color, CircleShape))
                DotShape.Square -> Box(Modifier.size(legendDot).clip(RoundedCornerShape(mockupDp(1))).background(color))
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.MONTH_LEGEND_TEXT_F),
                lineHeight = mockupSp(13f)
            ),
            color = LegendTextColor
        )
    }
}

/** Kept for day-detail navigation from month grid */
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBack: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit
) {
    MonthScreen(viewModel = viewModel, onDayClick = { })
}
