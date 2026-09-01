package com.example.calendartodo.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.SweetSectionLabel
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.formatDisplayShort
import com.example.calendartodo.ui.components.formatTime12h
import com.example.calendartodo.ui.stats.searchTasks
import com.example.calendartodo.ui.theme.SweetTheme

@Composable
fun SearchScreen(
    tasks: List<TaskEntity>,
    recentSearches: List<String>,
    onBack: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    var query by remember { mutableStateOf("") }
    val results = remember(tasks, query) { searchTasks(tasks, query) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
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
                    .clickable(onClick = onBack)
                    .padding(end = 12.dp, top = 4.dp, bottom = 4.dp)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.paper)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⌕", color = colors.muted, modifier = Modifier.padding(end = 8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.ink),
                    cursorBrush = SolidColor(colors.pink),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("Search tasks…", style = MaterialTheme.typography.bodyMedium, color = colors.muted)
                        }
                        inner()
                    }
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(horizontal = 18.dp)) {
            if (query.isNotBlank()) {
                item {
                    SweetSectionLabel("RESULTS · ${results.size}")
                }
                if (results.isEmpty()) {
                    item {
                        Text(
                            "No matching tasks",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.muted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                } else {
                    items(results, key = { it.id }) { task ->
                        HighlightedTaskRow(
                            task = task,
                            query = query,
                            onClick = {
                                onSearchSubmitted(query)
                                onTaskClick(task)
                            }
                        )
                    }
                }
            } else if (recentSearches.isNotEmpty()) {
                item { SweetSectionLabel("RECENT SEARCHES") }
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        recentSearches.forEach { term ->
                            Text(
                                term,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.purpleDeep,
                                modifier = Modifier
                                    .padding(end = 8.dp, bottom = 8.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (colors.isDark) colors.paper else androidx.compose.ui.graphics.Color(0xFFF1E9FB))
                                    .clickable { query = term }
                                    .padding(horizontal = 11.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun HighlightedTaskRow(
    task: TaskEntity,
    query: String,
    onClick: () -> Unit
) {
    val colors = SweetTheme.colors
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    val isToday = date == JalaliDate.today()
    val meta = buildList {
        add(if (isToday) "Today" else date.formatDisplayShort())
        task.reminderTime?.let { add(formatTime12h(it)) }
        if (task.category.isNotBlank()) add(task.category)
    }.joinToString(" · ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.paper)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            buildHighlightedText(task.title, query, colors),
            style = MaterialTheme.typography.titleSmall,
            color = colors.ink
        )
        Text(meta, style = MaterialTheme.typography.bodySmall, color = colors.muted, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun buildHighlightedText(text: String, query: String, colors: com.example.calendartodo.ui.theme.SweetColors) =
    buildAnnotatedString {
        val lower = text.lowercase()
        val q = query.trim().lowercase()
        var start = 0
        var idx = lower.indexOf(q)
        while (idx >= 0 && q.isNotEmpty()) {
            append(text.substring(start, idx))
            withStyle(SpanStyle(background = colors.lemon, color = colors.ink)) {
                append(text.substring(idx, idx + q.length))
            }
            start = idx + q.length
            idx = lower.indexOf(q, start)
        }
        append(text.substring(start))
    }
