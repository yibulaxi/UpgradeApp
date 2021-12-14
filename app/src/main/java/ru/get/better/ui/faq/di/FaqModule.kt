package ru.get.better.ui.faq.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.faq.FaqFragment
import ru.get.better.ui.metric.MetricFragment

@Module
interface FaqModule {
    @ContributesAndroidInjector
    fun inject(): FaqFragment
}