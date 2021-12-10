package ru.get.better.ui.auth.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.auth.AuthFragment

@Module
interface AuthModule {
    @ContributesAndroidInjector
    fun inject(): AuthFragment
}