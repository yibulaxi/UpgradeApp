package ru.get.better.util

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject
import android.R.id
import android.content.res.Configuration
import android.content.res.Resources
import ru.get.better.App
import java.util.*


class ResourcesProvider @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {

        return context.getString(stringResId)
    }

    fun getStringLocale(
        @StringRes stringResId: Int,
        locale: String = App.preferences.locale
    ): String {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(locale))
        return context.createConfigurationContext(config).getText(stringResId).toString()
    }
}