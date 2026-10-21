package com.sweetcalendar.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sweetcalendar.BuildConfig
import com.sweetcalendar.ui.components.SheetFooterCloseOnly
import com.sweetcalendar.ui.components.SweetDialogSurface
import com.sweetcalendar.ui.components.sheetDialogTitlePaddingModifier
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.LocalMockupScale
import com.sweetcalendar.ui.theme.ProvideMockupScale
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp

private const val GITHUB_URL = "https://github.com/AaronIzadi"
private const val GITHUB_LABEL = "github.com/AaronIzadi"

/** Shrink/grow about copy so it fits the dialog body without scrolling. */
private fun TextStyle.scaleAbout(factor: Float): TextStyle = copy(
    fontSize = (fontSize.value * factor).sp,
    lineHeight = (lineHeight.value * factor).sp,
)

private fun aboutPanelFitScale(availableHeightDp: Float, mockupScale: Float): Float {
    val refPanel = MockupDimens.ABOUT_PANEL_REF_HEIGHT * mockupScale
    return (availableHeightDp / refPanel)
        .coerceIn(MockupDimens.ABOUT_TEXT_SCALE_MIN, MockupDimens.ABOUT_TEXT_SCALE_MAX)
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val colors = SweetTheme.colors
    val context = LocalContext.current

    val titleStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = mockupSp(MockupDimens.ABOUT_TITLE_F),
        lineHeight = mockupSp(MockupDimens.ABOUT_TITLE_LINE),
    )
    val versionStyle = TextStyle(
        fontFamily = BodyFont,
        fontWeight = SecondaryTextWeight,
        fontSize = mockupSp(MockupDimens.ABOUT_VERSION_F),
        lineHeight = mockupSp(MockupDimens.ABOUT_VERSION_LINE),
    )
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
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val dialogHeight = (
        screenHeightDp * MockupDimens.ABOUT_DIALOG_HEIGHT_FRACTION -
            MockupDimens.ABOUT_DIALOG_VERTICAL_MARGIN * 2
        ).coerceAtLeast(320f).dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        ProvideMockupScale {
        SweetDialogSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = mockupDp(14),
                    vertical = mockupDp(MockupDimens.ABOUT_DIALOG_VERTICAL_MARGIN),
                ),
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(dialogHeight)
                .widthIn(max = 360.dp)
                .clip(RoundedCornerShape(mockupDp(16)))
                .background(colors.cream)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                val mockupScale = LocalMockupScale.current
                val headerRef = MockupDimens.ABOUT_HEADER_REF_HEIGHT * mockupScale
                val headerScale = (maxHeight.value * 0.22f / headerRef)
                    .coerceIn(MockupDimens.ABOUT_TEXT_SCALE_MIN, MockupDimens.ABOUT_TEXT_SCALE_MAX)

                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "About Sweet Calendar",
                        style = titleStyle.scaleAbout(headerScale),
                        color = colors.ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(sheetDialogTitlePaddingModifier())
                            .padding(bottom = mockupDp(2)),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "v${BuildConfig.VERSION_NAME}",
                        style = versionStyle.scaleAbout(headerScale),
                        color = colors.muted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = mockupDp(6)),
                        textAlign = TextAlign.Center
                    )

                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(
                                start = mockupDp(18),
                                end = mockupDp(18),
                                bottom = mockupDp(6),
                            ),
                    ) {
                        val density = LocalDensity.current
                        var panelTextScale by remember(maxHeight, mockupScale) {
                            mutableFloatStateOf(
                                aboutPanelFitScale(maxHeight.value, mockupScale)
                            )
                        }

                        AboutBodyPanel(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { coordinates ->
                                        val contentHeight = with(density) {
                                            coordinates.size.height.toDp()
                                        }
                                        if (contentHeight > maxHeight && contentHeight > 0.dp) {
                                            val corrected = panelTextScale *
                                                (maxHeight.value / contentHeight.value) * 0.96f
                                            if (corrected < panelTextScale - 0.01f) {
                                                panelTextScale = corrected.coerceAtLeast(
                                                    MockupDimens.ABOUT_TEXT_SCALE_MIN
                                                )
                                            }
                                        }
                                    },
                                verticalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Text(
                                    "Hey there! 👋🍬",
                                    style = leadStyle.scaleAbout(panelTextScale),
                                    color = colors.ink
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("I'm ")
                                        withStyle(boldStyle) { append("Aaron") }
                                        append(", the human behind Sweet Calendar.")
                                    },
                                    style = bodyStyle.scaleAbout(panelTextScale),
                                    color = colors.muted
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("I'm an ")
                                        withStyle(boldStyle) { append("academic nerd and software developer") }
                                        append(" who apparently thought, \"You know what my life needs? Another side project.\" 😂")
                                    },
                                    style = bodyStyle.scaleAbout(panelTextScale),
                                    color = colors.muted
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("So I made a cute little to-do app with a switchable ")
                                        withStyle(boldStyle) { append("calendar system") }
                                        append(", reminders, widgets, stats, and way too many sweet pixels. 🍭📅")
                                    },
                                    style = bodyStyle.scaleAbout(panelTextScale),
                                    color = colors.muted
                                )
                                Text(
                                    buildAnnotatedString {
                                        append("I hope Sweet Calendar makes organizing your day a little less boring and a little more... well, ")
                                        withStyle(boldStyle) { append("sweet") }
                                        append(". ✨")
                                    },
                                    style = bodyStyle.scaleAbout(panelTextScale),
                                    color = colors.muted
                                )
                                Text(
                                    "Thanks for stopping by! 💗",
                                    style = bodyStyle.scaleAbout(panelTextScale),
                                    color = colors.muted
                                )
                                Text(
                                    GITHUB_LABEL,
                                    style = linkStyle.scaleAbout(panelTextScale),
                                    color = colors.purpleDeep,
                                    modifier = Modifier.clickable {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
                                    }
                                )
                            }
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
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.shadow)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(20), vertical = mockupDp(14)),
            content = { content() }
        )
    }
}
