package com.example.calendartodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.navigation.AppDestination
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.PixelPurple
import com.example.calendartodo.ui.theme.PixelPurpleHighlight
import com.example.calendartodo.ui.theme.SweetTheme

enum class TaskCategory(val label: String) {
    Personal("Personal"),
    Home("Home"),
    Work("Work");

    companion object {
        fun fromString(value: String): TaskCategory = entries.find {
            it.label.equals(value, ignoreCase = true)
        } ?: Personal
    }
}

enum class TaskCardStyle {
    /** Mockup layout: candy category icons, depth shadow, no inline actions. */
    Mockup,
    /** Full interactive card with checkbox and delete. */
    Interactive
}

enum class TaskMetaStyle {
    Default,
    /** Date · time · category — used on search results. */
    SearchResult
}

private val TaskMetaColor = Color(0xFF9A8878)

@Composable
fun TaskCategory.accentColor(): Color = when (this) {
    TaskCategory.Personal -> SweetTheme.colors.pinkDeep
    TaskCategory.Home -> SweetTheme.colors.mintDeep
    TaskCategory.Work -> SweetTheme.colors.purpleDeep
}

@Composable
fun SweetSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(MockupDimens.SECTION_LABEL_F),
            lineHeight = mockupSp(14f)
        ),
        color = SweetTheme.colors.purpleDeep,
        modifier = modifier.padding(top = 20.dp, bottom = 10.dp)
    )
}

@Composable
fun SearchGroupLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(MockupDimens.SEARCH_GROUP_LABEL),
            lineHeight = mockupSp(12f)
        ),
        color = SweetTheme.colors.purpleDeep,
        modifier = modifier.padding(top = mockupDp(16), bottom = mockupDp(8))
    )
}

