package com.velkonost.upgrade.ui.splash

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SplashModule {

    @ContributesAndroidInjector
    fun inject(): SplashFragment

}