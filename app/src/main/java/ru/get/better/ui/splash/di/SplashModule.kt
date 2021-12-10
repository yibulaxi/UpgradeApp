package ru.get.better.ui.splash.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.splash.SplashFragment

@Module
interface SplashModule {

    @ContributesAndroidInjector
    fun inject(): SplashFragment

}