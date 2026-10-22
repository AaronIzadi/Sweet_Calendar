package com.sweetcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import com.sweetcalendar.ui.theme.BodyFont
import com.sweetcalendar.ui.theme.MockupDimens
import com.sweetcalendar.ui.theme.SecondaryTextWeight
import com.sweetcalendar.ui.theme.SweetTheme
import com.sweetcalendar.ui.theme.mockupDp
import com.sweetcalendar.ui.theme.mockupSp
import kotlin.math.max
import kotlin.math.min

enum class NotesFormat { Bold, Italic, Bullet }

fun applyNotesFormat(value: TextFieldValue, format: NotesFormat): TextFieldValue {
    return when (format) {
        NotesFormat.Bold -> wrapNotesMarker(value, "**")
        NotesFormat.Italic -> wrapNotesMarker(value, "*")
        NotesFormat.Bullet -> prefixBulletLine(value)
    }
}

private fun wrapNotesMarker(value: TextFieldValue, marker: String): TextFieldValue {
    val text = value.text
    val start = min(value.selection.start, value.selection.end)
    val end = max(value.selection.start, value.selection.end)
    if (start == end) {
        val newText = buildString {
            append(text.substring(0, start))
            append(marker)
            append(marker)
            append(text.substring(start))
        }
        val cursor = start + marker.length
        return TextFieldValue(newText, TextRange(cursor))
    }
    val newText = buildString {
        append(text.substring(0, start))
        append(marker)
        append(text.substring(start, end))
        append(marker)
        append(text.substring(end))
    }
    return TextFieldValue(newText, TextRange(end + marker.length * 2))
}

private fun prefixBulletLine(value: TextFieldValue): TextFieldValue {
    val text = value.text
    val cursor = value.selection.start
    val lineStart = text.lastIndexOf('\n', cursor - 1).let { if (it < 0) 0 else it + 1 }
    val lineEnd = text.indexOf('\n', cursor).let { if (it < 0) text.length else it }
    val line = text.substring(lineStart, lineEnd)
    if (line.startsWith("- ")) {
        val newText = text.substring(0, lineStart) + line.removePrefix("- ") + text.substring(lineEnd)
        val newCursor = max(lineStart, cursor - 2)
        return TextFieldValue(newText, TextRange(newCursor))
    }
    val newText = text.substring(0, lineStart) + "- " + line + text.substring(lineEnd)
    val newCursor = cursor + 2
    return TextFieldValue(newText, TextRange(newCursor))
}

/** One-line preview for list cards — strips note markup. */
fun notesPlainTextPreview(text: String): String {
    if (text.isBlank()) return text
    return text
        .lineSequence()
        .map { line ->
            val body = if (line.startsWith("- ")) line.drop(2) else line
            stripInlineNotesMarkup(body)
        }
        .joinToString(" ")
        .trim()
}

private fun stripInlineNotesMarkup(line: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < line.length) {
        if (line.startsWith("**", i)) {
            val end = line.indexOf("**", i + 2)
            if (end != -1) {
                sb.append(line.substring(i + 2, end))
                i = end + 2
                continue
            }
        }
        if (line[i] == '*' && i + 1 < line.length && line[i + 1] != '*') {
            val end = line.indexOf('*', i + 1)
            if (end != -1) {
                sb.append(line.substring(i + 1, end))
                i = end + 1
                continue
            }
        }
        sb.append(line[i])
        i++
    }
    return sb.toString()
}

/** Body text uses ExtraBold in the app; notes rendering uses a lighter base so emphasis reads clearly. */
private fun notesRenderBaseStyle(baseStyle: SpanStyle): SpanStyle {
    val weight = baseStyle.fontWeight ?: FontWeight.Normal
    val renderWeight = when {
        weight.weight >= FontWeight.SemiBold.weight -> FontWeight.Normal
        else -> weight
    }
    return baseStyle.copy(
        fontWeight = renderWeight,
        fontFamily = baseStyle.fontFamily ?: BodyFont,
    )
}

