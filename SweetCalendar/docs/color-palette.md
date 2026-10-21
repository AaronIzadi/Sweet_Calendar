# Sweet Calendar color palette

Tokens live in `ui/theme/Color.kt` and are wired through `SweetColors` in `Theme.kt`.

## Layer model (especially dark mode)

| Role | Light candy | Dark candy | Use |
|------|-------------|------------|-----|
| `cream` | `#FFF6EA` | `#241A30` | App canvas (deepest) |
| `paper` | `#FFFDF9` | `#2F2340` | Cards, fields, chip faces |
| `surfaceElevated` | same as paper | `#3A2C4C` | Raised groups, recent chips |
| `shadow` | `#EAD9C4` (line tan) | `#1E1628` | Pixel offset under `paper` |
| `shadowDeep` | `#4A2C18` | `#120C1A` | FAB / primary button drop |
| `line` | `#EAD9C4` | `#40314F` | Borders only (not drop shadow) |
| `bannerBg` | `#FFF1DC` | `#3A2A22` | Streak bar, offline banner |
| `surfaceInset` | `#FBEDE3` | `#3A2A22` | Completed-task trays |

Accent colors (`pink`, `purple`, `mint`, …) stay bright in dark mode; depth comes from neutrals.

## Ambient washes (`AmbientColors.kt`)

Full-screen or hero areas that used gradients now use **fixed** mid-tone solids (candy vs space) that do **not** change with light/dark mode — e.g. welcome, notification permission, holiday hero, jar fill, settings profile card.

## Space theme

Same roles with `Space*` / `SpaceDark*` values. Dark shadows: `#0B0E24` / `#060812`.

## Semantic accents

| Token | Role |
|-------|------|
| `purple` / `purpleDeep` | Primary CTAs (`SweetPixelButton`, `SweetBigSaveButton`), dismiss actions in sheets (`CLOSE`) |
| `confirmAccent` | Affirmative text actions (`SAVE`, `OK`, check icon) — mint tone, not pink-red |
| `pinkText` | Focus rings, cursors, category personal, decorative pink labels |

## In UI code

- Pixel stack: `Box` with `offset(y)` + `colors.shadow`, then face with `colors.paper`.
- Dividers and outlines: `colors.line`.
- Do not use `colors.line` for drop shadows in dark mode.
- Legacy pixel helpers (`PixelPanel`, `PixelButton`, `themePixelBorder`) read `SweetTheme.colors` (`choc`, `shadowDeep`, `paper`).
- Material snackbars: light `chocDeep`, dark `bannerBg` (same as `SweetRecoverSnackbar`).

## Home screen widgets

`WidgetTheme.colors(context)` calls `resolveSweetColors(themeFamily, darkMode)` and exposes ARGB fields. `WidgetUi` sets card and add-button backgrounds at runtime; checkbox/progress drawables stay as themed XML fallbacks aligned to `line` / accent colors.
