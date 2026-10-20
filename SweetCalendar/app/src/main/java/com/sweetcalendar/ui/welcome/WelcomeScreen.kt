package com.sweetcalendar.ui.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sweetcalendar.ui.components.RingPlanetIcon
import com.sweetcalendar.ui.components.StarIcon
import com.sweetcalendar.ui.components.SweetPixelButton
import com.sweetcalendar.ui.components.ThemeCompletedCheckIcon
import com.sweetcalendar.ui.components.ThemeHeroIcon
import com.sweetcalendar.ui.components.ThemeWelcomeDecoBottomStart
import com.sweetcalendar.ui.components.ThemeWelcomeDecoTopEnd
import com.sweetcalendar.ui.components.ThemeDecorBackground
import com.sweetcalendar.ui.components.ThemeWelcomeDecoTopStart
import com.sweetcalendar.ui.components.wobbleOnTap
import com.sweetcalendar.ui.components.NavPeppermintIcon
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.PixelFont
import com.sweetcalendar.ui.theme.SweetMotion
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.ambientWelcomeColor
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import com.sweetcalendar.ui.navigation.sweetScreenTransition
import com.sweetcalendar.ui.theme.rememberReducedMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class WelcomeSlide(
    val topTitle: String,
    val bottomTitle: String,
    val subtitle: String,
    val chips: List<String>,
)

private val welcomeSlides = listOf(
    WelcomeSlide(
        topTitle = "SWEET",
        bottomTitle = "CALENDAR",
        subtitle = "A softer way to keep your day in rhythm.",
        chips = listOf("Persian calendar", "Daily tasks", "Local holidays"),
    ),
    WelcomeSlide(
        topTitle = "PLAN",
        bottomTitle = "YOUR DAY",
        subtitle = "Turn your little wins into a jar full of momentum.",
        chips = listOf("Focus blocks", "Checklists", "Quick wins"),
    ),
    WelcomeSlide(
        topTitle = "KEEP",
        bottomTitle = "IT SWEET",
        subtitle = "Celebrate the dates, routines, and moments that matter.",
        chips = listOf("Holiday cues", "Streaks", "Joyful planning"),
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = SweetTheme.colors
    val pagerState = rememberPagerState(pageCount = { welcomeSlides.size })
    val currentPage = pagerState.currentPage
    val scope = rememberCoroutineScope()
    val reducedMotion = rememberReducedMotion()
    val tagColor = colors.muted
    val isLastPage = currentPage == welcomeSlides.lastIndex
    var entranceReady by remember { mutableStateOf(reducedMotion) }
    var footerVisible by remember { mutableStateOf(reducedMotion) }
    LaunchedEffect(Unit) {
        if (!reducedMotion) {
            entranceReady = true
        }
    }
    LaunchedEffect(entranceReady, currentPage) {
        if (reducedMotion) {
            footerVisible = true
            return@LaunchedEffect
        }
        if (currentPage == 0 && entranceReady) {
            footerVisible = false
            delay(720)
            footerVisible = true
        } else {
            footerVisible = true
        }
    }

    fun goToPage(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(
                page.coerceIn(0, welcomeSlides.lastIndex),
                animationSpec = if (reducedMotion) SweetMotion.shortTween() else SweetMotion.mediumTween()
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WelcomeAnimatedBackground(pageIndex = currentPage, reducedMotion = reducedMotion)

        if (currentPage > 0) {
            WelcomePixelBackButton(
                onClick = { goToPage(currentPage - 1) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = mockupDp(20), top = mockupDp(24))
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = mockupDp(20), top = mockupDp(24))
                    .alpha(0.85f)
                    .wobbleOnTap(),
            ) {
                ThemeWelcomeDecoTopStart(size = mockupDp(MockupDimens.DECO_ICON))
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = mockupDp(22), top = mockupDp(36))
                .alpha(0.85f)
                .wobbleOnTap(),
        ) {
            ThemeWelcomeDecoTopEnd(size = mockupDp(MockupDimens.DECO_ICON_SMALL))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = mockupDp(18), bottom = mockupDp(170))
                .alpha(0.85f)
                .wobbleOnTap(),
        ) {
            ThemeWelcomeDecoBottomStart(size = mockupDp(MockupDimens.DECO_ICON))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mockupDp(26), vertical = mockupDp(28)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = !reducedMotion
            ) { page ->
                val slide = welcomeSlides[page]
                val showEntrance = page == 0 && entranceReady && !reducedMotion
                AnimatedVisibility(
                    visible = reducedMotion || page != 0 || entranceReady,
                    enter = if (reducedMotion) {
                        fadeIn(SweetMotion.shortTween())
                    } else {
                        fadeIn(SweetMotion.mediumTween()) +
                            slideInVertically(SweetMotion.mediumTween()) { fullHeight -> fullHeight / 10 }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    WelcomeSlideContent(
                        slide = slide,
                        tagColor = tagColor,
                        heroEntrance = showEntrance,
                        reducedMotion = reducedMotion
                    )
                }
            }
            Spacer(Modifier.height(mockupDp(26)))
            AnimatedVisibility(
                visible = footerVisible,
                enter = if (reducedMotion) {
                    fadeIn(SweetMotion.shortTween())
                } else {
                    fadeIn(SweetMotion.mediumTween()) +
                        slideInVertically(SweetMotion.mediumTween()) { it / 5 }
                },
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SweetPixelButton(
                        text = if (isLastPage) "START PLANNING" else "NEXT",
                        onClick = {
                            if (isLastPage) onStart() else goToPage(currentPage + 1)
                        }
                    )
                    Spacer(Modifier.height(mockupDp(14)))
                    Text(
                        "Skip intro",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = SecondaryTextWeight,
                            fontSize = mockupSp(MockupDimens.SKIP_LINK),
                            lineHeight = mockupSp(16f)
                        ),
                        color = colors.purpleDeep,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = onSkip)
                    )
                }
            }
            Spacer(Modifier.height(mockupDp(20)))
            WelcomePageDots(
                currentPage = currentPage,
                totalPages = welcomeSlides.size,
                onPageSelected = { goToPage(it) }
            )
        }
    }
}

