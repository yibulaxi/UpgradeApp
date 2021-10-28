package com.velkonost.upgrade.ui.diary.di

import com.velkonost.upgrade.ui.auth.AuthFragment
import com.velkonost.upgrade.ui.diary.DiaryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface DiaryModule {

    @ContributesAndroidInjector
    fun inject(): DiaryFragment

}