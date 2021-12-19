package ru.get.better.ui.faq.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.faq.FaqFragment

@Module
interface FaqModule {
    @ContributesAndroidInjector
    fun inject(): FaqFragment
}