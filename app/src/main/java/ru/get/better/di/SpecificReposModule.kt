package ru.get.better.di

import dagger.Module
import dagger.Provides
import ru.get.better.repo.base.NasaRepo
import ru.get.better.repo.nasa.NasaRepoImpl
import ru.get.better.rest.NasaService

@Module
class SpecificReposModule {

    @Provides
    fun nasaRepo(
        nasaService: NasaService
    ): NasaRepo = NasaRepoImpl(nasaService)

}