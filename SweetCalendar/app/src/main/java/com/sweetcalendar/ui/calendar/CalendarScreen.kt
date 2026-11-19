package com.sweetcalendar.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.animation.core.Spring
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.rememberReducedMotion
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sweetcalendar.data.local.TaskEntity
import com.sweetcalendar.calendar.CalendarSystem
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import com.sweetcalendar.ui.components.CompletedSectionLink
import com.sweetcalendar.ui.components.MonthNavChevron
import com.sweetcalendar.ui.components.SweetSectionLabel
import com.sweetcalendar.ui.components.SweetTaskCard
import com.sweetcalendar.ui.components.TaskCardStyle
import com.sweetcalendar.ui.components.TaskMetaStyle
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SprinklesBlue
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
private fun monthWeekdayHeaderColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else colors.chocDeep
}

@Composable
private fun monthLegendTextColor(): Color = SweetTheme.colors.muted

private data class MonthPage(
    val system: CalendarSystem,
    val persianMonth: JalaliDate,
    val gregorianMonth: GregorianDate,
)

private data class MonthSnapshot(
    val page: MonthPage,
    val cells: List<DayCellInfo?>,
)

private fun CalendarUiState.toMonthPage(): MonthPage =
    MonthPage(calendarSystem, visibleMonth, visibleGregorianMonth)

private fun MonthPage.ordinal(): Int = when (system) {
    CalendarSystem.PERSIAN -> persianMonth.year * 12 + persianMonth.month
    CalendarSystem.GREGORIAN -> gregorianMonth.year * 12 + gregorianMonth.month
}

private val monthSlideSpec = tween<IntOffset>(SweetMotion.MediumMillis, easing = FastOutSlowInEasing)
private val monthFadeSpec = tween<Float>(SweetMotion.ShortMillis, easing = FastOutSlowInEasing)

@Composable
fun MonthScreen(
    viewModel: CalendarViewModel,
    onDayClick: (JalaliDate) -> Unit,
    onOfflineClick: () -> Unit = {},
    onEditTask: (TaskEntity) -> Unit = {},
    onCompleteTask: (TaskEntity) -> Unit = {},
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
                    .background(colors.bannerBg)
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
                        color = if (colors.isDark) colors.lemon else colors.chocDeep
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
            MonthNavChevron(pointingLeft = true, onClick = viewModel::goToPreviousMonth)
            AnimatedMonthTitle(page = state.toMonthPage())
            MonthNavChevron(pointingLeft = false, onClick = viewModel::goToNextMonth)
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
                    color = monthWeekdayHeaderColor()
                )
            }
        }

        val monthSnapshot = MonthSnapshot(state.toMonthPage(), state.monthCells)
        AnimatedMonthGrid(
            snapshot = monthSnapshot,
            today = today,
            selectedDate = state.selectedDate,
            onDayClick = { date ->
                viewModel.selectDate(date)
                onDayClick(date)
            },
        )

        CalendarActivityLegend(
            showHoliday = state.showHolidays,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
        )

        val pendingTodos = state.monthTodos.filter { !it.isDone }
        val completedTodos = state.monthTodos.filter { it.isDone }
        if (pendingTodos.isNotEmpty() || completedTodos.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                SweetSectionLabel("TO DO")
                pendingTodos.forEach { task ->
                    SweetTaskCard(
                        task = task,
                        onClick = { onEditTask(task) },
                        onToggleComplete = { onCompleteTask(task) },
                        style = TaskCardStyle.Mockup,
                        metaStyle = TaskMetaStyle.Deadline,
                    )
                }
                if (completedTodos.isNotEmpty()) {
                    CompletedSectionLink(
                        count = completedTodos.size,
                        suffix = "this month",
                    )
                    completedTodos.forEach { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = { onEditTask(task) },
                            onToggleComplete = { onCompleteTask(task) },
                            style = TaskCardStyle.Mockup,
                            metaStyle = TaskMetaStyle.Deadline,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(90.dp))
    }
}

