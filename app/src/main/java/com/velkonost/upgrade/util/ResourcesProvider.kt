package com.velkonost.upgrade.util

import android.content.Context
import androidx.annotation.StringRes
import com.velkonost.upgrade.ui.splash.SplashFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject
import javax.inject.Singleton

class ResourcesProvider @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }
}