package com.example.calendartodo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calendartodo.ui.theme.SweetTheme

data class SpaceIconPalette(
    val rocketBody: Color,
    val rocketWindow: Color,
    val rocketFin: Color,
    val rocketFlame: Color,
    val planetBody: Color,
    val planetRing: Color,
    val star: Color,
    val cometHead: Color,
    val cometTail: Color,
    val moon: Color,
    val astroBg: Color,
    val satBody: Color,
    val satPanel: Color,
    val navColor: Color,
)

@Composable
fun spaceNavIconPalette(
    primary: Color,
    highlight: Color,
    secondary: Color,
): SpaceIconPalette {
    return SpaceIconPalette(
        rocketBody = primary,
        rocketWindow = highlight,
        rocketFin = secondary,
        rocketFlame = secondary,
        planetBody = primary,
        planetRing = secondary,
        star = primary,
        cometHead = primary,
        cometTail = secondary,
        moon = primary,
        astroBg = primary,
        satBody = primary,
        satPanel = secondary,
        navColor = primary,
    )
}

@Composable
fun spaceIconPalette(): SpaceIconPalette {
    val colors = SweetTheme.colors
    return if (colors.isDark) {
        SpaceIconPalette(
            rocketBody = Color(0xFFECEFF7),
            rocketWindow = Color(0xFF5FE6E0),
            rocketFin = Color(0xFFFF6AC8),
            rocketFlame = Color(0xFFFFD466),
            planetBody = Color(0xFF33C8C2),
            planetRing = Color(0xFFFFD466),
            star = Color(0xFFFF6AC8),
            cometHead = Color(0xFFECEFF7),
            cometTail = Color(0xFFFFD466),
            moon = Color(0xFF8B6FEF),
            astroBg = Color(0xFF33C8C2),
            satBody = Color(0xFFECEFF7),
            satPanel = Color(0xFFFFD466),
            navColor = Color(0xFFC9B8FF),
        )
    } else {
        SpaceIconPalette(
            rocketBody = Color(0xFFC9BFF2),
            rocketWindow = Color(0xFF1F9E98),
            rocketFin = Color(0xFFD63F9E),
            rocketFlame = Color(0xFFDE9F14),
            planetBody = Color(0xFF1F9E98),
            planetRing = Color(0xFFDE9F14),
            star = Color(0xFFD63F9E),
            cometHead = Color(0xFF6B4FD6),
            cometTail = Color(0xFFDE9F14),
            moon = Color(0xFF6B4FD6),
            astroBg = Color(0xFF1F9E98),
            satBody = Color(0xFF6B4FD6),
            satPanel = Color(0xFFDE9F14),
            navColor = Color(0xFF6B4FD6),
        )
    }
}

private val RocketRows = listOf(
    "....1....",
    "...111...",
    "..11111..",
    "..1ooo1..",
    "..1ooo1..",
    "..11111..",
    "..11111..",
    ".2.111.2.",
    "22..1..22",
    "...fff...",
    "....f....",
)

private val RingPlanetRows = listOf(
    "...pppp...",
    "..pppppp..",
    ".pppppppp.",
    "rrrrrrrrrr",
    "rrrrrrrrrr",
    ".pppppppp.",
    "..pppppp..",
    "...pppp...",
)

private val StarRows = listOf(
    "...1...",
    "...1...",
    "1111111",
    "...1...",
    "...1...",
)

private val CometRows = listOf(
    "..11....",
    ".1111...",
    "11111f..",
    ".111ff..",
    "..11fff.",
    "...1fff.",
)

private val SatelliteRows = listOf(
    "....1....",
    "pp.111.pp",
    "pp.111.pp",
)

private val AstroCheckRows = listOf(
    "..mmm..",
    ".mmmmm.",
    "mmmmmmm",
    "mwmwmmm",
    "mmwmwmm",
    ".mmwmm.",
    "..mmm..",
)

private val MoonRows = listOf(
    "..111.",
    ".11111",
    "111111",
    "111111",
    ".11111",
    "..111.",
)

@Composable
fun RocketIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = RocketRows,
        palette = mapOf(
            '1' to palette.rocketBody,
            'o' to palette.rocketWindow,
            '2' to palette.rocketFin,
            'f' to palette.rocketFlame,
        ),
        size = size,
        modifier = modifier
    )
}

@Composable
fun RingPlanetIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = RingPlanetRows,
        palette = mapOf(
            'p' to palette.planetBody,
            'r' to palette.planetRing,
        ),
        size = size,
        modifier = modifier
    )
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = StarRows,
        palette = mapOf('1' to palette.star),
        size = size,
        modifier = modifier
    )
}

@Composable
fun CometIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = CometRows,
        palette = mapOf(
            '1' to palette.cometHead,
            'f' to palette.cometTail,
        ),
        size = size,
        modifier = modifier
    )
}

@Composable
fun SatelliteIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = SatelliteRows,
        palette = mapOf(
            '1' to palette.satBody,
            'p' to palette.satPanel,
        ),
        size = size,
        modifier = modifier
    )
}

@Composable
fun AstroCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    bgColor: Color? = null,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    val colors = SweetTheme.colors
    val fill = bgColor ?: palette.astroBg
    val check = if (colors.isDark) colors.ink else Color(0xFFEDEBFF)
    PixelIcon(
        rows = AstroCheckRows,
        palette = mapOf('m' to fill, 'w' to check),
        size = size,
        modifier = modifier
    )
}

@Composable
fun MoonIcon(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    palette: SpaceIconPalette = spaceIconPalette()
) {
    PixelIcon(
        rows = MoonRows,
        palette = mapOf('1' to palette.moon),
        size = size,
        modifier = modifier
    )
}

@Composable
fun SpaceStarsBackground(modifier: Modifier = Modifier) {
    val colors = SweetTheme.colors
    val starColors = listOf(colors.pink, colors.purple, colors.mint, colors.lemon, colors.purpleDeep)
    Canvas(modifier) {
        val step = 32.dp.toPx()
        var row = 0
        var y = 0f
        while (y < size.height) {
            var x = if (row % 2 == 0) 0f else step / 2
            var col = 0
            while (x < size.width) {
                if ((row + col) % 3 != 0) {
                    val color = starColors[(row + col) % starColors.size].copy(alpha = 0.55f)
                    val cell = 3f
                    val cx = x + step / 2f
                    val cy = y + step / 2f
                    drawRect(color, Offset(cx - cell / 2, cy - cell), Size(cell, cell))
                    drawRect(color, Offset(cx - cell, cy - cell / 2), Size(cell, cell))
                    drawRect(color, Offset(cx - cell / 2, cy), Size(cell, cell))
                    drawRect(color, Offset(cx, cy - cell / 2), Size(cell, cell))
                    drawRect(color, Offset(cx - cell / 2, cy - cell / 2), Size(cell, cell))
                }
                x += step
                col++
            }
            y += step
            row++
        }
    }
}