private enum class DotShape { Circle, Diamond, Leaf, Ring, Square }

@Composable
private fun AnimatedMonthTitle(page: MonthPage) {
    val reducedMotion = rememberReducedMotion()
    AnimatedContent(
        targetState = page,
        contentKey = { it.ordinal() },
        transitionSpec = {
            if (reducedMotion) {
                fadeIn(monthFadeSpec) togetherWith fadeOut(monthFadeSpec)
            } else {
                val dir = when {
                    targetState.ordinal() > initialState.ordinal() -> 1
                    targetState.ordinal() < initialState.ordinal() -> -1
                    else -> 0
                }
                (fadeIn(monthFadeSpec) + slideInHorizontally(monthSlideSpec) { dir * it / 6 }) togetherWith
                    (fadeOut(monthFadeSpec) + slideOutHorizontally(monthSlideSpec) { -dir * it / 6 })
            }
        },
        label = "monthTitle",
    ) { activePage ->
        MonthTitleContent(activePage)
    }
}

@Composable
private fun MonthTitleContent(page: MonthPage) {
    val colors = SweetTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (page.system) {
            CalendarSystem.PERSIAN -> {
                Text(
                    "${JalaliDate.MONTH_NAMES_EN[page.persianMonth.month - 1]} ${page.persianMonth.year}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.MONTH_TITLE),
                        lineHeight = mockupSp(21f)
                    ),
                    color = colors.ink
                )
                val gregorianMonth = page.persianMonth.toGregorianCalendar()
                val monthName = DateFormatSymbols(Locale.ENGLISH).months[gregorianMonth.get(java.util.Calendar.MONTH)]
                Text(
                    "$monthName ${gregorianMonth.get(java.util.Calendar.YEAR)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = SecondaryTextWeight,
                        fontSize = mockupSp(MockupDimens.MONTH_SUB),
                        lineHeight = mockupSp(15f)
                    ),
                    color = colors.muted
                )
            }
            CalendarSystem.GREGORIAN -> {
                val gMonth = page.gregorianMonth
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
                        fontWeight = SecondaryTextWeight,
                        fontSize = mockupSp(MockupDimens.MONTH_SUB),
                        lineHeight = mockupSp(15f)
                    ),
                    color = colors.muted
                )
            }
        }
    }
}

