package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendartodo.R
import com.example.calendartodo.ui.theme.BubblegumPink
import com.example.calendartodo.ui.theme.GrapePurple
import com.example.calendartodo.ui.theme.LemonYellow
import com.example.calendartodo.ui.theme.MintDeep
import com.example.calendartodo.ui.theme.MintGreen
import com.example.calendartodo.ui.theme.PixelBorder
import com.example.calendartodo.ui.theme.PixelPurple
import com.example.calendartodo.ui.theme.PixelPurpleHighlight
import com.example.calendartodo.ui.theme.Pink
import com.example.calendartodo.ui.theme.PinkDeep
import com.example.calendartodo.ui.theme.PurpleDeep
import com.example.calendartodo.ui.theme.SprinklesBlue
import com.example.calendartodo.ui.theme.SprinklesGreen
import com.example.calendartodo.ui.theme.SprinklesRed
import com.example.calendartodo.ui.theme.SweetTheme

private val MockupChocFillLight = Color(0xFF8A5A38)
private val MockupChocSeamLight = Color(0xFF5A3A22)
private val MockupChocFillDark = Color(0xFFC99770)
private val MockupChocSeamDark = Color(0xFF8A6B4E)
private val MockupGumBodyDark = Color(0xFFC3AEEF)
private val MockupGumHiDark = Color(0xFFE4D9FB)
private val MockupIconHeartDark = Color(0xFFFF8FBB)
private val MockupIconGemDark = Color(0xFFC3AEEF)
@Composable
private fun themedCategoryHeartColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) MockupIconHeartDark else PinkDeep
}

@Composable
private fun themedCategoryGemColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) MockupIconGemDark else PurpleDeep
}

@Composable
private fun themedCategoryLeafColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) colors.mint else MintDeep
}

@Composable
private fun themedChocolateFillColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) MockupChocFillDark else MockupChocFillLight
}

@Composable
private fun themedChocolateSeamColor(): Color {
    val colors = SweetTheme.colors
    return if (colors.isDark) MockupChocSeamDark else MockupChocSeamLight
}

@Composable
private fun themedGumdropColors(): Triple<Color, Color, Color> {
    val colors = SweetTheme.colors
    return if (colors.isDark) {
        Triple(colors.lemon, MockupGumBodyDark, MockupGumHiDark)
    } else {
        Triple(LemonYellow, PixelPurple, PixelPurpleHighlight)
    }
}

@Composable
private fun PixelArtImage(
    resId: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = modifier.size(width = size, height = height ?: size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun LollipopIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    height: Dp? = null
) {
    PixelArtImage(
        resId = R.drawable.pixel_lollipop_swirl,
        size = size,
        height = height,
        modifier = modifier,
        contentDescription = "Lollipop"
    )
}

@Composable
fun WrappedCandyIcon(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_wrapped_candy,
        size = size,
        modifier = modifier,
        contentDescription = "Candy"
    )
}

@Composable
fun PinkLollipopIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_lollipop,
        size = size,
        modifier = modifier,
        contentDescription = "Lollipop"
    )
}

@Composable
fun PeppermintCandyIcon(modifier: Modifier = Modifier, size: Dp = 20.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_peppermint,
        size = size,
        modifier = modifier,
        contentDescription = "Peppermint candy"
    )
}

@Composable
fun IceCreamIcon(modifier: Modifier = Modifier, size: Dp = 120.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_ice_cream,
        size = size,
        modifier = modifier,
        contentDescription = "Ice cream"
    )
}

/** Empty state — mockup `buildIceCream(8)`. */
@Composable
fun EmptyStateIceCreamIcon(modifier: Modifier = Modifier, size: Dp = 64.dp) {
    val colors = SweetTheme.colors
    val cell = size / 8
    val scoopLight = Color(0xFFFFF3E3)
    val scoopPink = if (colors.isDark) colors.pink else Pink
    val coneColor = Color(0xFFE8A857)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelIcon(
            rows = listOf(
                "..1111..",
                ".111211.",
                "11122211",
                "11222211",
                "11122211",
                ".111111."
            ),
            palette = mapOf('1' to scoopLight, '2' to scoopPink),
            width = size,
            height = cell * 6
        )
        PixelIcon(
            rows = listOf(
                ".33333.",
                "..333..",
                "...3..."
            ),
            palette = mapOf('3' to coneColor),
            width = cell * 5,
            height = cell * 3,
            modifier = Modifier
                .offset(x = cell, y = -cell / 8)
        )
    }
}

