package ru.get.better.di

import dagger.Module
import dagger.Provides
import ru.get.better.di.scope.AppScope
import ru.get.better.repo.base.AffirmationsRepo
import ru.get.better.repo.nasa.AffirmationsRepoImpl

@Module
class ReposModule {
//    @AppScope
//    @Provides
//    fun homeRepo(r: HomeRepoImpl): HomeRepo = r

    @AppScope
    @Provides
    fun nasaRepo(r: AffirmationsRepoImpl): AffirmationsRepo = r
}