package ru.get.better.di

import dagger.Module
import ru.get.better.ui.achievements.di.AchievementsModule
import ru.get.better.ui.articles.di.ArticlesModule
import ru.get.better.ui.diary.di.DiaryModule
import ru.get.better.ui.faq.di.FaqModule
import ru.get.better.ui.metric.di.MetricModule
import ru.get.better.ui.settings.di.SettingsModule
import ru.get.better.ui.welcome.di.WelcomeModule

@Module(
    includes = [
        MetricModule::class,
        WelcomeModule::class,
        SettingsModule::class,
        AchievementsModule::class,
        DiaryModule::class,
        FaqModule::class,
        ArticlesModule::class
    ]
)
class FragmentsModule