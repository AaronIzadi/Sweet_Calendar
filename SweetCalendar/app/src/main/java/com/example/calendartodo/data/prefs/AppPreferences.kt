package com.example.calendartodo.data.prefs

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var hasSeenWelcome: Boolean
        get() = prefs.getBoolean(KEY_WELCOME, false)
        set(value) = prefs.edit().putBoolean(KEY_WELCOME, value).apply()

    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Friend") ?: "Friend"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var hasSeenNotificationRationale: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_RATIONALE, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_RATIONALE, value).apply()

    var showHolidays: Boolean
        get() = prefs.getBoolean(KEY_SHOW_HOLIDAYS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_HOLIDAYS, value).apply()

    /** 0 = Saturday … 6 = Friday (matches [JalaliDate.weekdayIndex]). */
    var weekStartsOn: Int
        get() = prefs.getInt(KEY_WEEK_STARTS_ON, 0).coerceIn(0, 6)
        set(value) = prefs.edit().putInt(KEY_WEEK_STARTS_ON, value.coerceIn(0, 6)).apply()

    var recentSearches: List<String>
        get() = prefs.getString(KEY_RECENT_SEARCHES, "")
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        private set(value) {
            prefs.edit().putString(KEY_RECENT_SEARCHES, value.joinToString("|")).apply()
        }

    fun addRecentSearch(term: String) {
        val trimmed = term.trim()
        if (trimmed.isBlank()) return
        val updated = (listOf(trimmed) + recentSearches.filter { it != trimmed }).take(5)
        recentSearches = updated
    }

    companion object {
        private const val PREFS_NAME = "sweet_calendar_prefs"
        private const val KEY_WELCOME = "has_seen_welcome"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_NOTIF_RATIONALE = "has_seen_notification_rationale"
        private const val KEY_SHOW_HOLIDAYS = "show_holidays"
        private const val KEY_WEEK_STARTS_ON = "week_starts_on"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
    }
}
