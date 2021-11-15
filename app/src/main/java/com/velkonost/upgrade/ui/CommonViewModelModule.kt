package com.velkonost.upgrade.ui

import androidx.lifecycle.ViewModel
import com.velkonost.upgrade.di.scope.ViewModelKey
import com.velkonost.upgrade.ui.auth.AuthViewModel
import com.velkonost.upgrade.ui.splash.SplashViewModel
import com.velkonost.upgrade.ui.welcome.WelcomeViewModel
import com.velkonost.upgrade.vm.BaseViewModel
import com.velkonost.upgrade.vm.UserDiaryViewModel
import com.velkonost.upgrade.vm.UserInterestsViewModel
import com.velkonost.upgrade.vm.UserSettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CommonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun splashViewModel(m: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    fun welcomeViewModel(m: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun authViewModel(m: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BaseViewModel::class)
    fun baseViewModel(m: BaseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserSettingsViewModel::class)
    fun userSettingsViewModel(m: UserSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserDiaryViewModel::class)
    fun userDiaryViewModel(m: UserDiaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserInterestsViewModel::class)
    fun userInterestsViewModel(m: UserInterestsViewModel): ViewModel

}