private fun notesBoldStyle(base: SpanStyle): SpanStyle =
    base.copy(
        fontWeight = FontWeight.ExtraBold,
        fontFamily = BodyFont,
    )

private fun notesItalicStyle(base: SpanStyle): SpanStyle =
    base.copy(fontStyle = FontStyle.Italic)

fun buildNotesAnnotatedString(
    text: String,
    baseStyle: SpanStyle,
): AnnotatedString = buildAnnotatedString {
    val base = notesRenderBaseStyle(baseStyle)
    val lines = text.split('\n')
    lines.forEachIndexed { index, line ->
        val isBullet = line.startsWith("- ")
        val body = if (isBullet) line.drop(2) else line
        if (isBullet) {
            withStyle(base) { append("•  ") }
        }
        appendInlineNotes(body, base)
        if (index < lines.lastIndex) append('\n')
    }
}

private fun AnnotatedString.Builder.appendInlineNotes(
    text: String,
    base: SpanStyle,
) {
    val bold = notesBoldStyle(base)
    val italic = notesItalicStyle(base)
    var i = 0
    while (i < text.length) {
        if (text.startsWith("**", i)) {
            val end = text.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(bold) { append(text.substring(i + 2, end)) }
                i = end + 2
                continue
            }
        }
        if (text[i] == '*' && i + 1 < text.length && text[i + 1] != '*') {
            val end = text.indexOf('*', i + 1)
            if (end != -1) {
                withStyle(italic) { append(text.substring(i + 1, end)) }
                i = end + 1
                continue
            }
        }
        val nextBold = text.indexOf("**", i)
        val nextItalicStar = text.indexOf('*', i)
        val nextSpecial = listOf(
            if (nextBold == -1) text.length else nextBold,
            if (nextItalicStar == -1) text.length else nextItalicStar,
        ).min()
        if (nextSpecial <= i) {
            withStyle(base) { append(text[i]) }
            i++
        } else {
            withStyle(base) { append(text.substring(i, nextSpecial)) }
            i = nextSpecial
        }
    }
}

@Composable
fun RichNotesText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val renderStyle = style.copy(
        fontWeight = FontWeight.Normal,
        fontFamily = style.fontFamily ?: BodyFont,
    )
    val base = renderStyle.toSpanStyle().copy(
        color = color,
        fontWeight = FontWeight.Normal,
        fontFamily = BodyFont,
    )
    Text(
        text = buildNotesAnnotatedString(text, base),
        style = renderStyle,
        color = Color.Unspecified,
        modifier = modifier,
    )
}

@Composable
fun NotesFormatToolbar(
    onFormat: (NotesFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.focusProperties { canFocus = false },
        horizontalArrangement = Arrangement.spacedBy(mockupDp(8)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NotesFormatButton(label = "B", bold = true, onClick = { onFormat(NotesFormat.Bold) })
        NotesFormatButton(label = "I", italic = true, onClick = { onFormat(NotesFormat.Italic) })
        NotesFormatButton(label = "•", onClick = { onFormat(NotesFormat.Bullet) })
    }
}

@Composable
private fun NotesFormatButton(
    label: String,
    onClick: () -> Unit,
    bold: Boolean = false,
    italic: Boolean = false,
) {
    val colors = SweetTheme.colors
    val radius = mockupDp(8)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(radius))
            .border(mockupDp(1), colors.fieldOutline, RoundedCornerShape(radius))
            .background(colors.surfaceInset)
            .pointerInput(onClick) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    onClick()
                    waitForUpOrCancellation()
                }
            }
            .padding(horizontal = mockupDp(12), vertical = mockupDp(6)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = if (bold) FontWeight.Bold else SecondaryTextWeight,
                fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                fontSize = mockupSp(MockupDimens.FIELD_LABEL),
            ),
            color = colors.purpleDeep,
        )
    }
}
