package com.velkonost.upgrade.ui.metric.di

import com.velkonost.upgrade.ui.metric.MetricFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MetricModule {
    @ContributesAndroidInjector
    fun inject(): MetricFragment
}