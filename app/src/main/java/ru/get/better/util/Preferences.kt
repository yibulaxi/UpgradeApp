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

    var locale: String?
        set(value) = sharedPreferences.edit().putString(LOCALE, value).apply()
        get() = sharedPreferences.getString(LOCALE, null)

    var pushToken: String?
        set(value) = sharedPreferences.edit().putString(PUSH_TOKEN, value).apply()
        get() = sharedPreferences.getString(PUSH_TOKEN, null)

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

    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val LOCALE = "locale"
        const val IS_FIRST_LAUNCH = "is_first_launch"
        const val PUSH_TOKEN = "push_token"
        const val USER_NAME = "user_name"
        const val IS_INTERESTS_INITIALIZED = "is_interests_initialized"
    }
}