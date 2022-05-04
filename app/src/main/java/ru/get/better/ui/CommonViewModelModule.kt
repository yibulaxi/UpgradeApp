package ru.get.better.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.get.better.di.scope.ViewModelKey
import ru.get.better.vm.ArticlesViewModel
import ru.get.better.ui.welcome.WelcomeViewModel
import ru.get.better.vm.*

@Module
interface CommonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    fun welcomeViewModel(m: WelcomeViewModel): ViewModel

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

    @Binds
    @IntoMap
    @ViewModelKey(UserAchievementsViewModel::class)
    fun userAchievementsViewModel(m: UserAchievementsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AffirmationsViewModel::class)
    fun affirmationsViewModel(m: AffirmationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel::class)
    fun articlesViewModel(m: ArticlesViewModel): ViewModel

}