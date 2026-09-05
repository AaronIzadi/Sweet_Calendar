package com.example.calendartodo.ui.alarm

import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.calendartodo.data.prefs.AppPreferences
import com.example.calendartodo.ui.components.ThemeAlarmHeroIcon
import com.example.calendartodo.ui.components.SweetPixelButton
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.CalendarTodoTheme
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.ProvideMockupScale
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val dateTime = intent.getStringExtra(EXTRA_DATE_TIME).orEmpty()
        val preferences = AppPreferences(this)

        runCatching {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(this, uri)?.play()
        }

        setContent {
            CalendarTodoTheme(
                darkTheme = preferences.darkMode,
                themeFamily = preferences.themeFamily
            ) {
                ProvideMockupScale {
                    val colors = SweetTheme.colors
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.cream)
                            .padding(horizontal = mockupDp(30)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(mockupDp(80)))
                        ThemeAlarmHeroIcon(
                            size = mockupDp(72),
                            color = colors.pink
                        )
                        Spacer(Modifier.height(mockupDp(24)))
                        Text(
                            title,
                            style = TextStyle(
                                fontFamily = BodyFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = mockupSp(MockupDimens.OFFLINE_TITLE),
                                lineHeight = mockupSp(20f)
                            ),
                            color = colors.ink,
                            textAlign = TextAlign.Center
                        )
                        if (description.isNotBlank()) {
                            Spacer(Modifier.height(mockupDp(8)))
                            Text(
                                description,
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = mockupSp(12f),
                                    lineHeight = mockupSp(18f)
                                ),
                                color = colors.muted,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (dateTime.isNotBlank()) {
                            Spacer(Modifier.height(mockupDp(12)))
                            Text(
                                dateTime,
                                style = TextStyle(
                                    fontFamily = BodyFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = mockupSp(13f),
                                    lineHeight = mockupSp(18f)
                                ),
                                color = colors.muted,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(mockupDp(32)))
                        SweetPixelButton(
                            text = "CLOSE",
                            onClick = { finish() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_DESCRIPTION = "description"
        const val EXTRA_DATE_TIME = "date_time"
    }
}
