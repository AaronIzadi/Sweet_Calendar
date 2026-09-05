package com.example.calendartodo.ui.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.ui.components.BigBellIcon
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val PermissionSecondaryLight = Color(0xFF9A8878)
private val PermissionGradientTopLight = Color(0xFFFFF6EA)
private val PermissionGradientBottomLight = Color(0xFFF3E3FB)
private val PermissionGradientTopDark = Color(0xFF241A30)
private val PermissionGradientBottomDark = Color(0xFF1B1526)

@Composable
private fun permissionSecondaryColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else PermissionSecondaryLight
}

@Composable
fun NotificationPermissionScreen(
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SweetTheme.colors
    val gradient = if (colors.isDark) {
        Brush.verticalGradient(
            listOf(PermissionGradientTopDark, PermissionGradientBottomDark)
        )
    } else {
        Brush.verticalGradient(
            listOf(PermissionGradientTopLight, PermissionGradientBottomLight)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .padding(
                start = mockupDp(MockupDimens.PERMISSION_PAD_H),
                end = mockupDp(MockupDimens.PERMISSION_PAD_H),
                bottom = mockupDp(MockupDimens.PERMISSION_PAD_BOTTOM)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BigBellIcon(
            size = mockupDp(MockupDimens.PERMISSION_BELL),
            color = colors.pink
        )
        Spacer(Modifier.height(mockupDp(20)))
        Text(
            "Never miss a sweet reminder",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.Bold,
                fontSize = mockupSp(MockupDimens.PERMISSION_TITLE),
                lineHeight = mockupSp(22.4f)
            ),
            color = colors.ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(mockupDp(10)))
        Text(
            "Sweet Calendar can notify you a little before each task is due — you can turn this off anytime in Settings.",
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.PERMISSION_SUB),
                lineHeight = mockupSp(MockupDimens.PERMISSION_SUB * 1.65f)
            ),
            color = colors.muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(mockupDp(26)))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(mockupDp(10))
        ) {
            SweetPixelButton(
                text = "ALLOW NOTIFICATIONS",
                onClick = onAllow,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Not now",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.PERMISSION_SECONDARY),
                    lineHeight = mockupSp(16f)
                ),
                color = permissionSecondaryColor(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDismiss)
                    .padding(mockupDp(12))
            )
        }
    }
}
