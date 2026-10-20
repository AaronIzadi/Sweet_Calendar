package com.sweetcalendar.ui.theme

/**
 * Raw pixel values from [sweet_calendar_mockups.html] (360px phone frame).
 * Use with [mockupDp] / [mockupSp] inside [ProvideMockupScale].
 *
 * Type scale: body copy ≥ 12sp; 10–11sp only for dense chrome (nav, weekday headers, pixel buttons).
 */
object MockupDimens {
    // Welcome — icon grid uses `cell` param in mockup JS (×8 hero, ×4 deco, ×2 chips)
    const val HERO_LOLLIPOP_W = 64
    const val HERO_LOLLIPOP_H = 104
    const val DECO_ICON = 32
    const val DECO_ICON_SMALL = 24
    const val FEAT_CHIP_ICON = 16
    const val WELCOME_TITLE = 18
    const val WELCOME_TAG = 14
    const val FEAT_CHIP_TEXT = 12
    const val FEAT_CHIP_TEXT_F = 12f
    const val SKIP_LINK = 12
    const val PAGE_DOT = 7

    // Today
    const val GREET_TITLE = 18
    const val GREET_DATE = 12
    const val GREET_DATE_F = 12.5f
    const val STREAK_FONT = 11
    const val STREAK_ICON = 16
    const val JAR_LABEL = 12
    const val JAR_TRACK_H = 14
    const val SECTION_LABEL = 11
    const val SECTION_LABEL_F = 12f
    const val TASK_TITLE = 13
    const val TASK_TITLE_F = 13.5f
    const val TASK_META = 12
    const val TASK_META_F = 12f
    const val TASK_ICON = 28
    const val TASK_ACCENT_W = 6
    const val TASK_CARD_PAD_H = 12
    const val TASK_CARD_PAD_V = 10
    const val SPARKLE_ICON = 15
    const val COMPLETED_ROW_ICON = 14
    const val COMPLETED_LABEL = 12
    const val COMPLETED_LABEL_F = 12f

    // Empty state
    const val EMPTY_ICON = 64
    const val EMPTY_TITLE = 16
    const val EMPTY_SUB = 13

    // Week
    const val WEEK_TITLE = 17
    const val WEEK_RANGE = 12
    const val WEEK_DAY_LABEL = 11
    const val DAY_CHIP_MIN_W = 44
    const val DAY_CHIP_DOW = 10
    const val DAY_CHIP_NUM = 15
    const val DAY_CHIP_PIP = 5

    // Month
    const val MONTH_TITLE = 16
    const val MONTH_SUB = 12
    const val WEEKDAY_HEADER = 11
    const val WEEKDAY_HEADER_F = 11f
    const val MONTH_DAY_NUM = 12
    const val MONTH_DAY_NUM_F = 12.5f
    const val MONTH_CELL_DOT = 4
    const val MONTH_LEGEND_DOT = 9
    const val MONTH_LEGEND_TEXT = 11
    const val MONTH_LEGEND_TEXT_F = 12f
    const val CHEVRON_SIZE = 26
    const val CHEVRON_FONT = 14

    // Chrome
    const val PIXEL_BTN = 11
    const val PIXEL_BTN_PAD_V = 14
    const val PIXEL_BTN_PAD_H = 20
    const val FAB_SIZE = 54
    const val FAB_ICON = 30
    const val NAV_ICON = 16
    const val NAV_ICON_LARGE = 15
    const val NAV_ICON_SLOT = 18
    /** Fits `buildLollipop(2)` head + stick with −1px overlap (25px mockup). */
    const val NAV_ICON_SLOT_H = 26
    const val NAV_LABEL_F = 10f
    const val PRIORITY_SPARKLE_SLOT = 10
    const val PRIORITY_CHIP_PAD_V = 10
    const val PRIORITY_CHIP_PAD_H = 4
    const val PRIORITY_CHIP_GAP = 4
    const val PRIORITY_CHIP_LINE = 14
    /** pad×2 + sparkle + gap + label line — keeps row height fixed without empty sparkle slots on Low/High. */
    const val PRIORITY_CHIP_MIN_H = 48

    // Add / Edit task
    const val SHEET_HEADER_BTN = 12
    const val SHEET_HEADER_BTN_LINE = 14f
    const val SHEET_HEADER_ICON = 11
    const val SHEET_TITLE = 17
    const val FIELD_LABEL = 12
    const val FIELD_TEXT = 14
    const val FIELD_TEXT_F = 14f
    const val SWATCH_TEXT_F = 12f
    const val SWATCH_ICON = 21
    const val PRIORITY_CHIP_TEXT = 12
    const val SWITCH_LABEL_F = 13.5f
    const val BIG_SAVE_BTN = 12
    const val MINI_FIELD_ICON = 12
    const val FORM_FIELD_RADIUS = 12
    const val FORM_FIELD_SHADOW = 2
    const val BIG_SAVE_RADIUS = 14
    const val BIG_SAVE_SHADOW = 4

