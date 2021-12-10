package ru.get.better.ui.metric.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.metric.MetricFragment

@Module
interface MetricModule {
    @ContributesAndroidInjector
    fun inject(): MetricFragment
}