@Composable
fun ChocolateIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    PixelArtImage(
        resId = R.drawable.pixel_chocolate,
        size = size,
        modifier = modifier,
        contentDescription = "Chocolate"
    )
}

/** Small colored marker used on the calendar grid where tinting is needed. */
@Composable
fun GummyIcon(modifier: Modifier = Modifier, size: Dp = 16.dp, color: Color = LemonYellow) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val b = s * 0.1f
        val body = Path().apply {
            moveTo(s * 0.3f, s * 0.35f)
            lineTo(s * 0.7f, s * 0.35f)
            lineTo(s * 0.75f, s * 0.65f)
            lineTo(s * 0.25f, s * 0.65f)
            close()
        }
        drawPath(body, color)
        drawPath(body, PixelBorder, style = Stroke(width = b))
        drawCircle(color, s * 0.1f, Offset(s * 0.25f, s * 0.2f))
        drawCircle(color, s * 0.1f, Offset(s * 0.75f, s * 0.2f))
    }
}

/** Work category marker — purple gem from mockup `buildGem`. */
@Composable
fun TaskGemIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            "...1...",
            "..111..",
            ".11111.",
            "1111111",
            ".11111.",
            "..111..",
            "...1..."
        ),
        palette = mapOf('1' to themedCategoryGemColor()),
        size = size,
        modifier = modifier
    )
}

/** Personal category marker — pink heart from mockup `buildHeart`. */
@Composable
fun TaskHeartIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            ".11.11.",
            "1111111",
            "1111111",
            ".11111.",
            "..111..",
            "...1..."
        ),
        palette = mapOf('1' to themedCategoryHeartColor()),
        size = size,
        modifier = modifier
    )
}

/** Home category marker — mint leaf from mockup `buildLeaf`. */
@Composable
fun TaskLeafIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    PixelIcon(
        rows = listOf(
            "..1....",
            ".111...",
            "11111..",
            ".11111.",
            "..1111.",
            "...111.",
            "....11."
        ),
        palette = mapOf('1' to themedCategoryLeafColor()),
        size = size,
        modifier = modifier
    )
}

