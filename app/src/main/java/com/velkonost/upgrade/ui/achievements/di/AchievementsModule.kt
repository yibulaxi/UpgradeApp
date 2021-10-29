package com.velkonost.upgrade.ui.achievements.di

import com.velkonost.upgrade.ui.achievements.AchievementsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AchievementsModule {
    @ContributesAndroidInjector
    fun inject(): AchievementsFragment
}