package com.example.calendartodo.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.ui.components.SearchGroupLabel
import com.example.calendartodo.ui.components.SearchIcon
import com.example.calendartodo.ui.components.SweetTaskCard
import com.example.calendartodo.ui.components.TaskCardStyle
import com.example.calendartodo.ui.components.TaskMetaStyle
import com.example.calendartodo.ui.stats.searchTasks
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val SearchPlaceholderLight = Color(0xFFB7A493)
private val SearchPlaceholderDark = Color(0xFFC9B8DE)
private val RecentChipBackgroundLight = Color(0xFFF1E9FB)
private val RecentChipBackgroundDark = Color(0xFF3A2C4C)

@Composable
private fun searchPlaceholderColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) SearchPlaceholderDark else SearchPlaceholderLight
}

@OptIn(ExperimentalLayoutApi::class)
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
    val chipBackground = if (colors.isDark) RecentChipBackgroundDark else RecentChipBackgroundLight
    val placeholderColor = searchPlaceholderColor()

    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(16),
                    bottom = mockupDp(6)
                )
        ) {
            val barShape = RoundedCornerShape(mockupDp(MockupDimens.SEARCH_BAR_RADIUS))
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = mockupDp(MockupDimens.SEARCH_BAR_SHADOW))
                        .clip(barShape)
                        .background(colors.line)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(barShape)
                        .background(colors.paper)
                        .padding(
                            horizontal = mockupDp(MockupDimens.SEARCH_BAR_PAD_H),
                            vertical = mockupDp(MockupDimens.SEARCH_BAR_PAD_V)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(mockupDp(8))
                ) {
                    SearchIcon(
                        size = mockupDp(MockupDimens.SEARCH_ICON),
                        color = placeholderColor
                    )
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = mockupSp(MockupDimens.SEARCH_TEXT),
                            color = colors.ink
                        ),
                        cursorBrush = SolidColor(colors.pink),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (query.isEmpty()) {
                                Text(
                                    "Search tasks…",
                                    style = TextStyle(
                                        fontFamily = BodyFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = mockupSp(MockupDimens.SEARCH_TEXT)
                                    ),
                                    color = placeholderColor
                                )
                            }
                            inner()
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mockupDp(18))
        ) {
            if (query.isNotBlank()) {
                item {
                    SearchGroupLabel("RESULTS · ${results.size}")
                }
                if (results.isEmpty()) {
                    item {
                        Text(
                            "No matching tasks",
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = mockupSp(12f),
                                lineHeight = mockupSp(16f)
                            ),
                            color = colors.muted,
                            modifier = Modifier.padding(bottom = mockupDp(8))
                        )
                    }
                } else {
                    items(results, key = { it.id }) { task ->
                        SweetTaskCard(
                            task = task,
                            onClick = {
                                onSearchSubmitted(query)
                                onTaskClick(task)
                            },
                            style = TaskCardStyle.Mockup,
                            highlightQuery = query,
                            metaStyle = TaskMetaStyle.SearchResult
                        )
                    }
                }
            }

            if (recentSearches.isNotEmpty()) {
                item {
                    SearchGroupLabel("RECENT SEARCHES")
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
                        verticalArrangement = Arrangement.spacedBy(mockupDp(8)),
                        modifier = Modifier.padding(bottom = mockupDp(6))
                    ) {
                        recentSearches.forEach { term ->
                            Text(
                                term,
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(MockupDimens.SEARCH_CHIP_TEXT),
                                    lineHeight = mockupSp(14f)
                                ),
                                color = colors.purpleDeep,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(mockupDp(MockupDimens.SEARCH_CHIP_RADIUS)))
                                    .background(chipBackground)
                                    .clickable { query = term }
                                    .padding(horizontal = mockupDp(11), vertical = mockupDp(7))
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(mockupDp(24))) }
        }
    }
}