/** Search magnifier from mockup `buildSearchIcon` (cell × 3). */
@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    color: Color = Color(0xFFB7A493)
) {
    PixelIcon(
        rows = listOf(
            ".111..",
            "1...1.",
            "1...1.",
            ".111.1",
            "....11"
        ),
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

/** Trash can — mockup `buildTrashIcon(2)`. */
@Composable
fun TrashIcon(
    modifier: Modifier = Modifier,
    width: Dp = 12.dp,
    height: Dp = 10.dp,
    color: Color = Color(0xFFC9846A)
) {
    PixelIcon(
        rows = listOf(
            ".1111.",
            "111111",
            ".1111.",
            ".1111.",
            ".1111."
        ),
        palette = mapOf('1' to color),
        width = width,
        height = height,
        modifier = modifier
    )
}

/** Error banner — mockup `buildWarnIcon(2)`. */
@Composable
fun WarnIcon(
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    color: Color = ChocBrownDark
) {
    PixelIcon(
        rows = listOf(
            "..1..",
            ".111.",
            ".1.1.",
            ".1.1.",
            ".111."
        ),
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

/** Offline state hero — mockup `buildMeltedCandy(8)`. */
@Composable
fun MeltedCandyIcon(
    modifier: Modifier = Modifier,
    width: Dp = 64.dp,
    height: Dp = 56.dp,
    color: Color? = null
) {
    val colors = SweetTheme.colors
    val candyColor = color ?: if (colors.isDark) colors.purple else PixelPurple
    PixelIcon(
        rows = listOf(
            "..1111..",
            ".111111.",
            "11111111",
            "1111.111",
            ".11..11.",
            "..1..1..",
            ".1....1."
        ),
        palette = mapOf('1' to candyColor),
        width = width,
        height = height,
        modifier = modifier
    )
}

/** Large bell from mockup `buildBigBell` (cell × 8). */
@Composable
fun BigBellIcon(modifier: Modifier = Modifier, size: Dp = 72.dp, color: Color = BubblegumPink) {
    PixelIcon(
        rows = listOf(
            "...111...",
            "..11111..",
            ".1111111.",
            ".1111111.",
            ".1111111.",
            "111111111",
            "....1....",
            "...111..."
        ),
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

/** Completed-task candy from mockup `buildCheckCandy`. */
@Composable
fun CheckCandyIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    bgColor: Color = MintGreen
) {
    PixelIcon(
        rows = listOf(
            "..mmm..",
            ".mmmmm.",
            "mmmmmmm",
            "mwmwmmm",
            "mmwmwmm",
            ".mmwmm.",
            "..mmm.."
        ),
        palette = mapOf('m' to bgColor, 'w' to Color.White),
        size = size,
        modifier = modifier
    )
}

/** High-priority sparkle from mockup `buildSparkle`. */
@Composable
fun SparkleIcon(modifier: Modifier = Modifier, size: Dp = 12.dp) {
    PixelIcon(
        rows = listOf(
            "..y..",
            "..y..",
            "yyYyy",
            "..y..",
            "..y.."
        ),
        palette = mapOf('y' to Color(0xFFE8B32A), 'Y' to LemonYellow),
        size = size,
        modifier = modifier
    )
}

/** Mini calendar from mockup `buildCalIcon` (cell × 2). */
@Composable
fun CalMiniIcon(modifier: Modifier = Modifier, size: Dp = 12.dp, color: Color = GrapePurple) {
    PixelIcon(
        rows = listOf(
            "111111",
            "1.1.1.",
            "111111",
            "1....1",
            "1.1.1.",
            "111111"
        ),
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

/** Mini clock from mockup `buildClockIcon` (cell × 2). */
@Composable
fun ClockMiniIcon(modifier: Modifier = Modifier, size: Dp = 12.dp, color: Color = GrapePurple) {
    PixelIcon(
        rows = listOf(
            ".1111.",
            "1....1",
            "1.11.1",
            "1..1.1",
            "1....1",
            ".1111."
        ),
        palette = mapOf('1' to color),
        size = size,
        modifier = modifier
    )
}

/** FAB gumdrop — pink candy with white shine on the purple button face. */
@Composable
fun FabGumdropIcon(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    val colors = SweetTheme.colors
    SettingsGumdropIcon(
        modifier = modifier,
        size = size,
        wrapColor = colors.pinkDeep,
        bodyColor = colors.pink,
        highlightColor = Color.White
    )
}

/** Settings row / small UI — mockup `buildGumdrop(2)`. */
@Composable
fun SettingsGumdropIcon(
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
    wrapColor: Color? = null,
    bodyColor: Color? = null,
    highlightColor: Color? = null
) {
    val themed = themedGumdropColors()
    PixelIcon(
        rows = listOf(
            ".yppppppy.",
            "yyppppppyy",
            "yyphhhhpyy",
            "yyphhhhpyy",
            "yyppppppyy",
            ".yppppppy."
        ),
        palette = mapOf(
            'y' to (wrapColor ?: themed.first),
            'p' to (bodyColor ?: themed.second),
            'h' to (highlightColor ?: themed.third)
        ),
        width = size,
        height = size,
        modifier = modifier
    )
}

private val ChocBrown = Color(0xFF8A5A38)
private val ChocBrownDark = Color(0xFF5A3A22)

/** Mockup `buildChocolate(cell)` — 8×8 chocolate square. */
@Composable
fun MockupChocolateIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    fillColor: Color = ChocBrown,
    seamColor: Color = ChocBrownDark
) {
    PixelIcon(
        rows = chocolatePixelRows(),
        palette = mapOf('o' to seamColor, 'd' to seamColor, 'b' to fillColor),
        size = size,
        modifier = modifier
    )
}

/** Settings row — mockup `buildChocolate(2)`. */
@Composable
fun SettingsChocolateIcon(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    MockupChocolateIcon(
        modifier = modifier,
        size = size,
        fillColor = themedChocolateFillColor(),
        seamColor = themedChocolateSeamColor()
    )
}

/** Stats jar — mockup `buildChocolate(5)`. */
@Composable
fun StatsJarChocolateIcon(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    MockupChocolateIcon(modifier = modifier, size = size)
}

/** Settings row — mockup `buildBoxUnchecked(2)`. */
@Composable
fun SettingsBoxUncheckedIcon(modifier: Modifier = Modifier, size: Dp = 10.dp) {
    val colors = SweetTheme.colors
    PixelIcon(
        rows = listOf(
            "11111",
            "1...1",
            "1...1",
            "1...1",
            "11111"
        ),
        palette = mapOf('1' to colors.purpleDeep),
        size = size,
        modifier = modifier
    )
}

/** Profile avatar — mockup `buildLollipop(3)`. */
@Composable
fun ProfileLollipopIcon(modifier: Modifier = Modifier, size: Dp = 24.dp) {
    val colors = SweetTheme.colors
    NavLollipopIcon(
        modifier = modifier,
        size = size,
        headColor = if (colors.isDark) colors.pink else BubblegumPink,
        highlightColor = Color.White,
        stickColor = LollipopTan
    )
}

private fun chocolatePixelRows(): List<String> =
    List(8) { row ->
        List(8) { col ->
            when {
                row == 0 || row == 7 || col == 0 || col == 7 -> 'o'
                row == 3 || row == 4 || col == 3 || col == 4 -> 'd'
                else -> 'b'
            }
        }.joinToString("")
    }

private val LollipopTan = Color(0xFFD4A574)

/** Bottom-nav Today tab — mockup `buildLollipop(2)`. */
@Composable
fun NavLollipopIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    headColor: Color = PurpleDeep,
    highlightColor: Color = PixelPurpleHighlight,
    stickColor: Color = PixelPurple
) {
    val headPalette = mapOf('a' to headColor, 'b' to highlightColor)
    val stickPalette = mapOf('t' to stickColor)
    val stickHeight = size * 0.625f
    Column(
        modifier = modifier.width(size),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelIcon(
            rows = listOf(
                "..aaaa..",
                ".aabaaa.",
                "aabaabaa",
                "aabaabaa",
                "aabaabaa",
                "aabaabaa",
                ".aabaaa.",
                "..aaaa.."
            ),
            palette = headPalette,
            width = size,
            height = size
        )
        PixelIcon(
            rows = listOf(
                "...tt...",
                "...tt...",
                "...tt...",
                "...tt...",
                "...tt..."
            ),
            palette = stickPalette,
            width = size,
            height = stickHeight,
            modifier = Modifier.offset(y = -size / 16)
        )
    }
}

/** Bottom-nav Week tab — mockup `buildPeppermint(2)`. */
@Composable
fun NavPeppermintIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    color: Color = SprinklesRed,
    highlightColor: Color = Color.White
) {
    PixelIcon(
        rows = peppermintPixelRows(),
        palette = mapOf('r' to color, 'w' to highlightColor),
        width = size,
        height = size,
        modifier = modifier
    )
}

/** Bottom-nav Month tab — mockup `buildGridIcon(3)`. */
@Composable
fun NavMonthGridIcon(
    modifier: Modifier = Modifier,
    size: Dp = 15.dp,
    color: Color = PurpleDeep
) {
    PixelIcon(
        rows = listOf(
            "1.1.1",
            ".....",
            "1.1.1",
            ".....",
            "1.1.1"
        ),
        palette = mapOf('1' to color),
        width = size,
        height = size,
        modifier = modifier
    )
}

/** Bottom-nav Settings tab — mockup `buildGearIcon(3)`. */
@Composable
fun NavSettingsGearIcon(
    modifier: Modifier = Modifier,
    size: Dp = 15.dp,
    color: Color = PurpleDeep
) {
    PixelIcon(
        rows = listOf(
            ".1.1.",
            "11111",
            "1...1",
            "11111",
            ".1.1."
        ),
        palette = mapOf('1' to color),
        width = size,
        height = size,
        modifier = modifier
    )
}

private fun peppermintPixelRows(): List<String> {
    val mask = listOf(
        "..1111..",
        ".111111.",
        "11111111",
        "11111111",
        "11111111",
        "11111111",
        ".111111.",
        "..1111.."
    )
    return mask.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, ch ->
            when {
                ch != '1' -> '.'
                (rowIndex + colIndex) % 2 == 0 -> 'r'
                else -> 'w'
            }
        }.joinToString("")
    }
}

@Composable
fun CandySprinklesBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val sprinkleColors = listOf(SprinklesRed, SprinklesBlue, SprinklesGreen, LemonYellow, BubblegumPink)
        val step = 28.dp.toPx()
        var row = 0
        var y = 0f
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else step / 2
            var col = 0
            while (x < size.width) {
                val color = sprinkleColors[(row + col) % sprinkleColors.size]
                if ((row + col) % 3 != 0) {
                    drawRect(color, Offset(x, y), Size(4f, 4f))
                }
                x += step
                col++
            }
            y += step
            row++
        }
    }
}