@Composable
fun JarProgressCard(
    label: String,
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val shape = RoundedCornerShape(16.dp)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 3.dp)
                .clip(shape)
                .background(colors.line)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.JAR_LABEL)
                    ),
                    color = colors.ink
                )
                Text(
                    "$completed / $total",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.JAR_LABEL)
                    ),
                    color = colors.ink
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mockupDp(MockupDimens.JAR_TRACK_H))
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (colors.isDark) Color(0xFF1E1628) else Color(0xFFF1E6D8))
                    .border(2.dp, colors.line, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(mockupDp(MockupDimens.JAR_TRACK_H))
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(colors.pink, colors.mint)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun SweetTaskCard(
    task: TaskEntity,
    onClick: () -> Unit = {},
    onToggleComplete: (() -> Unit)? = null,
    onRecover: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
    highlightQuery: String? = null,
    metaStyle: TaskMetaStyle = TaskMetaStyle.Default,
    style: TaskCardStyle = TaskCardStyle.Interactive
) {
    when (style) {
        TaskCardStyle.Mockup -> MockupTaskCard(
            task = task,
            onClick = onClick,
            onToggleComplete = onToggleComplete,
            onRecover = onRecover,
            onDelete = onDelete,
            modifier = modifier,
            showDate = showDate,
            highlightQuery = highlightQuery,
            metaStyle = metaStyle
        )
        TaskCardStyle.Interactive -> InteractiveTaskCard(
            task, onClick, onToggleComplete, onDelete, modifier, showDate
        )
    }
}

@Composable
private fun MockupTaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    onToggleComplete: (() -> Unit)? = null,
    onRecover: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
    highlightQuery: String? = null,
    metaStyle: TaskMetaStyle = TaskMetaStyle.Default
) {
    val colors = SweetTheme.colors
    val category = TaskCategory.fromString(task.category)
    val accent = category.accentColor()
    val priority = TaskPriority.fromString(task.priority)
    val shape = RoundedCornerShape(14.dp)

    val taskIcon = mockupDp(MockupDimens.TASK_ICON)
    val taskAccentH = mockupDp(56)

    Box(modifier = modifier.padding(bottom = mockupDp(10))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(2))
                .clip(shape)
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (task.isDone) 0.55f else 1f)
                .clip(shape)
                .background(colors.paper),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = mockupDp(MockupDimens.TASK_ACCENT_W), height = taskAccentH)
                    .background(accent)
                    .clickable(onClick = onClick)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = mockupDp(MockupDimens.TASK_CARD_PAD_H),
                        vertical = mockupDp(MockupDimens.TASK_CARD_PAD_V)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.isDone) {
                    CheckCandyIcon(
                        size = taskIcon,
                        modifier = Modifier.clickable(
                            onClick = onToggleComplete ?: onClick,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    )
                } else {
                    Box(
                        modifier = Modifier.clickable(
                            onClick = onClick,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    ) {
                        TaskCategoryIcon(category = category, size = taskIcon)
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = mockupDp(10))
                        .weight(1f)
                        .clickable(onClick = onClick)
                ) {
                    Text(
                        text = buildMockupTaskTitle(task.title, highlightQuery, colors),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(MockupDimens.TASK_TITLE_F),
                            lineHeight = mockupSp(17f)
                        ),
                        color = colors.ink,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                    )
                    val meta = when (metaStyle) {
                        TaskMetaStyle.SearchResult -> buildList {
                            val date = JalaliDate.parseIso(task.jalaliDate)
                            add(
                                if (date == JalaliDate.today()) "Today"
                                else date.formatDisplayShort()
                            )
                            task.reminderTime?.let { add(formatTime12h(it)) }
                            if (task.category.isNotBlank()) add(task.category)
                        }
                        TaskMetaStyle.Default -> buildList {
                            task.reminderTime?.let { add(formatTime12h(it)) }
                            if (task.category.isNotBlank()) add(task.category)
                            if (showDate) add(JalaliDate.parseIso(task.jalaliDate).formatDisplayShort())
                        }
                    }
                    if (meta.isNotEmpty()) {
                        Text(
                            meta.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = mockupSp(MockupDimens.TASK_META_F),
                                lineHeight = mockupSp(13f)
                            ),
                            color = TaskMetaColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = mockupDp(2))
                        )
                    }
                }
                when {
                    onRecover != null || onDelete != null -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(mockupDp(6))
                        ) {
                            if (onRecover != null) {
                                TaskRecoverButton(onClick = onRecover)
                            }
                            if (onDelete != null) {
                                TaskTrashButton(onClick = onDelete)
                            }
                        }
                    }
                    !task.isDone && priority == TaskPriority.High -> {
                        SparkleIcon(size = mockupDp(MockupDimens.SPARKLE_ICON))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRecoverButton(onClick: () -> Unit) {
    val colors = SweetTheme.colors
    Text(
        "RECOVER",
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(MockupDimens.SNACKBAR_RECOVER),
            letterSpacing = mockupSp(0.5f),
            lineHeight = mockupSp(14f)
        ),
        color = colors.purpleDeep,
        modifier = Modifier
            .clip(RoundedCornerShape(mockupDp(MockupDimens.TRASH_BTN_RADIUS)))
            .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFBEDE3))
            .clickable(onClick = onClick)
            .padding(horizontal = mockupDp(8), vertical = mockupDp(7))
    )
}

@Composable
private fun TaskTrashButton(onClick: () -> Unit) {
    val colors = SweetTheme.colors
    Box(
        modifier = Modifier
            .size(mockupDp(MockupDimens.TRASH_BTN))
            .clip(RoundedCornerShape(mockupDp(MockupDimens.TRASH_BTN_RADIUS)))
            .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFBEDE3))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        TrashIcon(
            width = mockupDp(MockupDimens.TRASH_ICON_W),
            height = mockupDp(MockupDimens.TRASH_ICON_H)
        )
    }
}

private fun buildMockupTaskTitle(
    title: String,
    highlightQuery: String?,
    colors: com.example.calendartodo.ui.theme.SweetColors
): androidx.compose.ui.text.AnnotatedString {
    val query = highlightQuery?.trim().orEmpty()
    if (query.isEmpty()) return buildAnnotatedString { append(title) }
    val lower = title.lowercase()
    val q = query.lowercase()
    return buildAnnotatedString {
        var start = 0
        var idx = lower.indexOf(q)
        while (idx >= 0) {
            append(title.substring(start, idx))
            withStyle(SpanStyle(background = colors.lemon, color = colors.ink)) {
                append(title.substring(idx, idx + q.length))
            }
            start = idx + q.length
            idx = lower.indexOf(q, start)
        }
        append(title.substring(start))
    }
}

