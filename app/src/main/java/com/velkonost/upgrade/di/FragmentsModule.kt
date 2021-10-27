package com.velkonost.upgrade.di

import com.velkonost.upgrade.ui.auth.di.AuthModule
import com.velkonost.upgrade.ui.metric.di.MetricModule
import com.velkonost.upgrade.ui.splash.di.SplashModule
import com.velkonost.upgrade.ui.welcome.di.WelcomeModule
import dagger.Module

@Module(
    includes = [
        SplashModule::class,
        AuthModule::class,
        MetricModule::class,
        WelcomeModule::class
    ]
)
class FragmentsModule