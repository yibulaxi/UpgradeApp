package ru.get.better.di

import dagger.Module
import dagger.Provides
import ru.get.better.repo.base.AffirmationsRepo
import ru.get.better.repo.nasa.AffirmationsRepoImpl
import ru.get.better.rest.AffirmationService

@Module
class SpecificReposModule {

    @Provides
    fun nasaRepo(
        affirmationService: AffirmationService
    ): AffirmationsRepo = AffirmationsRepoImpl(affirmationService)

}