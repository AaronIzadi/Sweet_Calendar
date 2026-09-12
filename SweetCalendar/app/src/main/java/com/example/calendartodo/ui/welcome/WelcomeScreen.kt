package com.example.calendartodo.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.components.RingPlanetIcon
import com.example.calendartodo.ui.components.StarIcon
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.components.ThemeCompletedCheckIcon
import com.example.calendartodo.ui.components.ThemeHeroIcon
import com.example.calendartodo.ui.components.ThemeWelcomeDecoBottomStart
import com.example.calendartodo.ui.components.ThemeWelcomeDecoTopEnd
import com.example.calendartodo.ui.components.ThemeWelcomeDecoTopStart
import com.example.calendartodo.ui.components.NavPeppermintIcon
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.themeWelcomeTagline
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val WelcomeTagLight = Color(0xFF6B5A4B)
private val CandyWelcomeGradientLight = listOf(
    Color(0xFFFFE1EE),
    Color(0xFFF3E3FB),
    Color(0xFFE3F7EE)
)
private val CandyWelcomeGradientDark = listOf(
    Color(0xFF2E1E3B),
    Color(0xFF241C36),
    Color(0xFF16281F)
)
private val SpaceWelcomeGradientLight = listOf(
    Color(0xFFF3E8FF),
    Color(0xFFEAF0FF),
    Color(0xFFE8FBF8)
)
private val SpaceWelcomeGradientDark = listOf(
    Color(0xFF1A1440),
    Color(0xFF141833),
    Color(0xFF0B0E24)
)

private data class WelcomeSlide(
    val topTitle: String,
    val bottomTitle: String,
    val subtitle: String,
    val chips: List<String>,
    val gradient: List<Color>
)

