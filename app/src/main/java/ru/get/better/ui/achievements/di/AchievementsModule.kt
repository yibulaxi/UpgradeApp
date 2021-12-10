package ru.get.better.ui.achievements.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.achievements.AchievementsFragment

@Module
interface AchievementsModule {
    @ContributesAndroidInjector
    fun inject(): AchievementsFragment
}