@Composable
private fun InteractiveTaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    onToggleComplete: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
    showDate: Boolean = false
) {
    val colors = SweetTheme.colors
    val category = TaskCategory.fromString(task.category)
    val accent = category.accentColor()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.paper),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 56.dp)
                .background(accent)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onToggleComplete != null) {
                TaskCheckbox(
                    checked = task.isDone,
                    onClick = onToggleComplete,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            LegacyCategoryIcon(category = category, size = 16.dp)
            Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (task.isDone) colors.muted else colors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                val meta = buildList {
                    task.reminderTime?.let { add(formatTime12h(it)) }
                    if (task.category.isNotBlank()) add(task.category)
                    if (showDate) add(JalaliDate.parseIso(task.jalaliDate).formatDisplayShort())
                }
                if (meta.isNotEmpty()) {
                    Text(
                        meta.joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.85f
                        ),
                        color = colors.muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (onDelete != null) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (colors.isDark) Color(0xFF3A2A22) else Color(0xFFFBEDE3))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗑", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun TaskCheckbox(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .then(
                if (checked) {
                    Modifier.background(colors.mintDeep)
                } else {
                    Modifier.border(2.dp, colors.line, RoundedCornerShape(6.dp))
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Text("✓", style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
private fun TaskCategoryIcon(category: TaskCategory, size: androidx.compose.ui.unit.Dp) {
    when (category) {
        TaskCategory.Personal -> TaskHeartIcon(size = size)
        TaskCategory.Home -> TaskLeafIcon(size = size)
        TaskCategory.Work -> TaskGemIcon(size = size)
    }
}

@Composable
private fun LegacyCategoryIcon(category: TaskCategory, size: androidx.compose.ui.unit.Dp) {
    when (category) {
        TaskCategory.Personal -> PeppermintCandyIcon(size = size)
        TaskCategory.Home -> ChocolateIcon(size = size)
        TaskCategory.Work -> WrappedCandyIcon(size = size)
    }
}

fun formatTime12h(time24: String): String {
    val parts = time24.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return time24
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val amPm = if (h < 12) "AM" else "PM"
    val h12 = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else -> h
    }
    return if (m == 0) "$h12:00 $amPm" else "%d:%02d %s".format(h12, m, amPm)
}

fun JalaliDate.formatDisplayShort(calendarSystem: CalendarSystem = CalendarSystem.PERSIAN): String =
    when (calendarSystem) {
        CalendarSystem.PERSIAN -> "${JalaliDate.MONTH_NAMES_EN[month - 1]} $day"
        CalendarSystem.GREGORIAN -> {
            val g = GregorianDate.fromJalali(this)
            "${GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}"
        }
    }

fun JalaliDate.formatDisplayWithWeekday(calendarSystem: CalendarSystem = CalendarSystem.PERSIAN): String {
    val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[weekdayIndex()]
    return when (calendarSystem) {
        CalendarSystem.PERSIAN -> "${JalaliDate.MONTH_NAMES_EN[month - 1]} $day, $year · $weekday"
        CalendarSystem.GREGORIAN -> {
            val g = GregorianDate.fromJalali(this)
            "${GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}, ${g.year} · $weekday"
        }
    }
}

fun JalaliDate.formatAlternateCalendarLine(calendarSystem: CalendarSystem): String =
    when (calendarSystem) {
        CalendarSystem.PERSIAN -> {
            val g = GregorianDate.fromJalali(this)
            "${GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}, ${g.year}"
        }
        CalendarSystem.GREGORIAN -> "${JalaliDate.MONTH_NAMES_EN[month - 1]} $day, $year"
    }

/** Square paper icon button matching mockup `.icon-btn` (detail headers, etc.). */
@Composable
fun SweetIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.DETAIL_ICON_BTN_RADIUS)
    val size = mockupDp(MockupDimens.DETAIL_ICON_BTN)
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.line)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun SweetIconButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    SweetIconButton(onClick = onClick, modifier = modifier) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.DETAIL_ICON_BTN_FONT)
            ),
            color = colors.purpleDeep
        )
    }
}

@Composable
fun SweetFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(16.dp)
    Box(modifier = modifier.size(mockupDp(MockupDimens.FAB_SIZE))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(4))
                .clip(shape)
                .background(colors.purpleDeep)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(colors.purple)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            FabGumdropIcon(size = mockupDp(MockupDimens.FAB_ICON))
        }
    }
}

