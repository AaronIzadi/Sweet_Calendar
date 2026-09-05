package com.example.calendartodo.ui.offline

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.calendartodo.ui.components.MeltedCandyIcon
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.components.WarnIcon
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val OfflineBannerBackgroundLight = Color(0xFFFFF1DC)
private val OfflineBannerBackgroundDark = Color(0xFF3A2A22)
private val OfflineSubLight = Color(0xFF8A7867)
private val OfflineWarnColorDark = Color(0xFFEFD3AE)

@Composable
private fun offlineSubColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else OfflineSubLight
}

@Composable
private fun offlineBannerTextColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.lemon else colors.chocDeep
}

@Composable
private fun offlineWarnColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) OfflineWarnColorDark else colors.chocDeep
}

@Composable
fun OfflineScreen(
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val bannerBackground = if (colors.isDark) OfflineBannerBackgroundDark else OfflineBannerBackgroundLight

    BackHandler(onBack = onBack)

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
                    top = mockupDp(14)
                )
                .clip(RoundedCornerShape(mockupDp(MockupDimens.OFFLINE_BANNER_RADIUS)))
                .background(bannerBackground)
                .padding(horizontal = mockupDp(12), vertical = mockupDp(10)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mockupDp(10))
        ) {
            WarnIcon(
                size = mockupDp(MockupDimens.OFFLINE_WARN_ICON),
                color = offlineWarnColor()
            )
            Text(
                "Couldn't load holidays",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.OFFLINE_BANNER_TEXT)
                ),
                color = offlineBannerTextColor(),
                modifier = Modifier.weight(1f)
            )
            Text(
                "Retry",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.OFFLINE_BANNER_TEXT)
                ),
                color = colors.purpleDeep,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onRetry)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    start = mockupDp(MockupDimens.OFFLINE_PAD_H),
                    end = mockupDp(MockupDimens.OFFLINE_PAD_H),
                    bottom = mockupDp(MockupDimens.OFFLINE_PAD_BOTTOM)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MeltedCandyIcon(
                width = mockupDp(MockupDimens.OFFLINE_ICON_W),
                height = mockupDp(MockupDimens.OFFLINE_ICON_H)
            )
            Spacer(Modifier.height(mockupDp(18)))
            Text(
                "Holidays are offline for now",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.OFFLINE_TITLE),
                    lineHeight = mockupSp(20f)
                ),
                color = colors.ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(mockupDp(8)))
            Text(
                "time.ir isn't responding, so this month's occasions may be out of date. Your own tasks are saved locally and unaffected.",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = mockupSp(MockupDimens.OFFLINE_SUB),
                    lineHeight = mockupSp(MockupDimens.OFFLINE_SUB * 1.6f)
                ),
                color = offlineSubColor(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(mockupDp(20)))
            SweetPixelButton(text = "TRY AGAIN", onClick = onRetry)
        }
    }
}
