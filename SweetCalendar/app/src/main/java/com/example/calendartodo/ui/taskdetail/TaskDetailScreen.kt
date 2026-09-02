package com.example.calendartodo.ui.taskdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import com.example.calendartodo.data.local.TaskEntity
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CalMiniIcon
import com.example.calendartodo.ui.components.ClockMiniIcon
import com.example.calendartodo.ui.components.EditPencilIcon
import com.example.calendartodo.ui.components.SparkleIcon
import com.example.calendartodo.ui.components.TaskCategory
import com.example.calendartodo.ui.components.TaskGemIcon
import com.example.calendartodo.ui.components.TaskHeartIcon
import com.example.calendartodo.ui.components.TaskLeafIcon
import com.example.calendartodo.ui.components.TaskPriority
import com.example.calendartodo.ui.components.SweetIconButton
import com.example.calendartodo.ui.components.formatTime12h
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val MetaTextColor = Color(0xFF6B5A4B)
private val FieldLabelColor = Color(0xFF9A8878)
private val DeleteLinkColor = Color(0xFFD9455E)
private val CompleteBtnShadow = Color(0xFF3C9679)

@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val date = remember(task.jalaliDate) { JalaliDate.parseIso(task.jalaliDate) }
    val category = TaskCategory.fromString(task.category)
    val priority = TaskPriority.fromString(task.priority)
    val badgeColor = when (category) {
        TaskCategory.Personal -> colors.pinkDeep
        TaskCategory.Home -> colors.mintDeep
        TaskCategory.Work -> colors.purpleDeep
    }
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    val month = JalaliDate.MONTH_NAMES_EN[date.month - 1]
    val badgeIconSize = mockupDp(MockupDimens.DETAIL_BADGE_ICON)
    val miniIconSize = mockupDp(MockupDimens.MINI_FIELD_ICON)

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
                    bottom = mockupDp(4)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SweetIconButton(label = "←", onClick = onBack)
            SweetIconButton(onClick = onEdit) {
                EditPencilIcon(
                    size = mockupDp(MockupDimens.DETAIL_ICON_BTN_FONT.toInt()),
                    color = colors.purpleDeep
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(10),
                    bottom = mockupDp(24)
                )
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(mockupDp(MockupDimens.DETAIL_BADGE_RADIUS)))
                    .background(badgeColor)
                    .padding(horizontal = mockupDp(10), vertical = mockupDp(6)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mockupDp(6))
            ) {
                when (category) {
                    TaskCategory.Personal -> TaskHeartIcon(size = badgeIconSize)
                    TaskCategory.Home -> TaskLeafIcon(size = badgeIconSize)
                    TaskCategory.Work -> TaskGemIcon(size = badgeIconSize)
                }
                Text(
                    category.label.uppercase(),
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.DETAIL_BADGE_TEXT_F),
                        lineHeight = mockupSp(13f)
                    ),
                    color = Color.White
                )
            }

            Spacer(Modifier.height(mockupDp(12)))
            Text(
                task.title,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.DETAIL_TITLE),
                    lineHeight = mockupSp(26f)
                ),
                color = colors.ink,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )

            Spacer(Modifier.height(mockupDp(14)))
            Column(
                verticalArrangement = Arrangement.spacedBy(mockupDp(8)),
                modifier = Modifier.padding(bottom = mockupDp(18))
            ) {
                MetaRow(
                    icon = {
                        CalMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                    },
                    text = "$weekday, $month ${date.day}, ${date.year}"
                )
                task.reminderTime?.let { time ->
                    MetaRow(
                        icon = {
                            ClockMiniIcon(size = miniIconSize, color = colors.purpleDeep)
                        },
                        text = formatTime12h(time)
                    )
                }
                MetaRow(
                    icon = { SparkleIcon(size = mockupDp(10)) },
                    text = "${priority.label} priority"
                )
                if (task.repeatWeekly) {
                    MetaRow(
                        icon = {
                            Text(
                                "↻",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = mockupSp(MockupDimens.DETAIL_META_F)
                                ),
                                color = colors.purpleDeep
                            )
                        },
                        text = "Repeats weekly"
                    )
                }
            }

            if (task.notes.isNotBlank()) {
                DetailFieldLabel("NOTES")
                NotesBox(text = task.notes)
            }

            Spacer(Modifier.height(mockupDp(18)))
            if (!task.isDone) {
                CompleteButton(text = "MARK COMPLETE", onClick = onComplete)
            } else {
                DetailSecondaryButton(text = "MARK INCOMPLETE", onClick = onComplete)
            }

            Text(
                "Delete task",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.DETAIL_DELETE),
                    lineHeight = mockupSp(15f),
                    textDecoration = TextDecoration.Underline
                ),
                color = DeleteLinkColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = mockupDp(18))
                    .clickable(onClick = onDelete)
            )
        }
    }
}

@Composable
private fun DetailFieldLabel(text: String) {
    Text(
        text,
        style = TextStyle(
            fontFamily = BodyFont,
            fontWeight = FontWeight.Bold,
            fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            letterSpacing = mockupSp(0.3f),
            lineHeight = mockupSp(14f)
        ),
        color = FieldLabelColor,
        modifier = Modifier.padding(top = mockupDp(14), bottom = mockupDp(6))
    )
}

@Composable
private fun NotesBox(text: String) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.line)
        )
        Text(
            text,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.DETAIL_NOTES_F),
                lineHeight = mockupSp(20f)
            ),
            color = MetaTextColor,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12))
        )
    }
}

@Composable
private fun CompleteButton(text: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.COMPLETE_BTN_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.COMPLETE_BTN_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(CompleteBtnShadow)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.mintDeep)
                .clickable(onClick = onClick)
                .padding(vertical = mockupDp(MockupDimens.COMPLETE_BTN_PAD_V)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f)
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun DetailSecondaryButton(text: String, onClick: () -> Unit) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.COMPLETE_BTN_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.line)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .clickable(onClick = onClick)
                .padding(vertical = mockupDp(MockupDimens.COMPLETE_BTN_PAD_V)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.COMPLETE_BTN_F),
                    lineHeight = mockupSp(15f)
                ),
                color = colors.purpleDeep
            )
        }
    }
}

@Composable
private fun MetaRow(icon: @Composable () -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
    ) {
        icon()
        Text(
            text,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.DETAIL_META_F),
                lineHeight = mockupSp(17f)
            ),
            color = MetaTextColor
        )
    }
}
