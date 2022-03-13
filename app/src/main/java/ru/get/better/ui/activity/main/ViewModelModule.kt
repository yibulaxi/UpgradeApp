package ru.get.better.ui.activity.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.di.SpecificReposModule
import ru.get.better.ui.CommonViewModelModule

@Module(includes = [CommonViewModelModule::class, MainProvidersModule::class, SpecificReposModule::class])
interface ViewModelModule {
    @ContributesAndroidInjector
    fun inject(): MainActivity
}

@Module
interface MainProvidersModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel::class)
//    fun mainViewModel(m: MainViewModel): ViewModel


}