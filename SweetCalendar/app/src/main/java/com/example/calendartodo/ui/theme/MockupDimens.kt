package com.example.calendartodo.ui.theme

/**
 * Raw pixel values from [sweet_calendar_mockups.html] (360px phone frame).
 * Use with [mockupDp] / [mockupSp] inside [ProvideMockupScale].
 */
object MockupDimens {
    // Welcome — icon grid uses `cell` param in mockup JS (×8 hero, ×4 deco, ×2 chips)
    const val HERO_LOLLIPOP_W = 64
    const val HERO_LOLLIPOP_H = 104
    const val DECO_ICON = 32
    const val DECO_ICON_SMALL = 24
    const val FEAT_CHIP_ICON = 16
    const val WELCOME_TITLE = 17
    const val WELCOME_TAG = 13
    const val FEAT_CHIP_TEXT = 10
    const val FEAT_CHIP_TEXT_F = 10.5f
    const val SKIP_LINK = 11
    const val PAGE_DOT = 7

    // Today
    const val GREET_TITLE = 17
    const val GREET_DATE = 11
    const val GREET_DATE_F = 11.5f
    const val STREAK_FONT = 10
    const val STREAK_ICON = 16
    const val JAR_LABEL = 11
    const val JAR_TRACK_H = 14
    const val SECTION_LABEL = 9
    const val SECTION_LABEL_F = 9.5f
    const val TASK_TITLE = 12
    const val TASK_TITLE_F = 12.5f
    const val TASK_META = 9
    const val TASK_META_F = 9.5f
    const val TASK_ICON = 28
    const val TASK_ACCENT_W = 6
    const val TASK_CARD_PAD_H = 12
    const val TASK_CARD_PAD_V = 10
    const val SPARKLE_ICON = 15
    const val COMPLETED_ROW_ICON = 14
    const val COMPLETED_LABEL = 10
    const val COMPLETED_LABEL_F = 10.5f

    // Week
    const val WEEK_TITLE = 16
    const val WEEK_RANGE = 11
    const val WEEK_DAY_LABEL = 9
    const val DAY_CHIP_MIN_W = 44
    const val DAY_CHIP_DOW = 8
    const val DAY_CHIP_NUM = 14
    const val DAY_CHIP_PIP = 5

    // Month
    const val MONTH_TITLE = 15
    const val MONTH_SUB = 10
    const val WEEKDAY_HEADER = 8
    const val WEEKDAY_HEADER_F = 8.5f
    const val MONTH_DAY_NUM = 10
    const val MONTH_DAY_NUM_F = 10.5f
    const val MONTH_CELL_DOT = 4
    const val MONTH_LEGEND_DOT = 9
    const val MONTH_LEGEND_TEXT = 9
    const val MONTH_LEGEND_TEXT_F = 9.5f
    const val CHEVRON_SIZE = 26
    const val CHEVRON_FONT = 13

    // Chrome
    const val PIXEL_BTN = 9
    const val PIXEL_BTN_PAD_V = 14
    const val PIXEL_BTN_PAD_H = 20
    const val FAB_SIZE = 54
    const val FAB_ICON = 30
    const val NAV_ICON = 16
    const val NAV_ICON_LARGE = 15
    const val NAV_ICON_SLOT = 18
    const val NAV_LABEL_F = 6.5f
    const val PRIORITY_SPARKLE_SLOT = 10
    const val PRIORITY_CHIP_PAD_V = 10
    const val PRIORITY_CHIP_PAD_H = 4
    const val PRIORITY_CHIP_GAP = 4
    const val PRIORITY_CHIP_LINE = 14
    /** pad×2 + sparkle + gap + label line — keeps row height fixed without empty sparkle slots on Low/High. */
    const val PRIORITY_CHIP_MIN_H = 48

    // Add / Edit task
    const val SHEET_HEADER_BTN = 10
    const val SHEET_TITLE = 16
    const val FIELD_LABEL = 10
    const val FIELD_TEXT = 13
    const val FIELD_TEXT_F = 13f
    const val SWATCH_TEXT_F = 9.5f
    const val SWATCH_ICON = 21
    const val PRIORITY_CHIP_TEXT = 10
    const val SWITCH_LABEL_F = 12.5f
    const val BIG_SAVE_BTN = 11
    const val MINI_FIELD_ICON = 12
    const val FORM_FIELD_RADIUS = 12
    const val FORM_FIELD_SHADOW = 2
    const val BIG_SAVE_RADIUS = 14
    const val BIG_SAVE_SHADOW = 4

    // Task detail
    const val DETAIL_ICON_BTN = 30
    const val DETAIL_ICON_BTN_RADIUS = 9
    const val DETAIL_ICON_BTN_FONT = 13
    const val DETAIL_BADGE_TEXT_F = 9.5f
    const val DETAIL_BADGE_RADIUS = 10
    const val DETAIL_BADGE_ICON = 14
    const val DETAIL_TITLE = 19
    const val DETAIL_META_F = 12.5f
    const val DETAIL_NOTES_F = 12.5f
    const val DETAIL_DELETE = 11
    const val COMPLETE_BTN_F = 10.5f
    const val COMPLETE_BTN_PAD_V = 15
    const val COMPLETE_BTN_RADIUS = 14
    const val COMPLETE_BTN_SHADOW = 4

    // Day detail
    const val DAY_DETAIL_TITLE = 16
    const val DAY_DETAIL_SUB_F = 11f
    const val DAY_DETAIL_HEADER_GAP = 12
    const val HOLIDAY_EVENT_ICON = 24

    // Settings
    const val SETTINGS_TITLE = 18
    const val SETTINGS_GROUP_LABEL = 8
    const val SETTINGS_ROW_LABEL_F = 12.5f
    const val SETTINGS_ROW_VALUE_F = 11f
    const val SETTINGS_ROW_GAP = 12
    const val SETTINGS_ROW_PAD_H = 14
    const val SETTINGS_ROW_PAD_V = 12
    const val SETTINGS_ROW_RADIUS = 12
    const val SETTINGS_ROW_SHADOW = 2
    const val SETTINGS_ROW_ICON = 16
    const val SETTINGS_PROFILE_AVATAR = 44
    const val SETTINGS_PROFILE_RADIUS = 12
    const val SETTINGS_PROFILE_NAME_F = 13.5f
    const val SETTINGS_PROFILE_SUB_F = 10f
    const val SETTINGS_PROFILE_ICON = 24
    const val SETTINGS_CHEVRON_F = 13f
}