    // Task detail
    const val DETAIL_ICON_BTN = 30
    const val DETAIL_ICON_BTN_RADIUS = 9
    const val DETAIL_ICON_BTN_FONT = 14
    const val DETAIL_BADGE_TEXT_F = 12f
    const val DETAIL_BADGE_RADIUS = 10
    const val DETAIL_BADGE_ICON = 14
    const val DETAIL_TITLE = 20
    const val DETAIL_META_F = 13.5f
    const val DETAIL_NOTES_F = 13.5f
    const val DETAIL_DELETE = 12
    const val COMPLETE_BTN_F = 12f
    const val COMPLETE_BTN_PAD_V = 15
    const val COMPLETE_BTN_RADIUS = 14
    const val COMPLETE_BTN_SHADOW = 4

    // Day detail
    const val DAY_DETAIL_TITLE = 17
    const val DAY_DETAIL_SUB_F = 12f
    const val DAY_DETAIL_HEADER_GAP = 12
    const val HOLIDAY_EVENT_ICON = 24
    const val HOLIDAY_HERO_H = 170
    const val HOLIDAY_HERO_ICON = 72
    const val HOLIDAY_TAG_F = 11f
    const val HOLIDAY_TITLE = 19
    const val HOLIDAY_DATE_F = 13f
    const val HOLIDAY_BODY_F = 13.5f
    const val HOLIDAY_SOURCE_F = 12f
    const val HOLIDAY_SOURCE_ICON = 16

    // Settings
    const val SETTINGS_TITLE = 19
    const val SETTINGS_GROUP_LABEL = 11
    const val SETTINGS_ROW_LABEL_F = 13.5f
    const val SETTINGS_ROW_VALUE_F = 12f
    const val SETTINGS_ROW_GAP = 12
    const val SETTINGS_ROW_PAD_H = 14
    const val SETTINGS_ROW_PAD_V = 12
    const val SETTINGS_ROW_RADIUS = 12
    const val SETTINGS_ROW_SHADOW = 2
    const val SETTINGS_ROW_ICON = 16
    const val SETTINGS_PROFILE_AVATAR = 44
    const val SETTINGS_PROFILE_RADIUS = 12
    const val SETTINGS_PROFILE_NAME_F = 14.5f
    const val SETTINGS_PROFILE_SUB_F = 12f
    const val SETTINGS_PROFILE_ICON = 24
    const val SETTINGS_PROFILE_EDIT_ICON = 14
    const val SETTINGS_CHEVRON_F = 14f

    // Archive / Completed
    const val ARCHIVE_TITLE = 17
    const val ARCHIVE_STAT_NUM = 19
    const val ARCHIVE_STAT_LBL = 11f
    const val ARCHIVE_STAT_RADIUS = 14
    const val ARCHIVE_STAT_SHADOW = 2
    const val ARCHIVE_FILTER_TEXT = 12f
    const val ARCHIVE_FILTER_RADIUS = 9
    const val ARCHIVE_FILTER_SHADOW = 2
    const val ARCHIVE_DAY_LABEL = 11

    // Reminders / notification permission
    const val PERMISSION_BELL = 72
    const val PERMISSION_TITLE = 17
    const val PERMISSION_SUB = 13
    const val PERMISSION_SECONDARY = 13
    const val PERMISSION_PAD_H = 30
    const val PERMISSION_PAD_BOTTOM = 40

    // Search
    const val SEARCH_BAR_RADIUS = 12
    const val SEARCH_BAR_SHADOW = 2
    const val SEARCH_BAR_PAD_H = 14
    const val SEARCH_BAR_PAD_V = 11
    const val SEARCH_TEXT = 14
    const val SEARCH_ICON = 18
    const val SEARCH_GROUP_LABEL = 11
    const val SEARCH_CHIP_TEXT = 12f
    const val SEARCH_CHIP_RADIUS = 9

    // Stats
    const val STATS_TITLE = 19
    const val STATS_SUB = 12
    const val STATS_JAR_ICON = 40
    const val STATS_BIG_NUM = 30
    const val STATS_BIG_LBL = 12
    const val STATS_BIG_CARD_RADIUS = 18
    const val STATS_BIG_CARD_SHADOW = 3
    const val STATS_JAR_CARD_RADIUS = 16
    const val STATS_JAR_CARD_SHADOW = 3
    const val STATS_BAR_HEIGHT = 90
    const val STATS_BAR_DAY = 11
    const val STATS_BAR_RADIUS = 4
    const val STATS_HEAT_RADIUS = 4
    const val STATS_HEAT_GAP = 4

    // Offline / error
    const val OFFLINE_BANNER_RADIUS = 12
    const val OFFLINE_BANNER_TEXT = 12
    const val OFFLINE_WARN_ICON = 10
    const val OFFLINE_ICON_W = 64
    const val OFFLINE_ICON_H = 56
    const val OFFLINE_TITLE = 16
    const val OFFLINE_SUB = 13
    const val OFFLINE_PAD_H = 32
    const val OFFLINE_PAD_BOTTOM = 60

    // Recover snackbar
    const val SNACKBAR_RADIUS = 12
    const val SNACKBAR_MSG_F = 12.5f
    const val SNACKBAR_RECOVER = 11
    const val SNACKBAR_PAD_H = 14
    const val SNACKBAR_PAD_V = 12
    const val SNACKBAR_EDGE = 14
    const val SNACKBAR_BOTTOM = 14
    const val TRASH_BTN = 26
    const val TRASH_BTN_RADIUS = 8
    const val TRASH_ICON_W = 12
    const val TRASH_ICON_H = 10
    const val DEMO_HINT_F = 12f
    const val SNACKBAR_SCROLL_PAD_BOTTOM = 130
}
