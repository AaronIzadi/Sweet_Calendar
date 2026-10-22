package com.sweetcalendar.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sweetcalendar.BuildConfig
import com.sweetcalendar.ui.components.SheetFooterCloseOnly
import com.sweetcalendar.ui.components.SweetDialogSurface
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.LocalMockupScale
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

private const val GITHUB_URL = "https://github.com/AaronIzadi"
private const val GITHUB_LABEL = "github.com/AaronIzadi"

private fun TextStyle.scaleAbout(factor: Float): TextStyle {
    val scaledSize = (fontSize.value * factor).sp
    val lineRatio = (lineHeight.value / fontSize.value).coerceIn(1f, 1.35f)
    return copy(
        fontSize = scaledSize,
        lineHeight = (scaledSize.value * lineRatio).sp,
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val colors = SweetTheme.colors
    val context = LocalContext.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val bodyMaxHeight = (screenHeightDp * MockupDimens.ABOUT_POPUP_BODY_MAX_FRACTION).dp

    val bodyStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.ABOUT_BODY_F),
        lineHeight = mockupSp(MockupDimens.ABOUT_BODY_LINE),
    )
    val leadStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = mockupSp(MockupDimens.ABOUT_LEAD_F),
        lineHeight = mockupSp(MockupDimens.ABOUT_LEAD_LINE),
    )
    val linkStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = mockupSp(MockupDimens.ABOUT_BODY_F),
        lineHeight = mockupSp(MockupDimens.ABOUT_BODY_LINE),
        textDecoration = TextDecoration.Underline
    )
    val boldStyle = SpanStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        color = colors.ink
    )

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            SweetDialogSurface {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(mockupDp(16)))
                        .background(colors.cream)
                ) {
                    Text(
                        "About Sweet Calendar",
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                            lineHeight = mockupSp(22f),
                        ),
                        color = colors.ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(sheetDialogTitlePaddingModifier()),
                        textAlign = TextAlign.Center
                    )

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = mockupDp(18),
                                end = mockupDp(18),
                                bottom = mockupDp(8),
                            ),
                    ) {
                        val mockupScale = LocalMockupScale.current
                        val density = LocalDensity.current
                        val contentMaxHeight = minOf(maxHeight, bodyMaxHeight)
                        var bodyScale by remember(contentMaxHeight, mockupScale) {
                            mutableFloatStateOf(1f)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = contentMaxHeight)
                                .wrapContentHeight()
                                .onGloballyPositioned { coordinates ->
                                    val contentHeight = with(density) {
                                        coordinates.size.height.toDp()
                                    }
                                    if (contentHeight > contentMaxHeight && contentHeight > 0.dp) {
                                        val corrected = bodyScale *
                                            (contentMaxHeight.value / contentHeight.value) * 0.96f
                                        if (corrected < bodyScale) {
                                            bodyScale = corrected.coerceAtLeast(
                                                MockupDimens.ABOUT_TEXT_SCALE_MIN
                                            )
                                        }
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(mockupDp(6) * bodyScale),
                        ) {
                            Text(
                                "v${BuildConfig.VERSION_NAME}",
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = SecondaryTextWeight,
                                    fontSize = mockupSp(MockupDimens.ABOUT_VERSION_F),
                                    lineHeight = mockupSp(MockupDimens.ABOUT_VERSION_LINE),
                                ).scaleAbout(bodyScale),
                                color = colors.muted,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            AboutBodyPanel(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(mockupDp(4) * bodyScale),
                                ) {
                                    Text(
                                        "Hey there! 👋🍬",
                                        style = leadStyle.scaleAbout(bodyScale),
                                        color = colors.ink
                                    )
                                    Text(
                                        buildAnnotatedString {
                                            append("I'm ")
                                            withStyle(boldStyle) { append("Aaron") }
                                            append(", the human behind Sweet Calendar.")
                                        },
                                        style = bodyStyle.scaleAbout(bodyScale),
                                        color = colors.muted
                                    )
                                    Text(
                                        buildAnnotatedString {
                                            append("I'm an ")
                                            withStyle(boldStyle) { append("academic nerd and software developer") }
                                            append(" who apparently thought, \"You know what my life needs? Another side project.\" 😂")
                                        },
                                        style = bodyStyle.scaleAbout(bodyScale),
                                        color = colors.muted
                                    )
                                    Text(
                                        buildAnnotatedString {
                                            append("So I made a cute little to-do app with a switchable ")
                                            withStyle(boldStyle) { append("calendar system") }
                                            append(", reminders, widgets, stats, and way too many sweet pixels. 🍭📅")
                                        },
                                        style = bodyStyle.scaleAbout(bodyScale),
                                        color = colors.muted
                                    )
                                    Text(
                                        buildAnnotatedString {
                                            append("I hope Sweet Calendar makes organizing your day a little less boring and a little more... well, ")
                                            withStyle(boldStyle) { append("sweet") }
                                            append(". ✨")
                                        },
                                        style = bodyStyle.scaleAbout(bodyScale),
                                        color = colors.muted
                                    )
                                    Text(
                                        "Thanks for stopping by! 💗",
                                        style = bodyStyle.scaleAbout(bodyScale),
                                        color = colors.muted
                                    )
                                    Text(
                                        GITHUB_LABEL,
                                        style = linkStyle.scaleAbout(bodyScale),
                                        color = colors.purpleDeep,
                                        modifier = Modifier.clickable {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SheetFooterCloseOnly(onClose = onDismiss)
                }
            }
        }
    }
}

@Composable
private fun AboutBodyPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = modifier.wrapContentHeight()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(10)),
            content = { content() }
        )
    }
}