@Composable
fun SweetBottomNav(
    selected: AppDestination,
    onSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val navColor = colors.purpleDeep
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mockupDp(3))
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.paper)
                .padding(
                    start = mockupDp(6),
                    end = mockupDp(6),
                    top = mockupDp(10),
                    bottom = mockupDp(16)
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "TODAY",
                selected = selected == AppDestination.Today,
                onClick = { onSelect(AppDestination.Today) }
            ) {
                NavLollipopIcon(
                    size = mockupDp(MockupDimens.NAV_ICON),
                    headColor = navColor,
                    highlightColor = PixelPurpleHighlight,
                    stickColor = PixelPurple
                )
            }
            NavItem(
                label = "WEEK",
                selected = selected == AppDestination.Week,
                onClick = { onSelect(AppDestination.Week) }
            ) {
                NavPeppermintIcon(
                    size = mockupDp(MockupDimens.NAV_ICON),
                    color = navColor,
                    highlightColor = PixelPurpleHighlight
                )
            }
            NavItem(
                label = "MONTH",
                selected = selected == AppDestination.Month,
                onClick = { onSelect(AppDestination.Month) }
            ) {
                NavMonthGridIcon(
                    size = mockupDp(MockupDimens.NAV_ICON_LARGE),
                    color = navColor
                )
            }
            NavItem(
                label = "SETTINGS",
                selected = selected == AppDestination.Settings,
                onClick = { onSelect(AppDestination.Settings) }
            ) {
                NavSettingsGearIcon(
                    size = mockupDp(MockupDimens.NAV_ICON_LARGE),
                    color = navColor
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    val colors = SweetTheme.colors
    val iconSlotW = mockupDp(MockupDimens.NAV_ICON_SLOT)
    val iconSlotH = mockupDp(MockupDimens.NAV_ICON_SLOT_H)
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(mockupDp(12)))
            .background(if (selected) colors.navActiveBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = mockupDp(10), vertical = mockupDp(6)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(mockupDp(4))
    ) {
        Box(
            modifier = Modifier
                .width(iconSlotW)
                .height(iconSlotH),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Text(
            label,
            style = TextStyle(
                fontFamily = PixelFont,
                fontSize = mockupSp(MockupDimens.NAV_LABEL_F),
                lineHeight = mockupSp(10f)
            ),
            color = if (selected) colors.pinkDeep else colors.navInactive
        )
    }
}

@Composable
fun StreakPill(streak: Int, modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(10.dp)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 2.dp)
                .clip(shape)
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .clip(shape)
                .background(colors.streakBg)
                .padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            SettingsChocolateIcon(size = mockupDp(MockupDimens.STREAK_ICON))
            Text(
                streak.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.STREAK_FONT)
                ),
                color = colors.chocDeep
            )
        }
    }
}

/** Primary CTA matching mockup `.pixel-btn` (Press Start 2P + purple depth shadow). */
@Composable
fun SweetPixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(4))
                .clip(RoundedCornerShape(mockupDp(12)))
                .background(colors.purpleDeep)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(mockupDp(12)))
                .background(colors.purple)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = mockupDp(MockupDimens.PIXEL_BTN_PAD_H),
                    vertical = mockupDp(MockupDimens.PIXEL_BTN_PAD_V)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.PIXEL_BTN),
                    letterSpacing = mockupSp(0.5f),
                    lineHeight = mockupSp(14f)
                ),
                color = Color.White
            )
        }
    }
}

/** Full-width pink CTA matching mockup `.big-save-btn` on the add-task screen. */
@Composable
fun SweetBigSaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.BIG_SAVE_RADIUS)
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.BIG_SAVE_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.pinkDeep)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.pink)
                .clickable(onClick = onClick)
                .padding(mockupDp(16)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.BIG_SAVE_BTN),
                    lineHeight = mockupSp(15f),
                    textAlign = TextAlign.Center
                ),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Mockup `.switch` toggle used on settings and add-task rows. */
@Composable
fun SweetSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    Box(
        modifier = modifier
            .size(width = mockupDp(36), height = mockupDp(20))
            .clip(RoundedCornerShape(mockupDp(10)))
            .background(if (checked) colors.mintDeep else colors.line)
            .clickable { onCheckedChange(!checked) }
            .padding(mockupDp(2))
    ) {
        Box(
            modifier = Modifier
                .size(mockupDp(16))
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
