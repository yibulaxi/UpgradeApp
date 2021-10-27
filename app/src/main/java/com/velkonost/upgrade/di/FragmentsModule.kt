package com.velkonost.upgrade.di

import com.velkonost.upgrade.ui.achievements.di.AchievementsModule
import com.velkonost.upgrade.ui.auth.di.AuthModule
import com.velkonost.upgrade.ui.metric.di.MetricModule
import com.velkonost.upgrade.ui.settings.di.SettingsModule
import com.velkonost.upgrade.ui.splash.di.SplashModule
import com.velkonost.upgrade.ui.welcome.di.WelcomeModule
import dagger.Module

@Module(
    includes = [
        SplashModule::class,
        AuthModule::class,
        MetricModule::class,
        WelcomeModule::class,
        SettingsModule::class,
        AchievementsModule::class
    ]
)
class FragmentsModule