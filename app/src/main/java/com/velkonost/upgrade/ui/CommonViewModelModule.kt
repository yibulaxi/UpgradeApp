package com.velkonost.upgrade.ui

import androidx.lifecycle.ViewModel
import com.velkonost.upgrade.di.scope.ViewModelKey
import com.velkonost.upgrade.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CommonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun splashViewModel(m: SplashViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun homeViewModel(m: HomeViewModel) : ViewModel

}