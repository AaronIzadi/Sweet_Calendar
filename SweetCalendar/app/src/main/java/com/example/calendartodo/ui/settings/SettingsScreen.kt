package com.example.calendartodo.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.calendartodo.BuildConfig
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.export.TaskExportRunner
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CalMiniIcon
import com.example.calendartodo.ui.components.CheckCandyIcon
import com.example.calendartodo.ui.components.ClockMiniIcon
import com.example.calendartodo.ui.components.NavPeppermintIcon
import com.example.calendartodo.ui.components.ProfileLollipopIcon
import com.example.calendartodo.ui.components.SettingsBoxUncheckedIcon
import com.example.calendartodo.ui.components.SettingsChocolateIcon
import com.example.calendartodo.ui.components.SettingsGumdropIcon
import com.example.calendartodo.ui.components.SweetSwitch
import com.example.calendartodo.ui.components.TrashIcon
import com.example.calendartodo.ui.today.computeStreak
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val SettingsValueLight = Color(0xFF9A8878)
private val SettingsGroupLabelLight = Color(0xFFB39D89)
private val SettingsChevronLight = Color(0xFFC9B8A6)
private val ProfileAvatarBackgroundLight = Color(0xFFFFF8FA)
private val SettingsFieldLabelLight = Color(0xFF9A8878)
private val SettingsFieldPlaceholderLight = Color(0xFFB7A493)

@Composable
private fun settingsValueColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else SettingsValueLight
}

@Composable
private fun settingsGroupLabelColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.purpleDeep else SettingsGroupLabelLight
}

@Composable
private fun settingsChevronColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else SettingsChevronLight
}

@Composable
private fun settingsFieldLabelColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else SettingsFieldLabelLight
}

@Composable
private fun settingsFieldPlaceholderColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.navInactive else SettingsFieldPlaceholderLight
}

@Composable
private fun profileAvatarBackgroundColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) Color.Black.copy(alpha = 0.2f) else ProfileAvatarBackgroundLight
}

