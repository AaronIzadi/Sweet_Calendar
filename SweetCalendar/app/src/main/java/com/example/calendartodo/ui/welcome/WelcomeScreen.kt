package com.example.calendartodo.ui.welcome

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.calendartodo.ui.components.ChocolateIcon
import com.example.calendartodo.ui.components.GummyIcon
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PeppermintCandyIcon
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.components.WrappedCandyIcon
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = SweetTheme.colors
    val welcomeGradient = if (colors.isDark) {
        listOf(Color(0xFF2E1E3B), Color(0xFF241C36), Color(0xFF16281F))
    } else {
        listOf(Color(0xFFFFE1EE), Color(0xFFF3E3FB), Color(0xFFE3F7EE))
    }
    val tagColor = if (colors.isDark) colors.muted else Color(0xFF6B5A4B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(welcomeGradient))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = mockupDp(20), top = mockupDp(24))
                .alpha(0.85f)
        ) {
            PeppermintCandyIcon(size = mockupDp(MockupDimens.DECO_ICON))
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = mockupDp(22), top = mockupDp(36))
                .alpha(0.85f)
        ) {
            GummyIcon(size = mockupDp(MockupDimens.DECO_ICON_SMALL), color = LemonYellow)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = mockupDp(18), bottom = mockupDp(170))
                .alpha(0.85f)
        ) {
            ChocolateIcon(size = mockupDp(MockupDimens.DECO_ICON))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mockupDp(26), vertical = mockupDp(28)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LollipopIcon(
                size = mockupDp(MockupDimens.HERO_LOLLIPOP_W),
                height = mockupDp(MockupDimens.HERO_LOLLIPOP_H)
            )
            Spacer(Modifier.height(mockupDp(18)))
            Text(
                "SWEET",
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.WELCOME_TITLE),
                    lineHeight = mockupSp(29f)
                ),
                color = colors.pinkDeep,
                textAlign = TextAlign.Center
            )
            Text(
                "CALENDAR",
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.WELCOME_TITLE),
                    lineHeight = mockupSp(29f)
                ),
                color = colors.purpleDeep,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(mockupDp(8)))
            Text(
                "A Persian calendar to-do list, decorated one candy at a time.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.WELCOME_TAG),
                    lineHeight = mockupSp(19.5f)
                ),
                color = tagColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = mockupDp(10))
            )
            Spacer(Modifier.height(mockupDp(22)))
            WelcomeFeatureChips()
            Spacer(Modifier.height(mockupDp(26)))
            SweetPixelButton(text = "START PLANNING", onClick = onStart)
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
            WelcomePageDots()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WelcomeFeatureChips() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(mockupDp(8), Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(mockupDp(8)),
        modifier = Modifier.padding(horizontal = mockupDp(4))
    ) {
        WelcomeFeatureChip("Persian calendar") {
            PeppermintCandyIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
        }
        WelcomeFeatureChip("Daily tasks") {
            WrappedCandyIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
        }
        WelcomeFeatureChip("Local holidays") {
            ChocolateIcon(size = mockupDp(MockupDimens.FEAT_CHIP_ICON))
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
private fun WelcomePageDots() {
    val colors = SweetTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(mockupDp(6))) {
        Box(
            Modifier
                .size(mockupDp(MockupDimens.PAGE_DOT))
                .clip(RoundedCornerShape(mockupDp(2)))
                .background(colors.pinkDeep)
        )
        repeat(2) {
            Box(
                Modifier
                    .size(mockupDp(MockupDimens.PAGE_DOT))
                    .clip(RoundedCornerShape(mockupDp(2)))
                    .background(colors.line)
            )
        }
    }
}
