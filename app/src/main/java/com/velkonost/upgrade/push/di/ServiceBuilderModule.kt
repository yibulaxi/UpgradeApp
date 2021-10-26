package com.velkonost.upgrade.push.di

import com.velkonost.upgrade.push.FirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceBuilderModule {
    @ContributesAndroidInjector
    fun inject(): FirebaseMessagingService

}