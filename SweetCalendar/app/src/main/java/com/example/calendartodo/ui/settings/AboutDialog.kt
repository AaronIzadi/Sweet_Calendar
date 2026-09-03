package com.example.calendartodo.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import com.example.calendartodo.BuildConfig
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.PixelFont
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private const val GITHUB_URL = "https://github.com/AaronIzadi"
private const val GITHUB_LABEL = "github.com/AaronIzadi"

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val colors = SweetTheme.colors
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        ProvideMockupScale {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(mockupDp(16)))
                    .background(colors.cream)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = mockupDp(18),
                            end = mockupDp(18),
                            top = mockupDp(16),
                            bottom = mockupDp(8)
                        )
                ) {
                    Text(
                        "✕ CLOSE",
                        style = TextStyle(
                            fontFamily = PixelFont,
                            fontSize = mockupSp(MockupDimens.SHEET_HEADER_BTN),
                            lineHeight = mockupSp(14f)
                        ),
                        color = colors.purpleDeep,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                Text(
                    "About Sweet Calendar",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.SHEET_TITLE),
                        lineHeight = mockupSp(22f)
                    ),
                    color = colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(4)),
                    textAlign = TextAlign.Center
                )

                Text(
                    "v${BuildConfig.VERSION_NAME}",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(11f),
                        lineHeight = mockupSp(15f)
                    ),
                    color = colors.muted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mockupDp(12)),
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = mockupDp(24),
                            end = mockupDp(24),
                            bottom = mockupDp(24)
                        )
                ) {
                    val bodyStyle = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mockupSp(12f),
                        lineHeight = mockupSp(18f)
                    )
                    val boldStyle = SpanStyle(fontWeight = FontWeight.Bold, color = colors.ink)

                    Text(
                        "Hey there! 👋🍬",
                        style = bodyStyle.copy(fontWeight = FontWeight.Bold, fontSize = mockupSp(13f)),
                        color = colors.ink
                    )
                    Spacer(Modifier.height(mockupDp(12)))
                    Text(
                        buildAnnotatedString {
                            append("I'm ")
                            withStyle(boldStyle) { append("Aaron") }
                            append(", the human behind Sweet Calendar.")
                        },
                        style = bodyStyle,
                        color = colors.muted
                    )
                    Spacer(Modifier.height(mockupDp(10)))
                    Text(
                        buildAnnotatedString {
                            append("I'm an ")
                            withStyle(boldStyle) { append("academic nerd and software developer") }
                            append(" who apparently thought, \"You know what my life needs? Another side project.\" 😂")
                        },
                        style = bodyStyle,
                        color = colors.muted
                    )
                    Spacer(Modifier.height(mockupDp(10)))
                    Text(
                        buildAnnotatedString {
                            append("So I made a cute little to-do app with a switchable ")
                            withStyle(boldStyle) { append("calendar system") }
                            append(", reminders, widgets, stats, and way too many sweet pixels. 🍭📅")
                        },
                        style = bodyStyle,
                        color = colors.muted
                    )
                    Spacer(Modifier.height(mockupDp(10)))
                    Text(
                        buildAnnotatedString {
                            append("I hope Sweet Calendar makes organizing your day a little less boring and a little more... well, ")
                            withStyle(boldStyle) { append("sweet") }
                            append(". ✨")
                        },
                        style = bodyStyle,
                        color = colors.muted
                    )
                    Spacer(Modifier.height(mockupDp(10)))
                    Text(
                        "Thanks for stopping by! 💗",
                        style = bodyStyle,
                        color = colors.muted
                    )
                    Spacer(Modifier.height(mockupDp(16)))
                    Text(
                        GITHUB_LABEL,
                        style = TextStyle(
                            fontFamily = BodyFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = mockupSp(12f),
                            lineHeight = mockupSp(18f),
                            textDecoration = TextDecoration.Underline
                        ),
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