@Composable
private fun AnimatedMonthGrid(
    snapshot: MonthSnapshot,
    today: JalaliDate,
    selectedDate: JalaliDate,
    onDayClick: (JalaliDate) -> Unit,
) {
    val reducedMotion = rememberReducedMotion()
    AnimatedContent(
        targetState = snapshot,
        contentKey = { it.page.ordinal() },
        transitionSpec = {
            if (reducedMotion) {
                fadeIn(monthFadeSpec) togetherWith fadeOut(monthFadeSpec)
            } else {
                val dir = when {
                    targetState.page.ordinal() > initialState.page.ordinal() -> 1
                    targetState.page.ordinal() < initialState.page.ordinal() -> -1
                    else -> 0
                }
                (slideInHorizontally(monthSlideSpec) { dir * it } + fadeIn(monthFadeSpec)) togetherWith
                    (slideOutHorizontally(monthSlideSpec) { -dir * it } + fadeOut(monthFadeSpec))
            }
        },
        label = "monthGrid",
    ) { active ->
        val weeks = active.cells.chunked(7)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    week.forEach { cell ->
                        MonthDayCell(
                            cell = cell,
                            isToday = cell?.date == today,
                            isSelected = cell?.date == selectedDate,
                            modifier = Modifier.weight(1f),
                            onClick = onDayClick,
                        )
                    }
                    repeat(7 - week.size) {
                        MonthDayCell(
                            cell = null,
                            isToday = false,
                            isSelected = false,
                            modifier = Modifier.weight(1f),
                            onClick = {},
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    cell: DayCellInfo?,
    isToday: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (JalaliDate) -> Unit
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(9.dp)
    val reducedMotion = rememberReducedMotion()

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
    val textColor = when {
        isToday -> colors.onPink
        isSelected -> colors.purpleDeep
        else -> colors.ink
    }
    val shadowColor = when {
        isToday -> colors.pinkDeep
        else -> colors.shadow
    }
    val shadowOffset = if (isToday) 3.dp else 2.dp

    val selectionScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "daySelection",
    )

    val todayPulse = if (isToday && !reducedMotion) {
        val transition = rememberInfiniteTransition(label = "todayPulse")
        transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "todayPulseScale",
        ).value
    } else {
        1f
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = todayPulse
                scaleY = todayPulse
            },
    ) {
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
            if (selectionScale > 0.01f) {
                val circleSize = 0.78f * selectionScale.coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxSize(circleSize)
                        .clip(CircleShape)
                        .background(
                            if (isToday) {
                                Color.White.copy(alpha = 0.22f)
                            } else {
                                colors.purple.copy(alpha = 0.22f)
                            }
                        ),
                )
            }
            Text(
                cell.displayDay.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.MONTH_DAY_NUM_F),
                    lineHeight = mockupSp(14f)
                ),
                color = textColor
            )
            val hasMarkers =
                cell.hasScheduledTask || cell.hasTodo || cell.hasHabit ||
                    cell.hasOccasion || cell.isHoliday
            if (hasMarkers) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (cell.hasScheduledTask) {
                        CategoryDot(colors.purpleDeep, DotShape.Circle, isToday)
                    }
                    if (cell.hasTodo) {
                        CategoryDot(colors.pinkDeep, DotShape.Circle, isToday)
                    }
                    if (cell.hasHabit) {
                        CategoryDot(SprinklesBlue, DotShape.Circle, isToday)
                    }
                    if (cell.hasOccasion) {
                        CategoryDot(colors.lemonDeep, DotShape.Circle, isToday)
                    }
                    if (cell.isHoliday) {
                        CategoryDot(colors.mintDeep, DotShape.Circle, isToday)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarAlignedDot(
    color: Color,
    size: Dp,
    hollow: Boolean,
    holeColor: Color,
) {
    val stroke = mockupDp(1.5f)
    val holeScale = with(LocalDensity.current) {
        1f - (stroke.toPx() * 2f / size.toPx()).coerceIn(0.15f, 0.75f)
    }
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        if (hollow) {
            Box(Modifier.fillMaxSize().clip(CircleShape).background(color))
            Box(
                Modifier
                    .fillMaxSize(holeScale)
                    .clip(CircleShape)
                    .background(holeColor),
            )
        } else {
            Box(Modifier.fillMaxSize().clip(CircleShape).background(color))
        }
    }
}

@Composable
private fun CalendarActivityLegend(showHoliday: Boolean, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val legendDot = mockupDp(MockupDimens.MONTH_LEGEND_DOT)
    val entries = buildList {
        add(colors.purpleDeep to "Task")
        add(colors.pinkDeep to "To-do")
        add(SprinklesBlue to "Habit")
        if (showHoliday) {
            add(colors.lemonDeep to "Occasion")
            add(colors.mintDeep to "Holiday")
        }
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        entries.forEach { (color, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(5)),
            ) {
                CalendarAlignedDot(
                    color = color,
                    size = legendDot,
                    hollow = false,
                    holeColor = colors.cream,
                )
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.MONTH_LEGEND_TEXT_F),
                        lineHeight = mockupSp(13f),
                    ),
                    color = monthLegendTextColor(),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun CategoryDot(
    color: Color,
    shape: DotShape,
    onToday: Boolean,
    holeColor: Color = SweetTheme.colors.paper,
) {
    val dotColor = if (onToday) Color.White.copy(alpha = 0.9f) else color
    val cellDot = mockupDp(MockupDimens.MONTH_CELL_DOT)
    when (shape) {
        DotShape.Circle -> CalendarAlignedDot(dotColor, cellDot, hollow = false, holeColor = holeColor)
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
        DotShape.Ring -> CalendarAlignedDot(dotColor, cellDot, hollow = true, holeColor = holeColor)
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
