package ru.get.better.ui.welcome.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.welcome.WelcomeFragment

@Module
interface WelcomeModule {

    @ContributesAndroidInjector
    fun inject(): WelcomeFragment

}