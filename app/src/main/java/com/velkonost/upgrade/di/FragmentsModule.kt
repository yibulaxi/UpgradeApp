package com.velkonost.upgrade.di

import com.velkonost.upgrade.ui.auth.AuthFragment
import com.velkonost.upgrade.ui.auth.di.AuthModule
import com.velkonost.upgrade.ui.splash.SplashModule
import dagger.Module

@Module(
    includes = [
        SplashModule::class,
        AuthModule::class

    ]
)
class FragmentsModule