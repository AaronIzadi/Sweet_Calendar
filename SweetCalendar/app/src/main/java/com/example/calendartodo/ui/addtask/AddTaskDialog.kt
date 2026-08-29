package com.example.calendartodo.ui.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calendartodo.ui.components.LollipopIcon
import com.example.calendartodo.ui.components.PixelButton
import com.example.calendartodo.ui.components.PixelPanel
import com.example.calendartodo.ui.components.pixelBorder
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.ChocolateBrown
import com.example.calendartodo.ui.theme.CottonCandyPink
import com.example.calendartodo.ui.theme.CreamFrosting
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen

@Composable
fun AddTaskDialog(
    dayLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val isValid = title.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        PixelPanel(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = CottonCandyPink
        ) {
            Column {
                LollipopIcon(size = 32.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "New sweet task",
                    style = MaterialTheme.typography.titleSmall,
                    color = ChocolateBrown
                )
                Text(
                    dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocolateBrown.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                PixelTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                PixelTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes (optional)",
                    singleLine = false
                )
                Spacer(Modifier.height(16.dp))
                RowButtons(
                    isValid = isValid,
                    onDismiss = onDismiss,
                    onConfirm = { onConfirm(title, notes) }
                )
            }
        }
    }
}

@Composable
private fun PixelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = ChocolateBrown),
            cursorBrush = SolidColor(BubblegumPink),
            modifier = Modifier
                .fillMaxWidth()
                .pixelBorder(borderWidth = 2.dp, shadowOffset = 2.dp)
                .background(CreamFrosting)
                .padding(8.dp)
        )
    }
}

@Composable
private fun RowButtons(
    isValid: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        PixelButton(onClick = onDismiss, backgroundColor = LemonYellow) {
            Text("Cancel", style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        }
        Spacer(Modifier.padding(4.dp))
        PixelButton(
            onClick = { if (isValid) onConfirm() },
            backgroundColor = if (isValid) MintGreen else MintGreen.copy(alpha = 0.4f)
        ) {
            Text("Add", style = MaterialTheme.typography.labelSmall, color = ChocolateBrown)
        }
    }
}
