package ru.get.better.push.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.push.FirebaseMessagingService

@Module
interface ServiceBuilderModule {
    @ContributesAndroidInjector
    fun inject(): FirebaseMessagingService

}