@Composable
private fun WelcomeAnimatedBackground(pageIndex: Int, reducedMotion: Boolean) {
    if (reducedMotion) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ambientWelcomeColor(pageIndex))
            )
            ThemeDecorBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.35f),
            )
        }
    } else {
        AnimatedContent(
            targetState = pageIndex,
            transitionSpec = {
                if (reducedMotion) {
                    fadeIn(SweetMotion.shortTween()) togetherWith fadeOut(SweetMotion.shortTween())
                } else {
                    sweetScreenTransition(fromDepth = 0, toDepth = 0, tabDirection = 1)
                }
            },
            modifier = Modifier.fillMaxSize(),
            label = "welcome-bg"
        ) { index ->
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ambientWelcomeColor(index))
                )
                ThemeDecorBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.42f),
                )
            }
        }
    }
}

@Composable
private fun WelcomeSlideContent(
    slide: WelcomeSlide,
    tagColor: Color,
    heroEntrance: Boolean,
    reducedMotion: Boolean
) {
    val colors = SweetTheme.colors
    var entranceStage by remember(heroEntrance) {
        mutableIntStateOf(if (!heroEntrance || reducedMotion) 3 else 0)
    }
    LaunchedEffect(heroEntrance, reducedMotion) {
        if (!heroEntrance || reducedMotion) {
            entranceStage = 3
            return@LaunchedEffect
        }
        entranceStage = 1
        delay(160)
        entranceStage = 2
        delay(220)
        entranceStage = 3
    }
    val slideEnter = fadeIn(SweetMotion.mediumTween()) +
        slideInVertically(SweetMotion.mediumTween()) { fullHeight -> fullHeight / 12 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = mockupDp(4)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = entranceStage >= 1,
            enter = if (reducedMotion) fadeIn(SweetMotion.shortTween()) else slideEnter,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = SecondaryTextWeight,
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
            }
        }
        Spacer(Modifier.height(mockupDp(18)))
        AnimatedVisibility(
            visible = entranceStage >= 2,
            enter = if (reducedMotion) fadeIn(SweetMotion.shortTween()) else slideEnter,
        ) {
            ThemeHeroIcon(
                modifier = Modifier.wobbleOnTap(),
                size = mockupDp(MockupDimens.HERO_LOLLIPOP_W)
            )
        }
        Spacer(Modifier.height(mockupDp(22)))
        AnimatedVisibility(
            visible = entranceStage >= 3,
            enter = if (reducedMotion) fadeIn(SweetMotion.shortTween()) else slideEnter,
        ) {
            WelcomeFeatureChips(slide.chips)
        }
    }
}

@Composable
private fun WelcomePixelBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val cornerRadius = mockupDp(12)
    val shadowOffset = mockupDp(4)
    val horizontalPadding = mockupDp(14)
    val verticalPadding = mockupDp(10)
    Box(modifier = modifier.wrapContentSize()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(colors.shadow)
        )
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(colors.paper)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "BACK",
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.PIXEL_BTN),
                    letterSpacing = mockupSp(0.5f),
                    lineHeight = mockupSp(12f),
                    textAlign = TextAlign.Center
                ),
                color = colors.purpleDeep,
                maxLines = 1,
                softWrap = false
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
            .fillMaxWidth()
            .heightIn(min = 72.dp)
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
                .background(colors.shadow)
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
    val dotHeight = mockupDp(MockupDimens.PAGE_DOT)
    val dotWidthActive = mockupDp(16)
    val dotWidthIdle = dotHeight
    Row(horizontalArrangement = Arrangement.spacedBy(mockupDp(6))) {
        repeat(totalPages) { index ->
            val selected = index == currentPage
            val dotColor by animateColorAsState(
                targetValue = if (selected) colors.pinkDeep else colors.line,
                animationSpec = SweetMotion.shortTween(),
                label = "welcome-dot-$index"
            )
            val dotWidth by animateDpAsState(
                targetValue = if (selected) dotWidthActive else dotWidthIdle,
                animationSpec = SweetMotion.shortTween(),
                label = "welcome-dot-width-$index"
            )
            Box(
                Modifier
                    .size(width = dotWidth, height = dotHeight)
                    .clip(RoundedCornerShape(mockupDp(2)))
                    .background(dotColor)
                    .clickable { onPageSelected(index) }
            )
        }
    }
}
