package ru.get.better.repo.base

import io.reactivex.Single
import ru.get.better.model.NasaImg

interface NasaRepo {

    fun getSpaceImg(): Single<NasaImg>
}