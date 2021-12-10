package ru.get.better.ui.diary.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.ui.diary.DiaryFragment

@Module
interface DiaryModule {

    @ContributesAndroidInjector
    fun inject(): DiaryFragment

}