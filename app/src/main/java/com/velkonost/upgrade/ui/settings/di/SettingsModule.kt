package com.velkonost.upgrade.ui.settings.di

import com.velkonost.upgrade.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingsModule {

    @ContributesAndroidInjector
    fun inject(): SettingsFragment
}