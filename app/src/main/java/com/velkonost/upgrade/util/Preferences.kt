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

    companion object {
        const val PREF_FILE_NAME = "cv_prefs"
    }
}