package com.example.calendartodo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val SnackbarBackground = Color(0xFF3A2A22)
private val SnackbarTextColor = Color(0xFFFFF6EA)

@Composable
fun SweetRecoverSnackbar(
    message: String,
    visible: Boolean,
    onRecover: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String = "RECOVER"
) {
    val colors = SweetTheme.colors
    val shape = RoundedCornerShape(mockupDp(MockupDimens.SNACKBAR_RADIUS))

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mockupDp(MockupDimens.SNACKBAR_EDGE))
                .shadow(
                    elevation = mockupDp(6),
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(shape)
                .background(SnackbarBackground)
                .padding(
                    horizontal = mockupDp(MockupDimens.SNACKBAR_PAD_H),
                    vertical = mockupDp(MockupDimens.SNACKBAR_PAD_V)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                message,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.SNACKBAR_MSG_F),
                    lineHeight = mockupSp(16f)
                ),
                color = SnackbarTextColor,
                modifier = Modifier.weight(1f)
            )
            Text(
                actionLabel,
                style = TextStyle(
                    fontFamily = PixelFont,
                    fontSize = mockupSp(MockupDimens.SNACKBAR_RECOVER),
                    letterSpacing = mockupSp(0.5f),
                    lineHeight = mockupSp(14f)
                ),
                color = colors.lemon,
                modifier = Modifier
                    .clickable(onClick = onRecover)
                    .padding(start = mockupDp(10))
            )
        }
    }
}