@Composable
fun SettingsScreen(
    tasks: List<TaskEntity>,
    userName: String,
    darkMode: Boolean,
    showHolidays: Boolean,
    weekStartsOn: Int,
    calendarSystem: CalendarSystem,
    exportRunner: TaskExportRunner,
    onDarkModeChange: (Boolean) -> Unit,
    onShowHolidaysChange: (Boolean) -> Unit,
    onWeekStartsOnChange: (Int) -> Unit,
    onCalendarSystemChange: (CalendarSystem) -> Unit,
    onUserNameChange: (String) -> Unit,
    onOpenStats: () -> Unit = {},
    onOpenArchive: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenRecoverSnackbar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val streak = computeStreak(tasks)
    val candiesEarned = tasks.count { it.isDone }
    var showNameDialog by remember { mutableStateOf(false) }
    var showWeekDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var taskRemindersEnabled by remember { mutableStateOf(true) }
    val rowIconSlot = mockupDp(MockupDimens.SETTINGS_ROW_ICON)
    val miniIcon = mockupDp(MockupDimens.MINI_FIELD_ICON)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = mockupDp(18))
    ) {
        Spacer(Modifier.height(mockupDp(6)))
        Text(
            "Settings",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.SETTINGS_TITLE),
                lineHeight = mockupSp(24f)
            ),
            color = colors.ink,
            modifier = Modifier.padding(top = mockupDp(16), bottom = mockupDp(18))
        )

        Box(modifier = Modifier.fillMaxWidth().padding(bottom = mockupDp(4))) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = mockupDp(3))
                    .clip(RoundedCornerShape(mockupDp(MockupDimens.SETTINGS_PROFILE_RADIUS)))
                    .background(colors.purpleDeep)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(MockupDimens.SETTINGS_PROFILE_RADIUS)))
                    .background(Brush.linearGradient(listOf(colors.pink, colors.purple)))
                    .clickable { showNameDialog = true }
                    .padding(mockupDp(16)),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Box(
                modifier = Modifier
                    .size(mockupDp(MockupDimens.SETTINGS_PROFILE_AVATAR))
                    .clip(RoundedCornerShape(mockupDp(MockupDimens.SETTINGS_PROFILE_RADIUS)))
                    .background(profileAvatarBackgroundColor()),
                contentAlignment = Alignment.Center
            ) {
                ProfileLollipopIcon(size = mockupDp(MockupDimens.SETTINGS_PROFILE_ICON))
            }
            Column(modifier = Modifier.padding(start = mockupDp(12))) {
                Text(
                    userName,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SETTINGS_PROFILE_NAME_F),
                        lineHeight = mockupSp(18f)
                    ),
                    color = Color.White
                )
                Text(
                    "$streak day streak · $candiesEarned candies earned",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(MockupDimens.SETTINGS_PROFILE_SUB_F),
                        lineHeight = mockupSp(14f)
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.clickable(onClick = onOpenStats)
                )
            }
            }
        }

        SettingsGroupLabel("CALENDAR")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    CalMiniIcon(size = miniIcon, color = colors.purpleDeep)
                }
            },
            label = "Calendar system",
            value = calendarSystem.label,
            onClick = { showCalendarDialog = true }
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    CalMiniIcon(size = miniIcon, color = colors.purpleDeep)
                }
            },
            label = "Week starts on",
            value = JalaliDate.WEEKDAY_NAMES_EN[weekStartsOn],
            onClick = { showWeekDialog = true }
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    NavPeppermintIcon(size = rowIconSlot)
                }
            },
            label = "Show local holidays",
            trailing = {
                SweetSwitch(checked = showHolidays, onCheckedChange = onShowHolidaysChange)
            }
        )

        SettingsGroupLabel("APPEARANCE")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    SettingsGumdropIcon(size = miniIcon)
                }
            },
            label = "Candy theme",
            value = "Bubblegum"
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    SettingsChocolateIcon(size = rowIconSlot)
                }
            },
            label = "Dark mode",
            trailing = {
                SweetSwitch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }
        )

        SettingsGroupLabel("NOTIFICATIONS")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    ClockMiniIcon(size = miniIcon, color = colors.purpleDeep)
                }
            },
            label = "Task reminders",
            trailing = {
                SweetSwitch(
                    checked = taskRemindersEnabled,
                    onCheckedChange = { taskRemindersEnabled = it }
                )
            }
        )

        SettingsGroupLabel("WIDGETS")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    SettingsGumdropIcon(size = miniIcon)
                }
            },
            label = "Today tasks widget",
            value = "Pin from launcher"
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    SettingsChocolateIcon(size = rowIconSlot)
                }
            },
            label = "Jar progress widget",
            value = "Pin from launcher"
        )

        SettingsGroupLabel("TASKS")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    CheckCandyIcon(
                        size = mockupDp(MockupDimens.DETAIL_BADGE_ICON),
                        bgColor = if (colors.isDark) colors.mint else MintGreen
                    )
                }
            },
            label = "Completed tasks",
            onClick = onOpenArchive
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    CalMiniIcon(size = miniIcon, color = colors.purpleDeep)
                }
            },
            label = "Search tasks",
            onClick = onOpenSearch
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    TrashIcon(
                        width = mockupDp(MockupDimens.TRASH_ICON_W),
                        height = mockupDp(MockupDimens.TRASH_ICON_H)
                    )
                }
            },
            label = "Recover snackbar",
            onClick = onOpenRecoverSnackbar
        )

        SettingsGroupLabel("DATA")
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    SettingsBoxUncheckedIcon(size = mockupDp(10))
                }
            },
            label = "Backup & export",
            onClick = { showBackupDialog = true }
        )
        SettingsRow(
            icon = {
                SettingsIconSlot(rowIconSlot) {
                    CheckCandyIcon(
                        size = mockupDp(MockupDimens.DETAIL_BADGE_ICON),
                        bgColor = if (colors.isDark) colors.mint else MintGreen
                    )
                }
            },
            label = "About Sweet Calendar",
            value = "v${BuildConfig.VERSION_NAME}",
            onClick = { showAboutDialog = true }
        )

        Spacer(Modifier.height(mockupDp(24)))
    }

    if (showNameDialog) {
        NameEditDialog(
            initialName = userName,
            onDismiss = { showNameDialog = false },
            onConfirm = { name ->
                onUserNameChange(name)
                showNameDialog = false
            }
        )
    }

    if (showWeekDialog) {
        WeekStartDialog(
            selected = weekStartsOn,
            onDismiss = { showWeekDialog = false },
            onSelect = { day ->
                onWeekStartsOnChange(day)
                showWeekDialog = false
            }
        )
    }

    if (showCalendarDialog) {
        CalendarSystemDialog(
            selected = calendarSystem,
            onDismiss = { showCalendarDialog = false },
            onSelect = { system ->
                onCalendarSystemChange(system)
                showCalendarDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showBackupDialog) {
        BackupExportDialog(
            userName = userName,
            calendarSystem = calendarSystem,
            exportRunner = exportRunner,
            onDismiss = { showBackupDialog = false }
        )
    }
}

@Composable
private fun CalendarSystemDialog(
    selected: CalendarSystem,
    onDismiss: () -> Unit,
    onSelect: (CalendarSystem) -> Unit
) {
    SettingsPickerDialog(
        title = "Calendar system",
        onDismiss = onDismiss
    ) {
        CalendarSystem.entries.forEach { system ->
            SettingsPickerOption(
                label = system.label,
                selected = system == selected,
                onClick = { onSelect(system) }
            )
        }
    }
}

@Composable
private fun SettingsIconSlot(
    size: androidx.compose.ui.unit.Dp,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun NameEditDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val colors = SweetTheme.colors
    var name by remember(initialName) { mutableStateOf(initialName) }
    val fieldShape = RoundedCornerShape(mockupDp(MockupDimens.FORM_FIELD_RADIUS))

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        top = mockupDp(16),
                        bottom = mockupDp(8)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "✕ CLOSE",
                    style = TextStyle(
                        fontFamily = PixelFont,
                        fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                        lineHeight = mockupSp(14f)
                    ),
                    color = colors.purpleDeep,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
                Text(
                    "SAVE ✓",
                    style = TextStyle(
                        fontFamily = PixelFont,
                        fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                        lineHeight = mockupSp(14f)
                    ),
                    color = colors.pinkDeep,
                    modifier = Modifier.clickable {
                        onConfirm(name.trim().ifBlank { "Friend" })
                    }
                )
            }

            Text(
                "Edit info",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                    lineHeight = mockupSp(22f)
                ),
                color = colors.ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = mockupDp(8)),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Column(
                modifier = Modifier.padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(8),
                    bottom = mockupDp(24)
                )
            ) {
                Text(
                    "NAME",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.FIELD_LABEL),
                        letterSpacing = mockupSp(0.3f),
                        lineHeight = mockupSp(14f)
                    ),
                    color = settingsFieldLabelColor(),
                    modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6))
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                            .clip(fieldShape)
                            .background(colors.line)
                    )
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
                            lineHeight = mockupSp(18f),
                            color = colors.ink
                        ),
                        cursorBrush = SolidColor(colors.pink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(fieldShape)
                            .background(colors.paper)
                            .padding(horizontal = mockupDp(14), vertical = mockupDp(12)),
                        decorationBox = { inner ->
                            if (name.isEmpty()) {
                                Text(
                                    "e.g. Aaron",
                                    style = TextStyle(
                                        fontFamily = BodyFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
                                        lineHeight = mockupSp(18f)
                                    ),
                                    color = settingsFieldPlaceholderColor()
                                )
                            }
                            inner()
                        }
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun WeekStartDialog(
    selected: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    SettingsPickerDialog(
        title = "Week starts on",
        onDismiss = onDismiss
    ) {
        JalaliDate.WEEKDAY_NAMES_EN.forEachIndexed { index, label ->
            SettingsPickerOption(
                label = label,
                selected = index == selected,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
private fun SettingsPickerDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = SweetTheme.colors

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            top = mockupDp(16),
                            bottom = mockupDp(8)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✕ CLOSE",
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                            lineHeight = mockupSp(14f)
                        ),
                        color = colors.purpleDeep,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                Text(
                    title,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(8)),
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier.padding(
                        start = mockupDp(18),
                        end = mockupDp(18),
                        bottom = mockupDp(24)
                    ),
                    content = content
                )
            }
        }
    }
}

