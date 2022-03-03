package ru.get.better.util

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.ContextThemeWrapper
import java.util.*


object LocaleUtils {
    private var sLocale: Locale? = null
    fun setLocale(locale: Locale?) {
        sLocale = locale
        if (sLocale != null) {
            Locale.setDefault(sLocale)
        }
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        val configuration = Configuration()
        configuration.setLocale(sLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }
}