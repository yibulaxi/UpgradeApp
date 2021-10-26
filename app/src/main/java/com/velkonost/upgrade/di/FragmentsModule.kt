package com.velkonost.upgrade.di

import com.velkonost.upgrade.ui.splash.SplashModule
import dagger.Module

@Module(
    includes = [
        SplashModule::class

    ]
)
class FragmentsModule