@Composable
private fun SettingsPickerOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(mockupDp(10)))
            .background(if (selected) colors.purple.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = mockupDp(12), horizontal = mockupDp(10)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.FIELD_TEXT_F),
                lineHeight = mockupSp(18f)
            ),
            color = if (selected) colors.purpleDeep else colors.ink
        )
    }
}

@Composable
private fun SettingsGroupLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = PixelFont,
            fontSize = mockupSp(MockupDimens.SETTINGS_GROUP_LABEL),
            lineHeight = mockupSp(12f),
            letterSpacing = mockupSp(0.5f)
        ),
        color = settingsGroupLabelColor(),
        modifier = Modifier.padding(top = mockupDp(16), bottom = mockupDp(8))
    )
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.SETTINGS_ROW_RADIUS))

    Box(modifier = Modifier.padding(bottom = mockupDp(8))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.SETTINGS_ROW_SHADOW))
                .clip(shape)
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.paper)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(
                    horizontal = mockupDp(MockupDimens.SETTINGS_ROW_PAD_H),
                    vertical = mockupDp(MockupDimens.SETTINGS_ROW_PAD_V)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(
                label,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SETTINGS_ROW_LABEL_F),
                    lineHeight = mockupSp(17f)
                ),
                color = colors.ink,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = mockupDp(MockupDimens.SETTINGS_ROW_GAP))
            )
            if (value != null) {
                Text(
                    value,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(MockupDimens.SETTINGS_ROW_VALUE_F),
                        lineHeight = mockupSp(15f)
                    ),
                    color = settingsValueColor()
                )
            }
            trailing?.invoke()
            if (trailing == null && (value != null || onClick != null)) {
                Text(
                    "›",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SETTINGS_CHEVRON_F)
                    ),
                    color = settingsChevronColor(),
                    modifier = Modifier.padding(start = mockupDp(8))
                )
            }
        }
    }
}
