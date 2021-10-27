package com.velkonost.upgrade.ui.achievements.di

import com.velkonost.upgrade.ui.achievements.AchievementsFragment
import com.velkonost.upgrade.ui.auth.AuthFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AchievementsModule {
    @ContributesAndroidInjector
    fun inject(): AchievementsFragment
}