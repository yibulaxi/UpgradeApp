package ru.get.better.util

import android.content.Context
import android.content.SharedPreferences
import ru.get.better.App

class Preferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        if (context !is App) {
            throw IllegalArgumentException("Expected application context")
        }
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    var uid: String?
        set(value) = sharedPreferences.edit().putString(USER_ID, value).apply()
        get() = sharedPreferences.getString(USER_ID, null)

    var locale: String
        set(value) = sharedPreferences.edit().putString(LOCALE, value).apply()
        get() = sharedPreferences.getString(LOCALE, "ru") ?: "ru"

    var pushToken: String?
        set(value) = sharedPreferences.edit().putString(PUSH_TOKEN, value).apply()
        get() = sharedPreferences.getString(PUSH_TOKEN, null)

    var isDarkTheme: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_DARK_THEME, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_DARK_THEME, true)

    var isPushAvailable: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_PUSH_AVAILABLE, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_PUSH_AVAILABLE, true)

    var isInterestsInitialized: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_INTERESTS_INITIALIZED, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_INTERESTS_INITIALIZED, false)

    var isFirstLaunch: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)

    var isDiaryHabitsSpotlightShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean("isDiaryHabitsSpotlightShown", value)
            .apply()
        get() = sharedPreferences.getBoolean("isDiaryHabitsSpotlightShown", false)

    var isMainAddPostSpotlightShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean("isMainAddPostSpotlightShown", value)
            .apply()
        get() = sharedPreferences.getBoolean("isMainAddPostSpotlightShown", false)

    var isMetricWheelSpotlightShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean("isMetricWheelSpotlightShown", value)
            .apply()
        get() = sharedPreferences.getBoolean("isMetricWheelSpotlightShown", false)

    var launchCount: Int
        set(value) = sharedPreferences.edit().putInt(LAUNCH_COUNT, value).apply()
        get() = sharedPreferences.getInt(LAUNCH_COUNT, 0)

    var firstLaunchDate: Long
        set(value) = sharedPreferences.edit().putLong(FIRST_LAUNCH_DATE, value).apply()
        get() = sharedPreferences.getLong(FIRST_LAUNCH_DATE, 0L)

    var isAppRated: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_APP_RATED, value).apply()
        get() = sharedPreferences.getBoolean(IS_APP_RATED, false)

    var isRateAppLater: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_RATE_APP_LATER, value).apply()
        get() = sharedPreferences.getBoolean(IS_RATE_APP_LATER, false)

    var currentAffirmationNumber: Int
        set(value) = sharedPreferences.edit().putInt(CURRENT_AFFIRMATION_NUMBER, value).apply()
        get() = sharedPreferences.getInt(CURRENT_AFFIRMATION_NUMBER, 0)

    var lastDayShownAffirmationMills: Long
        set(value) = sharedPreferences.edit().putLong(LAST_DAY_SHOWN_AFFIRMATION_MILLS, value)
            .apply()
        get() = sharedPreferences.getLong(LAST_DAY_SHOWN_AFFIRMATION_MILLS, 0)

    var isUpdatedFrom2VersionTo3: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_updated", value).apply()
        get() = sharedPreferences.getBoolean("is_updated", false)

    var isUpdatedFrom3VersionTo31: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_updated_32111", value).apply()
        get() = sharedPreferences.getBoolean("is_updated_32111", false)

    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val LOCALE = "locale"
        const val IS_PUSH_AVAILABLE = "is_push_available"
        const val IS_FIRST_LAUNCH = "is_first_launch"
        const val PUSH_TOKEN = "push_token"
        const val IS_DARK_THEME = "is_dark_theme"
        const val USER_NAME = "user_name"
        const val IS_INTERESTS_INITIALIZED = "is_interests_initialized"
        const val LAUNCH_COUNT = "launch_count"
        const val FIRST_LAUNCH_DATE = "first_launch_date"

        const val IS_APP_RATED = "is_app_rated"
        const val IS_RATE_APP_LATER = "is_rate_app_later"

        const val CURRENT_AFFIRMATION_NUMBER = "current_affirmation_number"
        const val LAST_DAY_SHOWN_AFFIRMATION_MILLS = "last_day_shown_affirmation_mills"
    }
}