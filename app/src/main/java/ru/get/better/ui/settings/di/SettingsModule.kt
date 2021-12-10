package ru.get.better.ui.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.settings.SettingsFragment

@Module
interface SettingsModule {

    @ContributesAndroidInjector
    fun inject(): SettingsFragment
}