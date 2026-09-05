package com.example.calendartodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PinkDeep
import com.example.calendartodo.ui.theme.PixelPurple
import com.example.calendartodo.ui.theme.PixelPurpleHighlight
import com.example.calendartodo.ui.theme.SweetTheme

private val MockupLollipopStick = Color(0xFFE8A857)
private val MockupChocFillLight = Color(0xFF8A5A38)
private val MockupChocSeamLight = Color(0xFF5A3A22)
private val MockupChocFillDark = Color(0xFFC99770)
private val MockupChocSeamDark = Color(0xFF8A6B4E)
private val MockupGumBodyDark = Color(0xFFC3AEEF)
private val MockupGumHiDark = Color(0xFFE4D9FB)

@Composable
fun ThemeHeroIcon(modifier: Modifier = Modifier, size: Dp = 64.dp) {
    if (SweetTheme.isSpace) {
        RocketIcon(modifier = modifier, size = size)
    } else {
        val colors = SweetTheme.colors
        NavLollipopIcon(
            modifier = modifier,
            size = size,
            headColor = if (colors.isDark) colors.pink else PinkDeep,
            highlightColor = Color.White,
            stickColor = MockupLollipopStick
        )
    }
}

@Composable
fun ThemeFabIcon(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    if (SweetTheme.isSpace) {
        RocketIcon(modifier = modifier, size = size)
    } else {
        FabGumdropIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeCategoryTaskIcon(
    category: TaskCategory,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    if (SweetTheme.isSpace) {
        when (category) {
            TaskCategory.Personal -> StarIcon(modifier = modifier, size = size)
            TaskCategory.Home -> RingPlanetIcon(modifier = modifier, size = size)
            TaskCategory.Work -> RocketIcon(modifier = modifier, size = size)
        }
    } else {
        when (category) {
            TaskCategory.Personal -> TaskHeartIcon(modifier = modifier, size = size)
            TaskCategory.Home -> TaskLeafIcon(modifier = modifier, size = size)
            TaskCategory.Work -> TaskGemIcon(modifier = modifier, size = size)
        }
    }
}

@Composable
fun ThemeCategorySwatchIcon(
    category: TaskCategory,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    if (SweetTheme.isSpace) {
        when (category) {
            TaskCategory.Personal -> StarIcon(modifier = modifier, size = size)
            TaskCategory.Home -> RingPlanetIcon(modifier = modifier, size = size)
            TaskCategory.Work -> RocketIcon(modifier = modifier, size = size)
        }
    } else {
        when (category) {
            TaskCategory.Personal -> PeppermintCandyIcon(modifier = modifier, size = size)
            TaskCategory.Home -> ChocolateIcon(modifier = modifier, size = size)
            TaskCategory.Work -> WrappedCandyIcon(modifier = modifier, size = size)
        }
    }
}

@Composable
fun ThemeCompletedCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    bgColor: Color? = null
) {
    val colors = SweetTheme.colors
    if (SweetTheme.isSpace) {
        AstroCheckIcon(
            modifier = modifier,
            size = size,
            bgColor = bgColor ?: colors.mint
        )
    } else {
        CheckCandyIcon(
            modifier = modifier,
            size = size,
            bgColor = bgColor ?: if (colors.isDark) colors.mint else MintGreen
        )
    }
}

@Composable
fun ThemeEmptyStateIcon(modifier: Modifier = Modifier, size: Dp = 64.dp) {
    if (SweetTheme.isSpace) {
        MoonIcon(modifier = modifier, size = size)
    } else {
        EmptyStateIceCreamIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeOfflineHeroIcon(
    modifier: Modifier = Modifier,
    width: Dp = 64.dp,
    height: Dp = 56.dp
) {
    if (SweetTheme.isSpace) {
        SatelliteIcon(modifier = modifier, size = width)
    } else {
        MeltedCandyIcon(modifier = modifier, width = width, height = height)
    }
}

@Composable
fun ThemeStreakIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    if (SweetTheme.isSpace) {
        CometIcon(modifier = modifier, size = size)
    } else {
        val colors = SweetTheme.colors
        MockupChocolateIcon(
            modifier = modifier,
            size = size,
            fillColor = if (colors.isDark) MockupChocFillDark else MockupChocFillLight,
            seamColor = if (colors.isDark) MockupChocSeamDark else MockupChocSeamLight
        )
    }
}

@Composable
fun ThemeProfileAvatarIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    if (SweetTheme.isSpace) {
        RocketIcon(modifier = modifier, size = size)
    } else {
        ProfileLollipopIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeWelcomeDecoTopStart(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    if (SweetTheme.isSpace) {
        StarIcon(modifier = modifier, size = size)
    } else {
        NavPeppermintIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeWelcomeDecoTopEnd(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    if (SweetTheme.isSpace) {
        RingPlanetIcon(modifier = modifier, size = size)
    } else {
        val colors = SweetTheme.colors
        SettingsGumdropIcon(
            modifier = modifier,
            size = size,
            wrapColor = if (colors.isDark) colors.lemon else LemonYellow,
            bodyColor = if (colors.isDark) MockupGumBodyDark else PixelPurple,
            highlightColor = if (colors.isDark) MockupGumHiDark else PixelPurpleHighlight
        )
    }
}

@Composable
fun ThemeWelcomeDecoBottomStart(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    if (SweetTheme.isSpace) {
        CometIcon(modifier = modifier, size = size)
    } else {
        val colors = SweetTheme.colors
        MockupChocolateIcon(
            modifier = modifier,
            size = size,
            fillColor = if (colors.isDark) MockupChocFillDark else MockupChocFillLight,
            seamColor = if (colors.isDark) MockupChocSeamDark else MockupChocSeamLight
        )
    }
}

@Composable
fun ThemeSettingsThemeIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    if (SweetTheme.isSpace) {
        RocketIcon(modifier = modifier, size = size)
    } else {
        SettingsGumdropIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeNavTodayIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    headColor: Color,
    highlightColor: Color,
    stickColor: Color
) {
    if (SweetTheme.isSpace) {
        RocketIcon(
            modifier = modifier,
            size = size,
            palette = spaceNavIconPalette(headColor, highlightColor, stickColor)
        )
    } else {
        NavLollipopIcon(
            modifier = modifier,
            size = size,
            headColor = headColor,
            highlightColor = highlightColor,
            stickColor = stickColor
        )
    }
}

@Composable
fun ThemeNavWeekIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    color: Color,
    highlightColor: Color,
    accentColor: Color = color
) {
    if (SweetTheme.isSpace) {
        RingPlanetIcon(
            modifier = modifier,
            size = size,
            palette = spaceNavIconPalette(color, highlightColor, accentColor)
        )
    } else {
        NavPeppermintIcon(
            modifier = modifier,
            size = size,
            color = color,
            highlightColor = highlightColor
        )
    }
}

@Composable
fun ThemeSparkleIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    if (SweetTheme.isSpace) {
        CometIcon(modifier = modifier, size = size)
    } else {
        SparkleIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeHolidayHeroIcon(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    if (SweetTheme.isSpace) {
        RingPlanetIcon(modifier = modifier, size = size)
    } else {
        NavPeppermintIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeSettingsHolidayIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    if (SweetTheme.isSpace) {
        StarIcon(modifier = modifier, size = size)
    } else {
        NavPeppermintIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeSettingsDarkModeIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    if (SweetTheme.isSpace) {
        MoonIcon(modifier = modifier, size = size)
    } else {
        SettingsChocolateIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeSettingsWidgetIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    if (SweetTheme.isSpace) {
        SatelliteIcon(modifier = modifier, size = size)
    } else {
        SettingsGumdropIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeSettingsJarWidgetIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    if (SweetTheme.isSpace) {
        CometIcon(modifier = modifier, size = size)
    } else {
        SettingsChocolateIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeStatsJarIcon(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    if (SweetTheme.isSpace) {
        CometIcon(modifier = modifier, size = size)
    } else {
        StatsJarChocolateIcon(modifier = modifier, size = size)
    }
}

@Composable
fun ThemeAlarmHeroIcon(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    color: Color? = null
) {
    val colors = SweetTheme.colors
    if (SweetTheme.isSpace) {
        RocketIcon(modifier = modifier, size = size)
    } else {
        BigBellIcon(
            modifier = modifier,
            size = size,
            color = color ?: colors.pink
        )
    }
}

@Composable
fun ThemeDecorBackground(modifier: Modifier = Modifier) {
    if (SweetTheme.isSpace) {
        SpaceStarsBackground(modifier = modifier)
    } else {
        CandySprinklesBackground(modifier = modifier)
    }
}
