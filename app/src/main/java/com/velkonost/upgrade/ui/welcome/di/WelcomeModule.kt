package com.velkonost.upgrade.ui.welcome.di

import com.velkonost.upgrade.ui.splash.SplashFragment
import com.velkonost.upgrade.ui.welcome.WelcomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface WelcomeModule {

    @ContributesAndroidInjector
    fun inject(): WelcomeFragment

}