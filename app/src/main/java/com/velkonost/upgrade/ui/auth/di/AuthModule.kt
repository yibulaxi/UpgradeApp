package com.velkonost.upgrade.ui.auth.di

import com.velkonost.upgrade.ui.auth.AuthFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AuthModule {
    @ContributesAndroidInjector
    fun inject(): AuthFragment
}