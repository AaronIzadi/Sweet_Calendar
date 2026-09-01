package com.example.calendartodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.navigation.AppDestination
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp
import com.example.calendartodo.ui.theme.PixelFont
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
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showDate: Boolean = false,
    style: TaskCardStyle = TaskCardStyle.Interactive
) {
    when (style) {
        TaskCardStyle.Mockup -> MockupTaskCard(task, onClick, modifier, showDate)
        TaskCardStyle.Interactive -> InteractiveTaskCard(
            task, onClick, onToggleComplete, onDelete, modifier, showDate
        )
    }
}

@Composable
private fun MockupTaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDate: Boolean = false
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
                .background(colors.paper)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = mockupDp(MockupDimens.TASK_ACCENT_W), height = taskAccentH)
                    .background(accent)
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
                    CheckCandyIcon(size = taskIcon)
                } else {
                    TaskCategoryIcon(category = category, size = taskIcon)
                }
                Column(modifier = Modifier.padding(start = mockupDp(10)).weight(1f)) {
                    Text(
                        task.title,
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
                    val meta = buildList {
                        task.reminderTime?.let { add(formatTime12h(it)) }
                        if (task.category.isNotBlank()) add(task.category)
                        if (showDate) add(JalaliDate.parseIso(task.jalaliDate).formatDisplayShort())
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
                if (!task.isDone && priority == TaskPriority.High) {
                    SparkleIcon(size = mockupDp(MockupDimens.SPARKLE_ICON))
                }
            }
        }
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

fun JalaliDate.formatDisplayShort(): String =
    "${JalaliDate.MONTH_NAMES_EN[month - 1]} $day"

fun JalaliDate.formatDisplayWithWeekday(): String {
    val weekday = JalaliDate.WEEKDAY_NAMES_EN_SHORT[weekdayIndex()]
    return "${JalaliDate.MONTH_NAMES_EN[month - 1]} $day, $year · $weekday"
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
            GummyIcon(size = mockupDp(MockupDimens.FAB_ICON), color = colors.lemon)
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.paper)
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            label = "TODAY",
            selected = selected == AppDestination.Today,
            onClick = { onSelect(AppDestination.Today) }
        ) { LollipopIcon(size = mockupDp(MockupDimens.NAV_ICON)) }
        NavItem(
            label = "WEEK",
            selected = selected == AppDestination.Week,
            onClick = { onSelect(AppDestination.Week) }
        ) { PeppermintCandyIcon(size = mockupDp(MockupDimens.NAV_ICON)) }
        NavItem(
            label = "MONTH",
            selected = selected == AppDestination.Month,
            onClick = { onSelect(AppDestination.Month) }
        ) { ChocolateIcon(size = mockupDp(MockupDimens.NAV_ICON)) }
        NavItem(
            label = "SETTINGS",
            selected = selected == AppDestination.Settings,
            onClick = { onSelect(AppDestination.Settings) }
        ) { WrappedCandyIcon(size = mockupDp(MockupDimens.NAV_ICON)) }
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) colors.navActiveBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
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
            ChocolateIcon(size = mockupDp(MockupDimens.STREAK_ICON))
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
