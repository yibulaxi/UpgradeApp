package com.velkonost.upgrade.ui.splash.di

import com.velkonost.upgrade.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SplashModule {

    @ContributesAndroidInjector
    fun inject(): SplashFragment

}