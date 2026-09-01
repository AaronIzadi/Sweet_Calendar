package com.example.calendartodo.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.today.computeStreak
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun SettingsScreen(
    tasks: List<TaskEntity>,
    userName: String,
    darkMode: Boolean,
    showHolidays: Boolean,
    weekStartsOn: Int,
    onDarkModeChange: (Boolean) -> Unit,
    onShowHolidaysChange: (Boolean) -> Unit,
    onWeekStartsOnChange: (Int) -> Unit,
    onUserNameChange: (String) -> Unit,
    onOpenStats: () -> Unit = {},
    onOpenArchive: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val streak = computeStreak(tasks)
    val candiesEarned = tasks.count { it.isDone }
    var showNameDialog by remember { mutableStateOf(false) }
    var showWeekDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = colors.ink)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(colors.pink, colors.purple)))
                .clickable { showNameDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                LollipopIcon(size = 28.dp)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(userName, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Text(
                    "$streak day streak · $candiesEarned candies earned",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.85f
                    ),
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    "Tap to edit name",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f
                    ),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        SettingsGroupLabel("CALENDAR")
        SettingsRow(icon = { PeppermintCandyIcon(size = 18.dp) }, label = "Calendar system", value = "Persian")
        SettingsRow(
            icon = { PeppermintCandyIcon(size = 18.dp) },
            label = "Week starts on",
            value = JalaliDate.WEEKDAY_NAMES_EN[weekStartsOn],
            onClick = { showWeekDialog = true }
        )
        SettingsRow(icon = { WrappedCandyIcon(size = 18.dp) }, label = "Show local holidays", trailing = {
            Switch(
                checked = showHolidays,
                onCheckedChange = onShowHolidaysChange,
                colors = SwitchDefaults.colors(checkedTrackColor = colors.mintDeep)
            )
        })

        SettingsGroupLabel("APPEARANCE")
        SettingsRow(icon = { WrappedCandyIcon(size = 18.dp) }, label = "Candy theme", value = "Bubblegum")
        SettingsRow(icon = { ChocolateIcon(size = 18.dp) }, label = "Dark mode", trailing = {
            Switch(
                checked = darkMode,
                onCheckedChange = onDarkModeChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.mintDeep,
                    uncheckedTrackColor = colors.line
                )
            )
        })

        SettingsGroupLabel("WIDGETS")
        SettingsRow(
            icon = { LollipopIcon(size = 18.dp) },
            label = "Today tasks widget",
            value = "Pin from launcher"
        )
        SettingsRow(
            icon = { ChocolateIcon(size = 18.dp) },
            label = "Jar progress widget",
            value = "Pin from launcher"
        )

        SettingsGroupLabel("TASKS")
        SettingsRow(icon = { ChocolateIcon(size = 18.dp) }, label = "Completed tasks", onClick = onOpenArchive)
        SettingsRow(icon = { PeppermintCandyIcon(size = 18.dp) }, label = "Search tasks", onClick = onOpenSearch)

        SettingsGroupLabel("DATA")
        SettingsRow(icon = { ChocolateIcon(size = 18.dp) }, label = "About Sweet Calendar", value = "v1.0")
        Spacer(Modifier.height(32.dp))
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
}

@Composable
private fun NameEditDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val colors = SweetTheme.colors
    var name by remember(initialName) { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your name", color = colors.ink) },
        text = {
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.ink),
                cursorBrush = SolidColor(colors.pink),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.paper)
                    .padding(12.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim().ifBlank { "Friend" }) }) {
                Text("Save", color = colors.pinkDeep)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.muted)
            }
        }
    )
}

@Composable
private fun WeekStartDialog(
    selected: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val colors = SweetTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Week starts on", color = colors.ink) },
        text = {
            Column {
                JalaliDate.WEEKDAY_NAMES_EN.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (index == selected) colors.purple.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { onSelect(index) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (index == selected) colors.purpleDeep else colors.ink
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = colors.pinkDeep)
            }
        }
    )
}

@Composable
private fun SettingsGroupLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = SweetTheme.colors.muted,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.paper)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.ink,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = colors.muted)
        }
        trailing?.invoke()
        if (value != null && trailing == null) {
            Text("›", color = colors.muted, modifier = Modifier.padding(start = 8.dp))
        } else if (onClick != null && trailing == null && value == null) {
            Text("›", color = colors.muted, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
