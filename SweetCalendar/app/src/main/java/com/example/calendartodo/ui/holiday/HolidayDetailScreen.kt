package com.example.calendartodo.ui.holiday

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.calendartodo.calendar.CalendarSystem
import com.example.calendartodo.jalali.GregorianDate
import com.example.calendartodo.jalali.JalaliDate
import com.example.calendartodo.ui.components.CalMiniIcon
import com.example.calendartodo.ui.components.NavPeppermintIcon
import com.example.calendartodo.ui.components.SweetIconButton
import com.example.calendartodo.ui.theme.BodyFont
import com.example.calendartodo.ui.theme.MockupDimens
import com.example.calendartodo.ui.theme.SweetTheme
import com.example.calendartodo.ui.theme.mockupDp
import com.example.calendartodo.ui.theme.mockupSp

private val HolidayDateLight = Color(0xFF8A7867)
private val HolidayBodyLight = Color(0xFF6B5A4B)
private val HolidaySourceLight = Color(0xFFB39D89)
private val HolidayTagHolidayBgLight = Color(0xFFE3F7EE)
private val HolidayTagOccasionBgLight = Color(0xFFFFF5D6)
private val HeroGradientStartLight = Color(0xFFDFF7EC)
private val HeroGradientEndLight = Color(0xFFF3E3FB)
private val HeroGradientStartDark = Color(0xFF1E3A32)
private val HeroGradientEndDark = Color(0xFF241C36)

@Composable
private fun holidayDateColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else HolidayDateLight
}

@Composable
private fun holidayBodyColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else HolidayBodyLight
}

@Composable
private fun holidaySourceColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.muted else HolidaySourceLight
}

@Composable
private fun holidayTagStyle(isHoliday: Boolean): Pair<Color, Color> {
    val colors = SweetTheme.colors
    return if (colors.isDark) {
        if (isHoliday) colors.holidayBg to colors.mint
        else colors.streakBg to colors.purpleDeep
    } else {
        if (isHoliday) HolidayTagHolidayBgLight to colors.mintDeep
        else HolidayTagOccasionBgLight to colors.purpleDeep
    }
}

@Composable
fun HolidayDetailScreen(
    date: JalaliDate,
    title: String,
    description: String,
    isHoliday: Boolean = true,
    calendarSystem: CalendarSystem = CalendarSystem.PERSIAN,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val colors = SweetTheme.colors
    val (tagBg, tagText) = holidayTagStyle(isHoliday)
    val heroGradient = if (colors.isDark) {
        Brush.linearGradient(
            colors = listOf(HeroGradientStartDark, HeroGradientEndDark),
            start = Offset.Zero,
            end = Offset(400f, 700f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(HeroGradientStartLight, HeroGradientEndLight),
            start = Offset.Zero,
            end = Offset(400f, 700f)
        )
    }
    val bodyText = when {
        description.isNotBlank() && !description.equals(title, ignoreCase = true) -> description
        isHoliday -> {
            "A local holiday synced from the Iranian calendar. Keep your to-do list light and enjoy the day."
        }
        else -> "A calendar occasion synced from time.ir."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.cream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mockupDp(MockupDimens.HOLIDAY_HERO_H))
                .background(heroGradient),
            contentAlignment = Alignment.Center
        ) {
            SweetIconButton(
                label = "←",
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(mockupDp(16))
            )
            NavPeppermintIcon(size = mockupDp(MockupDimens.HOLIDAY_HERO_ICON))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = mockupDp(18),
                    end = mockupDp(18),
                    top = mockupDp(16),
                    bottom = mockupDp(24)
                )
        ) {
            Text(
                if (isHoliday) "OFFICIAL HOLIDAY" else "CALENDAR OCCASION",
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.HOLIDAY_TAG_F),
                    lineHeight = mockupSp(12f)
                ),
                color = tagText,
                modifier = Modifier
                    .clip(RoundedCornerShape(mockupDp(8)))
                    .background(tagBg)
                    .padding(horizontal = mockupDp(10), vertical = mockupDp(5))
            )
            Spacer(Modifier.height(mockupDp(10)))
            Text(
                title,
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = mockupSp(MockupDimens.HOLIDAY_TITLE),
                    lineHeight = mockupSp(22f)
                ),
                color = colors.ink
            )
            Text(
                formatHolidayDateLine(date, calendarSystem),
                style = TextStyle(
                    fontFamily = BodyFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = mockupSp(MockupDimens.HOLIDAY_DATE_F),
                    lineHeight = mockupSp(16f)
                ),
                color = holidayDateColor(),
                modifier = Modifier.padding(top = mockupDp(6), bottom = mockupDp(16))
            )
            HolidayNotesBox(text = bodyText)
            Spacer(Modifier.height(mockupDp(18)))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(mockupDp(8))
            ) {
                CalMiniIcon(
                    size = mockupDp(MockupDimens.HOLIDAY_SOURCE_ICON),
                    color = colors.purpleDeep
                )
                Text(
                    "Synced from time.ir",
                    style = TextStyle(
                        fontFamily = BodyFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = mockupSp(MockupDimens.HOLIDAY_SOURCE_F),
                        lineHeight = mockupSp(14f)
                    ),
                    color = holidaySourceColor()
                )
            }
        }
    }
}

private fun formatHolidayDateLine(date: JalaliDate, calendarSystem: CalendarSystem): String {
    val weekday = JalaliDate.WEEKDAY_NAMES_EN[date.weekdayIndex()]
    return when (calendarSystem) {
        CalendarSystem.PERSIAN ->
            "${JalaliDate.MONTH_NAMES_EN[date.month - 1]} ${date.day}, ${date.year} · $weekday"
        CalendarSystem.GREGORIAN -> {
            val g = GregorianDate.fromJalali(date)
            "${GregorianDate.MONTH_NAMES_EN[g.month - 1]} ${g.day}, ${g.year} · $weekday"
        }
    }
}

@Composable
private fun HolidayNotesBox(text: String) {
    val colors = SweetTheme.colors
    val radius = mockupDp(MockupDimens.FORM_FIELD_RADIUS)
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = mockupDp(MockupDimens.FORM_FIELD_SHADOW))
                .clip(RoundedCornerShape(radius))
                .background(colors.line)
        )
        Text(
            text,
            style = TextStyle(
                fontFamily = BodyFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = mockupSp(MockupDimens.HOLIDAY_BODY_F),
                lineHeight = mockupSp(20f)
            ),
            color = holidayBodyColor(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.paper)
                .padding(horizontal = mockupDp(14), vertical = mockupDp(12))
        )
    }
}
