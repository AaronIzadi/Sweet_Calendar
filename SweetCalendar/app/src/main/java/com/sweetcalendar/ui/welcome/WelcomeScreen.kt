package com.sweetcalendar.ui.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.zIndex
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
import com.sweetcalendar.ui.motion.floating
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
    var hasCompletedFirstSlideEntrance by remember { mutableStateOf(reducedMotion) }
    var hasPlayedFirstSlideFooterDelay by remember { mutableStateOf(reducedMotion) }
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
        if (currentPage == 0 && entranceReady && !hasPlayedFirstSlideFooterDelay) {
            footerVisible = false
            delay(720)
            hasPlayedFirstSlideFooterDelay = true
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

    BackHandler(enabled = currentPage > 0) {
        goToPage(currentPage - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.cream),
    ) {
        WelcomeAnimatedBackground(pageIndex = currentPage, reducedMotion = reducedMotion)

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
                val playStaggeredEntrance =
                    page == 0 && entranceReady && !reducedMotion && !hasCompletedFirstSlideEntrance
                val showPage = reducedMotion || page != 0 || entranceReady
                if (showPage) {
                    WelcomeSlideContent(
                        slide = slide,
                        tagColor = tagColor,
                        playStaggeredEntrance = playStaggeredEntrance,
                        reducedMotion = reducedMotion,
                        onStaggeredEntranceComplete = {
                            hasCompletedFirstSlideEntrance = true
                        },
                        modifier = Modifier.fillMaxSize(),
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
                            lineHeight = mockupSp(MockupDimens.SKIP_LINK_LINE),
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

        // Above the full-screen pager so BACK and corner decos receive taps.
        if (currentPage > 0) {
            WelcomePixelBackButton(
                onClick = { goToPage(currentPage - 1) },
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopStart)
                    .padding(start = mockupDp(20), top = mockupDp(24))
            )
        } else {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopStart)
                    .padding(start = mockupDp(20), top = mockupDp(24))
                    .alpha(0.85f)
                    .wobbleOnTap(),
            ) {
                ThemeWelcomeDecoTopStart(
                    size = mockupDp(MockupDimens.DECO_ICON),
                    modifier = Modifier.floating(phaseMillis = 0),
                )
            }
        }
        Box(
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopEnd)
                .padding(end = mockupDp(22), top = mockupDp(36))
                .alpha(0.85f)
                .wobbleOnTap(),
        ) {
            ThemeWelcomeDecoTopEnd(
                size = mockupDp(MockupDimens.DECO_ICON_SMALL),
                modifier = Modifier.floating(phaseMillis = 400),
            )
        }
        Box(
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.BottomStart)
                .padding(start = mockupDp(18), bottom = mockupDp(170))
                .alpha(0.85f)
                .wobbleOnTap(),
        ) {
            ThemeWelcomeDecoBottomStart(
                size = mockupDp(MockupDimens.DECO_ICON),
                modifier = Modifier.floating(phaseMillis = 800),
            )
        }
    }
}

@Composable
private fun WelcomeAnimatedBackground(pageIndex: Int, reducedMotion: Boolean) {
    val bgColor by animateColorAsState(
        targetValue = ambientWelcomeColor(pageIndex),
        animationSpec = if (reducedMotion) SweetMotion.shortTween() else SweetMotion.mediumTween(),
        label = "welcomeBg",
    )
    Box(Modifier.fillMaxSize().background(bgColor)) {
        ThemeDecorBackground(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (reducedMotion) 0.35f else 0.42f),
        )
    }
}

@Composable
private fun WelcomeSlideContent(
    slide: WelcomeSlide,
    tagColor: Color,
    playStaggeredEntrance: Boolean,
    reducedMotion: Boolean,
    onStaggeredEntranceComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SweetTheme.colors
    var entranceStage by remember {
        mutableIntStateOf(if (reducedMotion || !playStaggeredEntrance) 3 else 0)
    }
    LaunchedEffect(playStaggeredEntrance, reducedMotion) {
        if (!playStaggeredEntrance || reducedMotion) {
            entranceStage = 3
            return@LaunchedEffect
        }
        entranceStage = 1
        delay(160)
        entranceStage = 2
        delay(220)
        entranceStage = 3
        onStaggeredEntranceComplete()
    }
    val slideEnter = fadeIn(SweetMotion.mediumTween()) +
        slideInVertically(SweetMotion.mediumTween()) { fullHeight -> fullHeight / 12 }

    Column(
        modifier = modifier
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
                        lineHeight = mockupSp(MockupDimens.WELCOME_TITLE_LINE),
                    ),
                    color = colors.pinkDeep,
                    textAlign = TextAlign.Center,
                    minLines = 1,
                    maxLines = 1,
                    modifier = Modifier.widthIn(max = 220.dp)
                )
                Spacer(Modifier.height(mockupDp(MockupDimens.WELCOME_TITLE_GAP)))
                Text(
                    slide.bottomTitle,
                    style = TextStyle(
                        fontFamily = PixelFont,
                        fontSize = mockupSp(MockupDimens.WELCOME_TITLE),
                        lineHeight = mockupSp(MockupDimens.WELCOME_TITLE_LINE),
                    ),
                    color = colors.purpleDeep,
                    textAlign = TextAlign.Center,
                    minLines = 1,
                    maxLines = 1,
                    modifier = Modifier.widthIn(max = 220.dp)
                )
                Spacer(Modifier.height(mockupDp(14)))
                Text(
                    slide.subtitle,
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = SecondaryTextWeight,
                        fontSize = mockupSp(MockupDimens.WELCOME_TAG),
                        lineHeight = mockupSp(MockupDimens.WELCOME_TAG_LINE),
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
                    fontSize = mockupSp(MockupDimens.FEAT_CHIP_TEXT_F),
                    lineHeight = mockupSp(18f),
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
                targetValue = if (selected) colors.pinkText else colors.line,
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
