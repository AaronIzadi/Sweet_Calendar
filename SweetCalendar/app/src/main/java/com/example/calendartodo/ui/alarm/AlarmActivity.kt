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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.components.AlarmBellIllustration
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.CalendarTodoTheme
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CreamFrosting

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val dateTime = intent.getStringExtra(EXTRA_DATE_TIME).orEmpty()

        runCatching {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(this, uri)?.play()
        }

        setContent {
            CalendarTodoTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CreamFrosting)
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(80.dp))
                    AlarmBellIllustration()
                    Spacer(Modifier.height(40.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, color = ChocolateBrown)
                    if (description.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            description,
                            style = MaterialTheme.typography.bodySmall,
                            color = ChocolateBrown.copy(alpha = 0.8f)
                        )
                    }
                    if (dateTime.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(dateTime, style = MaterialTheme.typography.bodyMedium, color = ChocolateBrown)
                    }
                    Spacer(Modifier.height(40.dp))
                    PixelButton(
                        onClick = { finish() },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = BubblegumPink
                    ) {
                        Text("Close", style = MaterialTheme.typography.labelSmall, color = CreamFrosting)
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
