package ru.get.better.ui.articles.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.better.databinding.FragmentAchievementsBinding
import ru.get.better.ui.achievements.AchievementsFragment
import ru.get.better.ui.articles.ArticlesFragment
import ru.get.better.ui.base.BaseFragment

@Module
interface ArticlesModule  {
    @ContributesAndroidInjector
    fun inject(): ArticlesFragment
}