private val welcomeSlides = listOf(
    WelcomeSlide(
        topTitle = "SWEET",
        bottomTitle = "CALENDAR",
        subtitle = "A softer way to keep your day in rhythm.",
        chips = listOf("Persian calendar", "Daily tasks", "Local holidays"),
        gradient = CandyWelcomeGradientLight
    ),
    WelcomeSlide(
        topTitle = "PLAN",
        bottomTitle = "YOUR DAY",
        subtitle = "Turn your little wins into a jar full of momentum.",
        chips = listOf("Focus blocks", "Checklists", "Quick wins"),
        gradient = listOf(
            Color(0xFFFFF0D7),
            Color(0xFFEAF7FF),
            Color(0xFFE9F9E9)
        )
    ),
    WelcomeSlide(
        topTitle = "KEEP",
        bottomTitle = "IT SWEET",
        subtitle = "Celebrate the dates, routines, and moments that matter.",
        chips = listOf("Holiday cues", "Streaks", "Joyful planning"),
        gradient = listOf(
            Color(0xFFE9E9FF),
            Color(0xFFFEEAF8),
            Color(0xFFEAFBF7)
        )
    )
)

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = SweetTheme.colors
    var currentPage by remember { mutableStateOf(0) }
    val dragOffset = remember { mutableStateOf(0f) }
    val slide = welcomeSlides[currentPage]
    val welcomeGradient = when {
        SweetTheme.isSpace && colors.isDark -> SpaceWelcomeGradientDark
        SweetTheme.isSpace -> SpaceWelcomeGradientLight
        colors.isDark -> CandyWelcomeGradientDark
        else -> slide.gradient
    }
    val tagColor = if (colors.isDark || SweetTheme.isSpace) colors.muted else WelcomeTagLight
    val isLastPage = currentPage == welcomeSlides.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(welcomeGradient))
            .pointerInput(currentPage, welcomeSlides.size) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        dragOffset.value += dragAmount.x
                    },
                    onDragEnd = {
                        if (dragOffset.value > 60f && currentPage > 0) {
                            currentPage--
                        } else if (dragOffset.value < -60f && currentPage < welcomeSlides.lastIndex) {
                            currentPage++
                        }
                        dragOffset.value = 0f
                    },
                    onDragCancel = {
                        dragOffset.value = 0f
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = mockupDp(20), top = mockupDp(24))
                .alpha(0.85f)
        ) {
            ThemeWelcomeDecoTopStart(size = mockupDp(MockupDimens.DECO_ICON))
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = mockupDp(22), top = mockupDp(36))
                .alpha(0.85f)
        ) {
            ThemeWelcomeDecoTopEnd(size = mockupDp(MockupDimens.DECO_ICON_SMALL))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = mockupDp(18), bottom = mockupDp(170))
                .alpha(0.85f)
        ) {
            ThemeWelcomeDecoBottomStart(size = mockupDp(MockupDimens.DECO_ICON))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mockupDp(26), vertical = mockupDp(28)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ThemeHeroIcon(size = mockupDp(MockupDimens.HERO_LOLLIPOP_W))
            Spacer(Modifier.height(mockupDp(18)))
            Text(
                slide.topTitle,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.WELCOME_TITLE),
                    lineHeight = mockupSp(29f)
                ),
                color = colors.pinkDeep,
                textAlign = TextAlign.Center,
                minLines = 1,
                maxLines = 1,
                modifier = Modifier.widthIn(max = 220.dp)
            )
            Text(
                slide.bottomTitle,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.WELCOME_TITLE),
                    lineHeight = mockupSp(29f)
                ),
                color = colors.purpleDeep,
                textAlign = TextAlign.Center,
                minLines = 1,
                maxLines = 1,
                modifier = Modifier.widthIn(max = 220.dp)
            )
            Spacer(Modifier.height(mockupDp(8)))
            Text(
                slide.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.WELCOME_TAG),
                    lineHeight = mockupSp(19.5f)
                ),
                color = tagColor,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .padding(horizontal = mockupDp(10))
            )
            Spacer(Modifier.height(mockupDp(22)))
            WelcomeFeatureChips(slide.chips)
            Spacer(Modifier.height(mockupDp(26)))
            SweetPixelButton(
                text = if (isLastPage) "START PLANNING" else "NEXT",
                onClick = {
                    if (isLastPage) onStart() else currentPage++
                }
            )
            Spacer(Modifier.height(mockupDp(14)))
            Text(
                "Skip intro",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SKIP_LINK)
                ),
                color = colors.purpleDeep,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onSkip)
            )
            Spacer(Modifier.height(mockupDp(20)))
            WelcomePageDots(
                currentPage = currentPage,
                totalPages = welcomeSlides.size,
                onPageSelected = { currentPage = it }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WelcomeFeatureChips(chips: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(mockupDp(8), Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(mockupDp(8)),
        modifier = Modifier
            .heightIn(min = 48.dp)
            .padding(horizontal = mockupDp(4))
    ) {
        chips.forEach { label ->
            WelcomeFeatureChip(label) {
                when (label) {
                    "Persian calendar", "Focus blocks", "Holiday cues" -> {
                        if (SweetTheme.isSpace) {
                            RingPlanetIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                        } else {
                            NavPeppermintIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                        }
                    }
                    "Daily tasks", "Checklists", "Streaks" -> {
                        ThemeCompletedCheckIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                    }
                    "Local holidays", "Quick wins", "Joyful planning" -> {
                        if (SweetTheme.isSpace) {
                            StarIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                        } else {
                            ThemeWelcomeDecoBottomStart(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                        }
                    }
                    else -> {
                        ThemeCompletedCheckIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeatureChip(
    label: String,
    icon: @Composable () -> Unit
) {
    val colors = SweetTheme.colors
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(2))
                .clip(RoundedCornerShape(mockupDp(10)))
                .background(colors.line)
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(mockupDp(10)))
                .background(colors.paper)
                .padding(horizontal = mockupDp(10), vertical = mockupDp(7)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mockupDp(6))
        ) {
            icon()
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.FEAT_CHIP_TEXT_F)
                ),
                color = colors.ink
            )
        }
    }
}

@Composable
private fun WelcomePageDots(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit
) {
    val colors = SweetTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(mockupDp(6))) {
        repeat(totalPages) { index ->
            Box(
                Modifier
                    .size(mockupDp(MockupDimens.PAGE_DOT))
                    .clip(RoundedCornerShape(mockupDp(2)))
                    .background(if (index == currentPage) colors.pinkDeep else colors.line)
                    .clickable { onPageSelected(index) }
            )
        }
    }
}
