package com.velkonost.upgrade.util

import android.content.Context
import android.content.SharedPreferences
import com.velkonost.upgrade.App

class Preferences(context: Context) {

    val authToken: String = "123"
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

    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val IS_INTERESTS_INITIALIZED = "is_interests_initialized"
    }
}