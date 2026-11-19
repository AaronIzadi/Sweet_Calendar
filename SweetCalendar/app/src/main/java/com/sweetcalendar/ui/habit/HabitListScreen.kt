package com.sweetcalendar.ui.habit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.sweetcalendar.data.HabitWithDays
import com.sweetcalendar.ui.components.BackArrowMiniIcon
import com.sweetcalendar.ui.components.SweetIconButton
import com.sweetcalendar.ui.motion.appearOnEnter
import com.sweetcalendar.ui.motion.rememberNewItemTracker
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

@Composable
fun HabitListScreen(
    habits: List<HabitWithDays>,
    onBack: () -> Unit,
    onHabitClick: (HabitWithDays) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val colors = SweetTheme.colors
    val isNewHabit = rememberNewItemTracker(habits.map { it.id })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(16),
                    bottom = mockupDp(8),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SweetIconButton(onClick = onBack) {
                BackArrowMiniIcon(size = mockupDp(14), color = colors.purpleDeep)
            }
            Text(
                "Habits",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(20f),
                    lineHeight = mockupSp(24f),
                ),
                color = colors.ink,
                modifier = Modifier.padding(start = mockupDp(MockupDimens.DAY_DETAIL_HEADER_GAP)),
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = mockupDp(18))
        ) {
            if (habits.isEmpty()) {
                item {
                    Text(
                        "No habits yet",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(14f),
                        ),
                        color = colors.muted,
                        modifier = Modifier.padding(vertical = mockupDp(24)),
                    )
                }
            } else {
                items(habits, key = { it.id }) { habit ->
                    SweetHabitCard(
                        habit = habit,
                        onClick = { onHabitClick(habit) },
                        modifier = Modifier.appearOnEnter(isNewHabit(habit.id)),
                    )
                }
            }
            item { Spacer(Modifier.height(mockupDp(90))) }
        }
    }
}
